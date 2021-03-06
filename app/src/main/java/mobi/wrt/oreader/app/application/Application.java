package mobi.wrt.oreader.app.application;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

import by.istin.android.xcore.CoreApplication;
import by.istin.android.xcore.error.ErrorHandler;
import by.istin.android.xcore.plugin.uil.ImageLoaderPlugin;
import by.istin.android.xcore.provider.IDBContentProviderSupport;
import by.istin.android.xcore.provider.impl.DBContentProviderFactory;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.source.impl.http.HttpAndroidDataSource;
import mobi.wrt.oreader.app.clients.AuthManagerFactory;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.feedly.FeedlyModule;
import mobi.wrt.oreader.app.clients.feedly.exception.FeedlyAuthException;
import mobi.wrt.oreader.app.clients.flickr.FlickrModule;
import mobi.wrt.oreader.app.clients.twitter.TwitterModule;
import mobi.wrt.oreader.app.content.ContentProvider;
import mobi.wrt.oreader.app.helpers.ReadUnreadHelper;
import mobi.wrt.oreader.app.image.CustomImageDownloader;
import mobi.wrt.oreader.app.image.Displayers;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Application extends CoreApplication {

    public static final String FONTS_FOLDER = "fonts/";
    public static final String DEFAULT_FONT = "Roboto-Light.ttf";
    public static final String DEFAULT_FONT_AB = "RobotoCondensed-Bold.ttf";
    public static final String DEFAULT_FONT_PATH = FONTS_FOLDER + DEFAULT_FONT;

    @Override
    public void onCreate() {
        super.onCreate();
        L.disableLogging();
        CalligraphyConfig.initDefault(DEFAULT_FONT_PATH);
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

        IDBContentProviderSupport dbContentProvider = DBContentProviderFactory.getDefaultDBContentProvider(this, ContentProvider.ENTITIES);

        registerAppService(new ClientsFactory());
        registerAppService(new AuthManagerFactory());
        registerAppService(new ReadUnreadHelper());

        //TWITTER
        TwitterModule.onCreate(this, dbContentProvider);

        //FEEDLY
        FeedlyModule.onCreate(this, dbContentProvider);

        //FLICKR
        FlickrModule.onCreate(this, dbContentProvider);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(Displayers.BITMAP_DISPLAYER_OPTIONS)
                .imageDownloader(new CustomImageDownloader(this)).build();
        addPlugin(new ImageLoaderPlugin(config));
    }
}
