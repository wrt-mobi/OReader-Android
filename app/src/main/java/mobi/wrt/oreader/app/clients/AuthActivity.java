package mobi.wrt.oreader.app.clients;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.ui.DialogBuilder;
import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.R;

public class AuthActivity extends ActionBarActivity implements IAuthManager.IAuthListener {

	public static final String INTENT_PROFILE = "profile";

	private static final String TAG = AuthActivity.class.getSimpleName();
	
	private static final String INTENT_TYPE = "type";

	public static void get(Activity activity, AuthManagerFactory.Type type) {
		Intent intent = new Intent(activity, AuthActivity.class);
		intent.putExtra(INTENT_TYPE, type.ordinal());
		activity.startActivityForResult(intent, 1);
	}
	
	private WebView mWebView;
	
	private IAuthManager mSocialManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
		mSocialManager = AuthManagerFactory.getManager(AuthManagerFactory.Type.values()[getIntent().getIntExtra(INTENT_TYPE, 0)]);
		mWebView = new WebView(this);
		setContentView(mWebView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.setWebViewClient(new AuthWebViewClient());
		mSocialManager.getAuthorizationUrl(this, new ISuccess<String>() {

			@Override
			public void success(String url) {
				mWebView.loadUrl(url);				
			}
			
		});
	}

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onError(Exception e) {
        DialogBuilder.simple(this, getString(R.string.error), e.getMessage(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    private class AuthWebViewClient extends WebViewClient {

    	/**
		 * Instantiates a new twitter login web view client.
		 *
		 */
		public AuthWebViewClient() {
			super();
			
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "page started " + url);
			showLoading();
			if (mSocialManager.isRedirect(AuthActivity.this, url)) {
				view.setVisibility(View.INVISIBLE);
				Log.d(TAG, "Parsing url" + url);
			}
		}

		
		
		/* (non-Javadoc)
		 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
		 */
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "overr " + url);
	        if (mSocialManager.isRedirect(AuthActivity.this, url)) {
	        	Log.d(TAG, "overr redr");
	        	view.setVisibility(View.INVISIBLE);
	        	Log.d(TAG, "Parsing url" + url);
	        	if (!mSocialManager.proceedRedirectURL(AuthActivity.this, url, success)) {
					hideLoading();
				}
	        	return true;
	        } else {
	        	 //view.loadUrl(url);
	        	 return false;
	        }
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			//showLoadingDialog("Error: " + description);
			Log.d(TAG, "error " + failingUrl);
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.d(TAG, "finish " + url);
			if (url.contains("&amp;")) {
				url = url.replace("&amp;", "&");
				view.loadUrl(url);
				return;
			}
			if (!mSocialManager.proceedRedirectURL(AuthActivity.this, url, success)) {
                hideLoading();
			}
		}

	}
    
    private ISuccess<Profile> success = new ISuccess<Profile>() {

		@Override
		public void success(Profile profile) {
			Intent intent = new Intent();
			intent.putExtra(INTENT_PROFILE, profile);
			setResult(RESULT_OK, intent);
			AuthActivity.this.finish();
		}
	};
}
