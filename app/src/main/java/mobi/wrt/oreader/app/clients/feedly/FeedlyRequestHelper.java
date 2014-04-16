package mobi.wrt.oreader.app.clients.feedly;

import android.app.Activity;
import android.os.Bundle;

import by.istin.android.xcore.Core;
import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.clients.IAuthManager;
import mobi.wrt.oreader.app.clients.feedly.bo.AuthResponse;
import mobi.wrt.oreader.app.clients.feedly.processor.AuthFeedlyProcessor;
import mobi.wrt.oreader.app.clients.twitter.bo.UserItem;

public class FeedlyRequestHelper {

    static void token(String code, final IAuthManager.IAuthListener listener, final ISuccess<AuthResponse> success) {
        Core.ExecuteOperationBuilder<UserItem> userItemExecuteOperationBuilder = new Core.ExecuteOperationBuilder<UserItem>();
        final Activity activity = listener.getActivity();
        String requestDataUri = HttpAndroidDataSource.DefaultHttpRequestBuilder.getUrl(FeedlyApi.Auth.TOKEN.build(StringUtil.encode(code)), HttpAndroidDataSource.DefaultHttpRequestBuilder.Type.POST);
        userItemExecuteOperationBuilder
                .setActivity(activity)
                .setDataSourceRequest(new DataSourceRequest(requestDataUri))
                .setProcessorKey(AuthFeedlyProcessor.APP_SERVICE_KEY)
                .setDataSourceKey(HttpAndroidDataSource.SYSTEM_SERVICE_KEY)
                .setSuccess(new ISuccess<AuthResponse>() {
                    @Override
                    public void success(AuthResponse userItem) {
                        success.success(userItem);
                    }
                })
                .setDataSourceServiceListener(new Core.SimpleDataSourceServiceListener() {
                    @Override
                    public void onDone(Bundle resultData) {

                    }

                    @Override
                    public void onError(final Exception exception) {
                        super.onError(exception);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(exception);
                            }
                        });
                    }
                });
        Core.get(activity).execute(userItemExecuteOperationBuilder.build());
    }

    static void refreshToken(String refreshToken, final IAuthManager.IAuthListener listener, final ISuccess<AuthResponse> success) {
        Core.ExecuteOperationBuilder<UserItem> userItemExecuteOperationBuilder = new Core.ExecuteOperationBuilder<UserItem>();
        final Activity activity = listener.getActivity();
        String requestDataUri = HttpAndroidDataSource.DefaultHttpRequestBuilder.getUrl(FeedlyApi.Auth.REFRESH_TOKEN_BUILDER.build(StringUtil.encode(refreshToken)), HttpAndroidDataSource.DefaultHttpRequestBuilder.Type.POST);
        userItemExecuteOperationBuilder
                .setActivity(activity)
                .setDataSourceRequest(new DataSourceRequest(requestDataUri))
                .setProcessorKey(AuthFeedlyProcessor.APP_SERVICE_KEY)
                .setDataSourceKey(HttpAndroidDataSource.SYSTEM_SERVICE_KEY)
                .setSuccess(new ISuccess<AuthResponse>() {
                    @Override
                    public void success(AuthResponse userItem) {
                        success.success(userItem);
                    }
                })
                .setDataSourceServiceListener(new Core.SimpleDataSourceServiceListener() {
                    @Override
                    public void onDone(Bundle resultData) {

                    }

                    @Override
                    public void onError(final Exception exception) {
                        super.onError(exception);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(exception);
                            }
                        });
                    }
                });
        Core.get(activity).execute(userItemExecuteOperationBuilder.build());
    }

}
