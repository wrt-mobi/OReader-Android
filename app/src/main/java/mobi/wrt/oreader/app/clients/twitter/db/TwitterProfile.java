/**
 * 
 */
package mobi.wrt.oreader.app.clients.twitter.db;

import android.provider.BaseColumns;

import com.google.gson.annotations.SerializedName;

import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbInteger;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;


public class TwitterProfile implements BaseColumns {

    @dbLong
    @SerializedName(value = "id")
    public static final String ID = _ID;

    @dbString
    public static final String ID_STR = "id_str";

    @dbString
    public static final String NAME = "name";

    @dbString
    public static final String SCREEN_NAME = "screen_name";

    @dbString
    public static final String LOCATION = "location";

    @dbString
    public static final String DESCRIPTION = "description";

    @dbString
    public static final String URL = "url";

    @dbBoolean
    public static final String PROTECTED = "protected";

    @dbLong
    public static final String FOLLOWERS_COUNT = "followers_count";

    @dbLong
    public static final String FRIENDS_COUNT = "friends_count";

    @dbLong
    public static final String LISTED_COUNT = "listed_count";

    @dbString
    public static final String CREATED_AT = "created_at";

    @dbLong
    public static final String FAVOURITES_COUNT = "favourites_count";

    @dbInteger
    public static final String UTC_OFFSET = "utc_offset";

    @dbString
    public static final String TIME_ZONE = "time_zone";

    @dbBoolean
    public static final String GEO_ENABLED = "geo_enabled";

    @dbBoolean
    public static final String VERIFIED = "verified";

    @dbLong
    public static final String STATUSES_COUNT = "statuses_count";

    @dbString
    public static final String LANG = "lang";

    @dbBoolean
    public static final String CONTRIBUTORS_ENABLED = "contributors_enabled";

    @dbBoolean
    public static final String IS_TRANSLATOR = "is_translator";

    @dbBoolean
    public static final String IS_TRANSLATION_ENABLED = "is_translation_enabled";

    @dbString
    public static final String PROFILE_BACKGROUND_COLOR = "profile_background_color";

    @dbString
    public static final String PROFILE_BACKGROUND_IMAGE_URL = "profile_background_image_url";

    @dbString
    public static final String PROFILE_BACKGROUND_IMAGE_URL_HTTPS = "profile_background_image_url_https";

    @dbBoolean
    public static final String PROFILE_BACKGROUND_TILE = "profile_background_tile";

    @dbString
    public static final String PROFILE_IMAGE_URL = "profile_image_url";

    @dbString
    public static final String PROFILE_IMAGE_URL_HTTPS = "profile_image_url_https";

    @dbString
    public static final String PROFILE_BANNER_URL = "profile_banner_url";

    @dbString
    public static final String PROFILE_LINK_COLOR = "profile_link_color";

    @dbString
    public static final String PROFILE_SIDEBAR_BORDER_COLOR = "profile_sidebar_border_color";

    @dbString
    public static final String PROFILE_SIDEBAR_FILL_COLOR = "profile_sidebar_fill_color";

    @dbString
    public static final String PROFILE_TEXT_COLOR = "profile_text_color";

    @dbBoolean
    public static final String PROFILE_USE_BACKGROUND_IMAGE = "profile_use_background_image";

    @dbBoolean
    public static final String DEFAULT_PROFILE = "default_profile";

    @dbBoolean
    public static final String DEFAULT_PROFILE_IMAGE = "default_profile_image";

    @dbBoolean
    public static final String FOLLOWING = "following";

    @dbBoolean
    public static final String FOLLOW_REQUEST_SENT = "follow_request_sent";

    @dbBoolean
    public static final String NOTIFICATIONS = "notifications";

    //        "status":{"created_at":"Sun Jun 08 13:18:05 +0000 2014","id":475627826616340480,"id_str":"475627826616340480","text":"Picfair Raises $520K To Take On Getty With An Image Marketplace http:\/\/t.co\/5M9GYeWStx by @ingridlunden","source":"\u003ca href=\"http:\/\/10up.com\" rel=\"nofollow\"\u003e10up Publish Tweet\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":73,"favorite_count":29,"entities":{"hashtags":[],"symbols":[],"urls":[{"url":"http:\/\/t.co\/5M9GYeWStx","expanded_url":"http:\/\/bit.ly\/UmofKF","display_url":"bit.ly\/UmofKF","indices":[64,86]}],"user_mentions":[{"screen_name":"ingridlunden","name":"Ingrid Lunden","id":21662638,"id_str":"21662638","indices":[90,103]}]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"lang":"en"},

//"entities":{"url":{"urls":[{"url":"http:\/\/t.co\/FQzFJNIg8e","expanded_url":"http:\/\/techcrunch.com","display_url":"techcrunch.com","indices":[0,22]}]},"description":{"urls":[]}},

}
