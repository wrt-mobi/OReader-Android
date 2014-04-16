package mobi.wrt.oreader.app.clients.feedly;

import android.net.Uri;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.preference.PreferenceHelper;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.IAuthManager;
import mobi.wrt.oreader.app.clients.Profile;
import mobi.wrt.oreader.app.clients.feedly.bo.AuthResponse;

public class FeedlyAuthManager implements IAuthManager {

    public static final String CODE = "code";
    public static final String KEY_AUTH_RESPONSE = "feedlyAuthResponse";
    public static final String KEY_LAST_UPDATE = "feedlyLastUpdate";

    @Override
    public void sign(HttpUriRequest request) throws Exception {
        //array( 'Authorization: Bearer ' . $access_key )
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
        FeedlyRequestHelper.token(code, listener, new ISuccess<AuthResponse>() {
            @Override
            public void success(AuthResponse authResponse) {
                if (StringUtil.isEmpty(authResponse.getAccessToken())) {
                    FeedlyRequestHelper.refreshToken(authResponse.getRefreshToken(), listener, new ISuccess<AuthResponse>() {
                        @Override
                        public void success(AuthResponse authResponse) {
                            success.success(new Profile());
                        }
                    });
                } else {
                    success.success(new Profile());
                }
            }
        });
        return true;
    }



    @Override
    public boolean isLogged() {
        return false;
    }

    @Override
    public void exit() {

    }
}
