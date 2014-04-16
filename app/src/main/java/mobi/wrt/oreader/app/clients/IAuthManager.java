package mobi.wrt.oreader.app.clients;


import android.app.Activity;

import org.apache.http.client.methods.HttpUriRequest;

import by.istin.android.xcore.callable.ISuccess;

public interface IAuthManager {

    public static interface IAuthListener {
        void showLoading();
        void hideLoading();
        Activity getActivity();

        void onError(Exception e);
    }

    /*	public static String getOauthVerifierFromUrl(String url) {
            return url.substring(url.indexOf(TwitterConstants.OAUTH_VERIFIER)
                    + TwitterConstants.OAUTH_VERIFIER.length());
        }
    */
    void sign(HttpUriRequest request) throws Exception;

    void getAuthorizationUrl(IAuthListener listener, ISuccess<String> success);
	
	boolean isRedirect(IAuthListener listener, String url);
	
	boolean proceedRedirectURL(IAuthListener listener, String url, ISuccess<Profile> success);
	
	boolean isLogged();
	
	void exit();
	
}
