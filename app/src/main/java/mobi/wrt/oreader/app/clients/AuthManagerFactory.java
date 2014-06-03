package mobi.wrt.oreader.app.clients;


import mobi.wrt.oreader.app.clients.feedly.FeedlyAuthManager;
import mobi.wrt.oreader.app.clients.twitter.TwitterAuthManager;

public class AuthManagerFactory {

	private static String sFbClientId;
	
	private static String sFbScope;
	
	private static String sVkClientId;
	
	private static String sVkScope;
	
	private static String sMailRuId;
	
	private static String sMailRuToken;
	
	private static String sConsumerKey;
	
	private static String sConsumerSecret;
	
	public static void initFb(String fbClientId, String fbScope) {
		sFbClientId = fbClientId;
		sFbScope = fbScope;
	}
	
	public static void initVk(String vkClientId, String vkScope) {
		sVkClientId = vkClientId;
		sVkScope = vkScope;
	}
	
	public static void initMailRu(String mailruClientId, String mailruToken) {
		sMailRuId = mailruClientId;
		sMailRuToken = mailruToken;
	}
	
	public static void initTw(String consumerKey, String consumerSecret) {
		sConsumerKey = consumerKey;
		sConsumerSecret = consumerSecret;
	}
	
	public static enum Type {
		FACEBOOK, TWITTER, VK, FEEDLY
	}

    private static TwitterAuthManager sTwitterAuthManager;

	public static IAuthManager getManager(Type type) {
		switch (type) {
		case FACEBOOK:
			//return new FbManager(sFbClientId, sFbScope);
		case TWITTER:
            if (sTwitterAuthManager == null) {
                sTwitterAuthManager = new TwitterAuthManager(sConsumerKey, sConsumerSecret);
            }
			return sTwitterAuthManager;
		case VK:
			//return new VkManager(sVkClientId, sVkScope);
		case FEEDLY:
            return new FeedlyAuthManager();
			//return new VkManager(sVkClientId, sVkScope);

		default:
			return null;
		}
	}
	
}
