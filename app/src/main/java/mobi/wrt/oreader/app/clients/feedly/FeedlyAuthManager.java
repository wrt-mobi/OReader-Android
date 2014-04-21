package mobi.wrt.oreader.app.clients.feedly;

import android.net.Uri;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;

import java.io.IOException;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.preference.PreferenceHelper;
import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.IAuthManager;
import mobi.wrt.oreader.app.clients.Profile;
import mobi.wrt.oreader.app.clients.feedly.bo.AuthResponse;
import mobi.wrt.oreader.app.clients.feedly.exception.FeedlyAuthException;

public class FeedlyAuthManager implements IAuthManager {

    public static final String CODE = "code";
    public static final String KEY_AUTH_RESPONSE = "feedlyAuthResponse";
    public static final String KEY_CODE = "feedlyCode";
    public static final String KEY_LAST_UPDATE = "feedlyLastUpdate";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    @Override
    public void sign(HttpUriRequest request) throws IOException {
        AuthResponse authResponse = getAuthResponse();
        long lastUpdateToken = getLastUpdateToken();
        String code = PreferenceHelper.getString(KEY_CODE, null);
        if (authResponse == null || lastUpdateToken == -1l || StringUtil.isEmpty(code)) {
            throw new FeedlyAuthException();
        }
        Long expiresIn = authResponse.getExpiresIn()*1000l;
        if (System.currentTimeMillis() - lastUpdateToken > expiresIn) {
            try {
                refreshToken(true, null, null, code);
            } catch (Exception e) {
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                //TODO log in crashlytics
            }
        }
        request.addHeader(HEADER_AUTHORIZATION, authResponse.getTokenType() + " " + authResponse.getAccessToken());
    }

    public static AuthResponse getAuthResponse() {
        String string = PreferenceHelper.getString(KEY_AUTH_RESPONSE, null);
        if (StringUtil.isEmpty(string)) {
            return null;
        }
        try {
            return new AuthResponse(string);
        } catch (JSONException e) {
            //TODO is not possible?
            return null;
        }
    }

    public static long getLastUpdateToken() {
        return PreferenceHelper.getLong(KEY_LAST_UPDATE, -1l);
    }

    public static void save(AuthResponse authResponse) {
        PreferenceHelper.set(KEY_AUTH_RESPONSE, authResponse.toString());
        PreferenceHelper.set(KEY_LAST_UPDATE, System.currentTimeMillis());
    }


    @Override
    public void getAuthorizationUrl(IAuthListener listener, ISuccess<String> success) {
        success.success(FeedlyApi.Auth.AUTH);
    }

    @Override
    public boolean isRedirect(IAuthListener listener, String url) {
        return url.startsWith(FeedlyApi.Auth.REDIRECT_URI_VALUE);
    }

    @Override
    public boolean proceedRedirectURL(final IAuthListener listener, String url, final ISuccess<Profile> success) {
        if (!isRedirect(listener, url)) {
            return false;
        }
        String code = Uri.parse(url).getQueryParameter(CODE);
        PreferenceHelper.set(KEY_CODE, code);
        try {
            refreshToken(false, listener, success, code);
        } catch (Exception e) {
            //can't happen
        }
        return true;
    }

    private void refreshToken(final boolean isSync, final IAuthListener listener, final ISuccess<Profile> success, String code) throws Exception {
        final Holder<Exception> exceptionHolder = new Holder<Exception>();
        FeedlyRequestHelper.token(isSync, code, listener, new ISuccess<AuthResponse>() {
            @Override
            public void success(AuthResponse authResponse) {
                if (StringUtil.isEmpty(authResponse.getAccessToken())) {
                    try {
                        FeedlyRequestHelper.refreshToken(isSync, authResponse.getRefreshToken(), listener, new ISuccess<AuthResponse>() {
                            @Override
                            public void success(AuthResponse authResponse) {
                                if (success != null) {
                                    success.success(new Profile());
                                }
                            }
                        });
                    } catch (Exception e) {
                        exceptionHolder.set(e);
                    }
                } else {
                    if (success != null) {
                        success.success(new Profile());
                    }
                }
            }
        });
        if (!exceptionHolder.isNull()) {
            throw exceptionHolder.get();
        }
    }


    @Override
    public boolean isLogged() {
        return false;
    }

    @Override
    public void exit() {

    }
}
