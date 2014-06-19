package mobi.wrt.oreader.app.clients.twitter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.preference.PreferenceHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.IAuthManager;
import mobi.wrt.oreader.app.clients.Profile;
import mobi.wrt.oreader.app.clients.twitter.bo.UserItem;
import mobi.wrt.oreader.app.clients.twitter.datasource.TwitterDataSource;
import mobi.wrt.oreader.app.clients.twitter.processor.AuthTwitterProcessor;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class TwitterAuthManager implements IAuthManager {

    private static final String TAG = TwitterAuthManager.class.getSimpleName();

	private static final String DENIED = "denied";
    public static final String PREF_TWITTER_PROFILE = "pref_twitter_profile";

    private OAuthConsumer mConsumer;

	private OAuthProvider mProvider;

    private UserItem mUserItem;

	public TwitterAuthManager() {
        mConsumer = new CommonsHttpOAuthConsumer(
                    TwitterOAuthConstants.CONSUMER_KEY,
                    TwitterOAuthConstants.CONSUMER_SECRET);
        mProvider = new CommonsHttpOAuthProvider(
					TwitterOAuthConstants.REQUEST_URL,
					TwitterOAuthConstants.ACCESS_URL,
					TwitterOAuthConstants.AUTHORIZE_URL);
	}

	public boolean isDenied(String url) {
		return url.contains(DENIED);
	}

    @Override
	public void sign(HttpUriRequest request) throws Exception{
        try {
            if (mUserItem == null) {
                restore();
            }
            if (mUserItem != null && mConsumer.getTokenSecret() == null) {
                mConsumer.setTokenWithSecret(mUserItem.getToken(), mUserItem.getTokenSecret());
                //setRetrieveAccessToken(mUserItem.getToken(), mUserItem.getTokenSecret());
            }
            mConsumer.sign(request);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
            throw e;
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
            throw e;
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
            throw e;
        } /*catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
            throw e;
        }*/
    }

	public void setRetrieveAccessToken(String oauthVerifier)
			throws OAuthMessageSignerException, OAuthNotAuthorizedException,
			OAuthExpectationFailedException, OAuthCommunicationException {
		mProvider.retrieveAccessToken(mConsumer, oauthVerifier);
	}

	@Override
	public void getAuthorizationUrl(final IAuthListener authListener,
			final ISuccess<String> success) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Activity activity = authListener.getActivity();
                Runnable result;
                try {
                    final String url = mProvider.retrieveRequestToken(mConsumer, TwitterOAuthConstants.REDIRECT_URL);
                    if (!StringUtil.isEmpty(url)) {
                        result = new Runnable() {
                            @Override
                            public void run() {
                                success.success(url);
                            }
                        };
                    } else {
                        throw new Exception("can't get login url");
                    }
                } catch (final Exception e) {
                    result = new Runnable() {
                        @Override
                        public void run() {
                            authListener.onError(e);
                        }
                    };
                }
                activity.runOnUiThread(result);
            }

        }).start();
	}

	@Override
	public boolean isRedirect(IAuthListener delegate, String url) {
		return url.startsWith(TwitterOAuthConstants.REDIRECT_URL) && !isDenied(url);
	}
	
	@Override
	public boolean proceedRedirectURL(final IAuthListener delegate, String url,
			final ISuccess<Profile> success) {
        Uri uri = Uri.parse(url);
        final String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);
		final String token = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
        if (!StringUtil.isEmpty(verifier) && !StringUtil.isEmpty(token)) {
            final Handler handler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setRetrieveAccessToken(verifier);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Core.ExecuteOperationBuilder<UserItem> userItemExecuteOperationBuilder = new Core.ExecuteOperationBuilder<UserItem>();
                                userItemExecuteOperationBuilder
                                        .setActivity(delegate.getActivity())
                                        .setDataSourceRequest(new DataSourceRequest(TwitterApi.Account.VERIFY_CREDENTIALS))
                                        .setProcessorKey(AuthTwitterProcessor.APP_SERVICE_KEY)
                                        .setDataSourceKey(TwitterDataSource.APP_SERVICE_KEY)
                                        .setSuccess(new ISuccess<UserItem>() {
                                            @Override
                                            public void success(UserItem userItem) {
                                                Profile profile = new Profile();
                                                Long uid = userItem.getUid();
                                                if (uid != null) {
                                                    profile.setId(uid.toString());
                                                }
                                                profile.setFirstName(userItem.getName());
                                                profile.setNickname(userItem.getNickname());
                                                profile.setToken(verifier);
                                                profile.setType(AuthManagerFactory.Type.TWITTER);
                                                success.success(profile);
                                                userItem.set(UserItem.TOKEN_SECRET, mConsumer.getTokenSecret());
                                                userItem.set(UserItem.TOKEN, mConsumer.getToken());
                                                saveSettings(userItem);
                                                mUserItem = userItem;
                                            }
                                        })
                                        .setDataSourceServiceListener(new Core.SimpleDataSourceServiceListener() {
                                            @Override
                                            public void onDone(Bundle resultData) {

                                            }

                                            @Override
                                            public void onError(final Exception exception) {
                                                super.onError(exception);
                                                delegate.getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        delegate.onError(exception);
                                                    }
                                                });
                                            }
                                        });
                                Core.get(delegate.getActivity()).execute(userItemExecuteOperationBuilder.build());
                            }
                        });
                    } catch (Exception e) {
                        delegate.onError(e);
                    }
                }
            }).start();
		} else {
			return false;
		}
		return true;
	}

    private void saveSettings(UserItem userItem) {
        PreferenceHelper.set(PREF_TWITTER_PROFILE, userItem != null ? userItem.toString() : null);
    }

    @Override
	public boolean isLogged() {
        restore();
		return mUserItem != null;
	}

    private void restore() {
        if (mUserItem != null) {
            return;
        }
        String savedValue = PreferenceHelper.getString(PREF_TWITTER_PROFILE, StringUtil.EMPTY);
        if (!StringUtil.isEmpty(savedValue)) {
            try {
                mUserItem = new UserItem(savedValue);
            } catch (JSONException e) {
                //is not possible
            }
        }
    }

    @Override
	public void exit() {
        saveSettings(null);
        mUserItem = null;
	}

}
