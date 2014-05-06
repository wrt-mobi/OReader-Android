package mobi.wrt.oreader.app.clients.feedly.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import by.istin.android.xcore.annotations.JsonSubJSONObject;
import by.istin.android.xcore.annotations.dbBoolean;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.db.IDBConnection;
import by.istin.android.xcore.db.impl.DBHelper;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.HashUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.feedly.FeedlyApi;
import mobi.wrt.oreader.app.image.IContentImage;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class Content extends BaseEntity {

    @dbLong
    public static final String PUBLISHED = "published";

    @dbString
    @JsonSubJSONObject
    @SerializedName("summary:content")
    public static final String SUMMARY_CONTENT = "summary_content";

    @dbString
    @JsonSubJSONObject
    @SerializedName("visual:url")
    public static final String VISUAL_URL = "visual_url";

    @dbString
    @JsonSubJSONObject
    @SerializedName("visual:height")
    public static final String VISUAL_HEIGHT = "visual_height";

    @dbString
    @JsonSubJSONObject
    @SerializedName("visual:width")
    public static final String VISUAL_WIDTH = "visual_width";

    @dbString
    public static final String TITLE = "title";

    @dbBoolean
    public static final String UNREAD = "unread";

    //LOCAL
    @dbString
    public static final String STREAM_ID = "stream_id";

    @dbString
    public static final String STRIP_CONTENT = "strip_content";

    @dbString
    public static final String IMAGES = "content_images";

    @Override
    public long generateId(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, ContentValues contentValues) {
        return HashUtils.generateId(contentValues.getAsString(ID_AS_STRING), dataSourceRequest.getParam(FeedlyApi.Streams.STREAM_ID));
    }

    @Override
    public void onBeforeListUpdate(DBHelper dbHelper, IDBConnection db, DataSourceRequest dataSourceRequest, int position, ContentValues contentValues) {
        if (dataSourceRequest != null) {
            String paramCount = dataSourceRequest.getParam(_COUNT);
            if (StringUtil.isEmpty(paramCount)) {
                contentValues.put(POSITION, position);
            } else {
                contentValues.put(POSITION, position + Integer.parseInt(paramCount));
            }
            contentValues.put(STREAM_ID, dataSourceRequest.getParam(FeedlyApi.Streams.STREAM_ID));
        }
        Long id = contentValues.getAsLong(ID);
        if (id == null) {
            id = generateId(dbHelper, db, dataSourceRequest, contentValues);
            contentValues.put(ID, id);
        }
        String summaryContent = contentValues.getAsString(SUMMARY_CONTENT);
        Log.startAction("htmlContentParse");
        Document document = Jsoup.parse(summaryContent);
        Elements imgs = document.select("img");
        //TODO create images view that will draw 1+ images
        //TODO create Image interface with height width and url
        contentValues.put(STRIP_CONTENT, stripHtml(summaryContent));
        contentValues.put(IMAGES, imgs.toString());
        Log.endAction("htmlContentParse");
        //TODO need to recognize type of content text/image with text/images with text/video and text/audio with text and prepare fields for view
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html.replaceAll("<[^>]*>", StringUtil.EMPTY)).toString().trim();
    }

    public static List<IContentImage> getImages(Cursor cursor) {
        String images = CursorUtils.getString(IMAGES, cursor);
        if (StringUtil.isEmpty(images)) {
            return null;
        }
        Document document = Jsoup.parse(images);
        Elements imgs = document.select("img");
        List<IContentImage> contentImages = new ArrayList<IContentImage>(imgs.size());
        for (final Element element : imgs) {
            contentImages.add(new IContentImage() {
                @Override
                public String getUrl() {
                    return element.attr("src");
                }

                @Override
                public Integer getWidth() {
                    String width = element.attr("width");
                    if (StringUtil.isEmpty(width) && !TextUtils.isDigitsOnly(width)) {
                        return null;
                    }
                    try {
                        return Integer.parseInt(width);
                    } catch (NumberFormatException e) {
                        return null;
                    }

                }

                @Override
                public Integer getHeight() {
                    String height = element.attr("height");
                    if (StringUtil.isEmpty(height) && !TextUtils.isDigitsOnly(height)) {
                        return null;
                    }
                    try {
                        return Integer.parseInt(height);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            });
        }
        return contentImages;
    }

}
