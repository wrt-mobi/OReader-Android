package mobi.wrt.oreader.app.clients.twitter;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;

import java.util.List;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.ContentUtils;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.twitter.datasource.TwitterDataSource;
import mobi.wrt.oreader.app.clients.twitter.db.SearchTwitterProfile;
import mobi.wrt.oreader.app.clients.twitter.processor.SearchTwitterProfileProcessor;

public class TwitterRequestHelper {

    public static void searchProfile(final Context context, final String q, final ISuccess<String> success) {
        Core.ExecuteOperationBuilder<ContentValues[]> userItemExecuteOperationBuilder = new Core.ExecuteOperationBuilder<ContentValues[]>();
        String requestDataUri = TwitterApi.Users.SEARCH.build(StringUtil.encode(q), "1", "1");
        DataSourceRequest pDataSourceRequest = new DataSourceRequest(requestDataUri);
        //TODO something wrong with cache
        pDataSourceRequest.setForceUpdateData(true);
        pDataSourceRequest.setCacheable(true);
        pDataSourceRequest.setCacheExpiration(DateUtils.DAY_IN_MILLIS);
        userItemExecuteOperationBuilder
                .setDataSourceRequest(pDataSourceRequest)
                .setProcessorKey(SearchTwitterProfileProcessor.APP_SERVICE_KEY)
                .setDataSourceKey(TwitterDataSource.APP_SERVICE_KEY)
                .setSuccess(new ISuccess<ContentValues[]>() {
                    @Override
                    public void success(ContentValues[] result) {
                        if (result != null && result.length > 0) {
                            success.success(result[0].getAsString(SearchTwitterProfile.PROFILE_BACKGROUND_IMAGE_URL));
                        }
                    }
                })
                .setDataSourceServiceListener(new Core.SimpleDataSourceServiceListener() {

                    @Override
                    public void onCached(Bundle resultData) {
                        super.onCached(resultData);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                List<ContentValues> entities = ContentUtils.getEntities(context,
                                        new String[]{SearchTwitterProfile.PROFILE_BACKGROUND_IMAGE_URL},
                                        SearchTwitterProfile.class,
                                        SearchTwitterProfile.SEARCH_QUERY + "=?",
                                        new String[]{q});
                                if (entities != null && entities.size() > 0) {
                                    success.success(entities.get(0).getAsString(SearchTwitterProfile.PROFILE_BANNER_URL));
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onDone(Bundle resultData) {

                    }

                    @Override
                    public void onError(final Exception exception) {
                        super.onError(exception);
                    }

                });
        Core core = Core.get(context);
        Core.IExecuteOperation<ContentValues[]> executeOperation = userItemExecuteOperationBuilder.build();
        core.execute(executeOperation);
    }

}
