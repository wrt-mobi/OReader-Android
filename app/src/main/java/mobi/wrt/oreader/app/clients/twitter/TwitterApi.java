package mobi.wrt.oreader.app.clients.twitter;

import by.istin.android.xcore.utils.UrlBuilder;

public class TwitterApi {

    public static final String BASE_PATH = "api.twitter.com";

    //path segments
    public static final String VERSION = "1.1";

    public static final UrlBuilder BASE = UrlBuilder.https(BASE_PATH).s(VERSION);
    public static final String DEFAULT_COUNT_VALUE = "20";
    public static final String COUNT_PARAM = "count";

    public static class Account {
        public static final String VERIFY_CREDENTIALS = UrlBuilder.parent(BASE).s("account").s("verify_credentials.json").build();
    }

    public static class Users {
        public static final String SEARCH_QUERY_PARAM = "q";

        public static final UrlBuilder SEARCH = UrlBuilder.parent(BASE).s("users").s("search.json").param(SEARCH_QUERY_PARAM).param("page").param("count");

    }

}
