package mobi.wrt.oreader.app.clients.feedly.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import by.istin.android.xcore.annotations.dbEntities;
import by.istin.android.xcore.annotations.dbLong;
import by.istin.android.xcore.annotations.dbString;
import by.istin.android.xcore.gson.GsonPrimitiveJoinerConverter;
import by.istin.android.xcore.utils.StringUtil;

/**
 * Created by Uladzimir_Klyshevich on 4/28/2014.
 */
public class Subscriptions extends BaseEntity {

    @dbLong
    public static final String ADDED = "added";

    @dbLong
    public static final String UPDATED = "updated";

    @dbString
    public static final String SORT_ID = "sortid";

    @dbString
    public static final String TITLE = "title";

    @dbString
    public static final String VISUAL_URL = "visualUrl";

    @dbString
    public static final String WEBSITE = "website";

    @dbString
    public static final String CATEGORIES_JOINED = "categories_joined";

    //TODO refactoring for other converter
    //only for parsing
    @dbEntities(clazz = Object.class, jsonConverter = CategoryConverter.class)
    public static final String CATEGORIES = "categories";

    public static class CategoryConverter extends GsonPrimitiveJoinerConverter {

        @Override
        public void convert(Params params) {
            StringBuilder tagsBuilder = new StringBuilder();
            JsonArray jsonArray = params.getJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement item = jsonArray.get(i);
                tagsBuilder.append(item.getAsJsonObject().get(Category.LABEL).getAsString());
                if (i != jsonArray.size()-1) {
                    tagsBuilder.append(getSplitter());
                }
            }
            String result = tagsBuilder.toString();
            if (!StringUtil.isEmpty(result)) {
                params.getContentValues().put(getEntityKey(), result);
            }
        }

        @Override
        public String getSplitter() {
            return ", ";
        }

        @Override
        public String getEntityKey() {
            return CATEGORIES_JOINED;
        }
    }
}
