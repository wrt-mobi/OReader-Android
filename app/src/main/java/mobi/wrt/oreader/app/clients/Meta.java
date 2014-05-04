package mobi.wrt.oreader.app.clients;

import by.istin.android.xcore.utils.UrlBuilder;

public class Meta {

    public static final String SCHEME_VALUE = "oreader";
    public static final String SCHEME = SCHEME_VALUE + UrlBuilder.SCHEME_END;
    public static final String DB_ENTITY = "db_entity";

    public static String buildImageUrl(String value) {
        return UrlBuilder.scheme(SCHEME, value).build();
    }

    public static UrlBuilder buildMeta(String host) {
        return UrlBuilder.scheme(SCHEME, host);
    }
}
