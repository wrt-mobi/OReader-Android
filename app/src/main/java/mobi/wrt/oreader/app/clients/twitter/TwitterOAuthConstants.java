package mobi.wrt.oreader.app.clients.twitter;

import mobi.wrt.oreader.app.BuildConfig;

public final class TwitterOAuthConstants {

	public static final String OAuthHelper = "++OAuthHelper++";

	public static final String REDIRECT_URL = "http://twconnect.com/success";

	public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";

	public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";

	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

    public static final String CONSUMER_KEY = BuildConfig.TWITTER_CONSUMER_KEY;

    public static final String CONSUMER_SECRET = BuildConfig.TWITTER_CONSUMER_SECRET;
}
