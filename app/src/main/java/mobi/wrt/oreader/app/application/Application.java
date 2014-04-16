package mobi.wrt.oreader.app.application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.error.ErrorHandler;
import by.istin.android.xcore.plugin.uil.ImageLoaderPlugin;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.processor.AuthFeedlyProcessor;
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
        ));
        AuthManagerFactory.initTw("gEu05wPZ3zJTWas5bDf1Ow", "MiDej7peU8wJkf93Rsq9gt8wLiwkXNW8KYsLxFBw");
        registerAppService(new ClientsFactory());

        //TWITTER
        registerAppService(new TwitterDataSource());
        registerAppService(new AuthTwitterProcessor());

        //FEEDLY
        registerAppService(new AuthFeedlyProcessor());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(BITMAP_DISPLAYER_OPTIONS).build();
        addPlugin(new ImageLoaderPlugin(config));
    }
}
