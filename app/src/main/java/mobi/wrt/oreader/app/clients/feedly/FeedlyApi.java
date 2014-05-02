package mobi.wrt.oreader.app.clients.feedly;

import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UrlBuilder;
import mobi.wrt.oreader.app.BuildConfig;

public class FeedlyApi {

    public static final String BASE_PATH = BuildConfig.FEEDLY_BASE_PATH;

    //path segments
    public static final String VERSION = "v3";

    public static final UrlBuilder BASE = UrlBuilder.https(BASE_PATH).s(VERSION);
    public static final String DEFAULT_COUNT_VALUE = "20";
    public static final String COUNT_PARAM = "count";

    public static class Auth {

        public static final String REDIRECT_URI_VALUE = "http://localhost";
        private static final String CLIENT_ID_VALUE = BuildConfig.FEEDLY_CLIENT_ID_VALUE;
        private static final String CLIENT_SECRET_VALUE = BuildConfig.FEEDLY_CLIENT_SECRET_VALUE;

        //params
        private static final String CLIENT_SECRET = "client_secret";
        private static final String CLIENT_ID = "client_id";
        private static final String REDIRECT_URI = "redirect_uri";
        private static final String GRANT_TYPE = "grant_type";
        private static final String STATE = "state";
        private static final String REFRESH_TOKEN_VALUE = "refresh_token";
        private static final String CODE = "code";
        private static final String REVOKE_TOKEN_VALUE = "revoke_token";
        private static final String GRAND_TYPE_AUTHORIZATION_CODE = "authorization_code";
        private static final String RESPONSE_TYPE = "response_type";
        private static final String SCOPE = "scope";
        private static final String SCOPE_VALUE = "https://cloud.feedly.com/subscriptions";

        //path segments
        private static final String AUTH_VALUE = "auth";
        private static final String TOKEN_VALUE = "token";

        private static final UrlBuilder BASE_AUTH = UrlBuilder.parent(BASE).s(AUTH_VALUE);

        public static final String AUTH = UrlBuilder.parent(BASE_AUTH)
                .s(AUTH_VALUE)
                .param(CLIENT_ID, CLIENT_ID_VALUE)
                .param(CLIENT_SECRET, CLIENT_SECRET_VALUE)
                .param(REDIRECT_URI, REDIRECT_URI_VALUE)
                .param(RESPONSE_TYPE, CODE)
                .param(SCOPE, SCOPE_VALUE)
                .build();

        private static final UrlBuilder BASE_TOKEN_BUILDER = UrlBuilder.parent(BASE_AUTH)
                .s(TOKEN_VALUE)
                .param(CLIENT_ID, CLIENT_ID_VALUE)
                .param(CLIENT_SECRET, CLIENT_SECRET_VALUE)
                .param(REDIRECT_URI, REDIRECT_URI_VALUE);

        public static final UrlBuilder TOKEN =
                UrlBuilder.parent(BASE_TOKEN_BUILDER)
                        .param(STATE, StringUtil.EMPTY)
                        .param(GRANT_TYPE, GRAND_TYPE_AUTHORIZATION_CODE)
                        .param(CODE);

        public static final UrlBuilder REFRESH_TOKEN_BUILDER =
                UrlBuilder.parent(BASE_TOKEN_BUILDER)
                        .param(GRANT_TYPE, REFRESH_TOKEN_VALUE)
                        .param(REFRESH_TOKEN_VALUE);

        public static final UrlBuilder REVOKE_TOKEN_BUILDER =
                UrlBuilder.parent(BASE_TOKEN_BUILDER)
                        .param(GRANT_TYPE, REVOKE_TOKEN_VALUE)
                        .param(REFRESH_TOKEN_VALUE);
    }

    public static class Categories {
        public static final String PATH = UrlBuilder.parent(BASE).s("categories").build();
        public static final UrlBuilder CONTENTS = UrlBuilder.parent(BASE)
                .s("streams")
                .s("contents")
                .param(COUNT_PARAM, DEFAULT_COUNT_VALUE)
                //user%2Fde2328c6-dcf7-4aa4-b24c-48d79676bf63%2Fcategory%2Fmarketing
                .param("streamId")
                //from     continuation in response or from id in last content item
                .param("continuation")
                ;
    }

    public static class Subscriptions {
        public static final String PATH = UrlBuilder.parent(BASE).s("subscriptions").build();
    }

    public static class Markers {
        public static final String PATH = UrlBuilder.parent(BASE).s("markers").s("counts").build();
    }

}
