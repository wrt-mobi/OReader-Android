package mobi.wrt.oreader.app.clients;

import android.app.Activity;
import android.content.Intent;

public class ClientsFactory {

    public static interface IClient {

        void performLogin(Activity activity);

        void handleLoginResult(Activity activity, int requestCode, int resultCode, Intent data);
    }

    private class EmptyClient implements IClient {

        @Override
        public void performLogin(Activity activity) {

        }

        @Override
        public void handleLoginResult(Activity activity, int requestCode, int resultCode, Intent data) {

        }

    }

    public static enum Type {
        FEEDLY, VK, FACEBOOK, TWITTER;
    }

    public IClient getClient(Type type) {
        EmptyClient emptyClient = new EmptyClient();
        switch (type) {
            case FEEDLY:
                return emptyClient;
            case VK:
                return emptyClient;
            case FACEBOOK:
                return emptyClient;
            case TWITTER:
                return emptyClient;
        }
        throw new IllegalArgumentException("unknown type");
    }

}
