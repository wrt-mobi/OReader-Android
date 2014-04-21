package mobi.wrt.oreader.app.clients.twitter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.IAuthManager;
import mobi.wrt.oreader.app.clients.Profile;
import mobi.wrt.oreader.app.clients.twitter.bo.UserItem;
import mobi.wrt.oreader.app.clients.twitter.datasource.TwitterDataSource;
import mobi.wrt.oreader.app.clients.twitter.processor.AuthTwitterProcessor;
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

	private OAuthConsumer consumer;

	private OAuthProvider provider;

	private static TwitterAuthManager instanse;

	public TwitterAuthManager(String consumerKey, String consumerSecret) {
			consumer = new CommonsHttpOAuthConsumer(
					consumerKey,
					consumerSecret);
			provider = new CommonsHttpOAuthProvider(
					TwitterOAuthConstants.REQUEST_URL,
					TwitterOAuthConstants.ACCESS_URL,
					TwitterOAuthConstants.AUTHORIZE_URL);
	}

	public static TwitterAuthManager getInstanse() {
		return instanse;
	}


/*	private void saveActiveUser(Account user) {
		CustomLog.logD(TAG, "saveActiveUser");
		SharedPreferences.Editor editor = mContext.getSharedPreferences(
				ApplicationConstants.SHARED_PREFERENSE, Context.MODE_PRIVATE)
				.edit();
		try {
			editor.putString(ApplicationConstants.ARG_ACTIVE_USER_NAME,
					ObjectSerializer.serialize((Serializable) user));
		} catch (IOException e) {
			CustomLog.logE(TAG, "error when serialize user " + e.getMessage());
		}
		editor.commit();
	}

	public void restoreToken(Account user) throws IOException,
			ClassNotFoundException {
		CustomLog.logD(TAG, "restoreToken");
		saveActiveUser(user);
		consumer.setTokenWithSecret(user.getToken(), user.getTokenSecret());

	}
*/

	public boolean isDenied(String url) {
		return url.contains(DENIED);
	}

	public boolean isAuthorizeUrl(String url) {
		return url.startsWith(TwitterOAuthConstants.AUTHORIZE_URL);
	}

	public boolean isAuthorizeFinish(String url) {
		return url.startsWith(TwitterOAuthConstants.AUTHORIZE_URL)
				&& !url.startsWith(TwitterOAuthConstants.AUTHORIZE_URL + "?");
	}

	public boolean isAuthorizeUrlToken(String url) {
		return url.startsWith(TwitterOAuthConstants.AUTHORIZE_URL + "?");
	}

/*	public static String getOauthVerifierFromUrl(String url) {
		return url.substring(url.indexOf(TwitterConstants.OAUTH_VERIFIER)
				+ TwitterConstants.OAUTH_VERIFIER.length());
	}
*/
    @Override
	public void sign(HttpUriRequest request)
			throws IOException {
        try {
            consumer.sign(request);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
    }

	/*private Account getActiveUser() {
		CustomLog.logD(TAG, "getActiveUser");
		if (mContext == null) {
			CustomLog.logE(TAG, "context == null ");
		}
		String serializeUser = mContext.getSharedPreferences(
				ApplicationConstants.SHARED_PREFERENSE, Context.MODE_PRIVATE)
				.getString(ApplicationConstants.ARG_ACTIVE_USER_NAME, "");
		try {
			return (Account) ObjectSerializer.deserialize(serializeUser);
		} catch (IOException e) {
			CustomLog.logE(TAG, "error when deserialize active user ", e);
		} catch (ClassNotFoundException e) {
			CustomLog.logE(TAG, "error when deserialize active user ", e);
		}

		return null;
	}*/

	public void setRetrieveAccessToken(String oauthVerifier)
			throws OAuthMessageSignerException, OAuthNotAuthorizedException,
			OAuthExpectationFailedException, OAuthCommunicationException {
		provider.retrieveAccessToken(consumer, oauthVerifier);
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
                    final String url = provider.retrieveRequestToken(consumer, TwitterOAuthConstants.REDIRECT_URL);
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
	
	public static final String OAUTH_VERIFIER = "oauth_verifier=";

	@Override
	public boolean proceedRedirectURL(final IAuthListener delegate, String url,
			final ISuccess<Profile> success) {
		final String verifier = Uri.parse(url).getQueryParameter("oauth_verifier");
		final String token = Uri.parse(url).getQueryParameter("oauth_token");
		if (!StringUtil.isEmpty(verifier) && !StringUtil.isEmpty(token)) {
            Core.ExecuteOperationBuilder<UserItem> userItemExecuteOperationBuilder = new Core.ExecuteOperationBuilder<UserItem>();
            userItemExecuteOperationBuilder
                    .setActivity(delegate.getActivity())
                    .setDataSourceRequest(new DataSourceRequest("https://api.twitter.com/1.1/account/verify_credentials.json"))
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
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean isLogged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}

}
