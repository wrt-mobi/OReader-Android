package mobi.wrt.oreader.app.application;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.IOException;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.error.ErrorHandler;
import by.istin.android.xcore.plugin.uil.ImageLoaderPlugin;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import by.istin.android.xcore.source.impl.http.exception.IOStatusException;
import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.datasource.FeedlyDataSource;
import mobi.wrt.oreader.app.clients.feedly.exception.FeedlyAuthException;
import mobi.wrt.oreader.app.clients.feedly.processor.AuthFeedlyProcessor;
import mobi.wrt.oreader.app.clients.feedly.processor.TestStringProcessor;
import mobi.wrt.oreader.app.clients.twitter.datasource.TwitterDataSource;
import mobi.wrt.oreader.app.clients.twitter.processor.AuthTwitterProcessor;

public class Application extends CoreApplication {

    public static DisplayImageOptions BITMAP_DISPLAYER_OPTIONS = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .delayBeforeLoading(300)
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .displayer(new SimpleBitmapDisplayer())
            .build();


    @Override
    public void onCreate() {
        super.onCreate();
        registerAppService(new HttpAndroidDataSource(
                        new HttpAndroidDataSource.DefaultHttpRequestBuilder(),
                        new HttpAndroidDataSource.DefaultResponseStatusHandler())
        );
        registerAppService(new ErrorHandler(
                "Error",
                "Check your internet connection",
                "Server error",
                "Developer error",
                "istin2007@gmail.com"
        ){
            @Override
            public ErrorType getErrorType(Exception exception) {
                if (exception instanceof FeedlyAuthException) {
                    return ErrorType.UNKNOWN;
                }
                return super.getErrorType(exception);
            }

            @Override
            protected void onUnknownError(final FragmentActivity activity, final DataSourceRequest dataSourceRequest, final Exception exception, ErrorType type, Runnable clearRunnable) {
                if (exception instanceof FeedlyAuthException) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Application.this, "feedly auth failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        AuthManagerFactory.initTw("gEu05wPZ3zJTWas5bDf1Ow", "MiDej7peU8wJkf93Rsq9gt8wLiwkXNW8KYsLxFBw");
        registerAppService(new ClientsFactory());

        //TWITTER
        registerAppService(new TwitterDataSource());
        registerAppService(new AuthTwitterProcessor());

        //FEEDLY
        registerAppService(new AuthFeedlyProcessor());
        registerAppService(new FeedlyDataSource());
        registerAppService(new TestStringProcessor());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(BITMAP_DISPLAYER_OPTIONS).build();
        addPlugin(new ImageLoaderPlugin(config));
    }
}
