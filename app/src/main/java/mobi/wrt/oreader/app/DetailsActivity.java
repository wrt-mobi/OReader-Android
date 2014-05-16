package mobi.wrt.oreader.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.CursorModelLoader;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UiUtil;
import by.istin.android.xcore.widget.ViewPagerCursorAdapter;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Content;
import mobi.wrt.oreader.app.fragments.ContentsFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailsActivity extends ActionBarActivity implements
        CursorLoaderFragmentHelper.ICursorLoaderFragmentHelper,
        CursorModelLoader.ILoading {

    public static final String EXTRA_POSITION = "position";

    public static final int LOADER_ID = 1;

    private ViewPager mViewPager;

    private ClientsFactory.IClient mClient;

    private ClientsFactory.IClient.IContentsFragmentConnector mContentsFragmentConnector;

    private Uri mMeta;

    private String mType;

    private String mTitle;

    private Cursor mCursor;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiUtil.setTranslucentStatus(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_details);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        Intent intent = getIntent();
        mMeta = Uri.parse(intent.getStringExtra(ClientEntity.META));
        mType = intent.getStringExtra(ClientEntity.TYPE);
        mTitle = intent.getStringExtra(ClientEntity.TITLE);

        mClient = ClientsFactory.get(getActivity()).getClient(ClientsFactory.Type.valueOf(mType));
        mContentsFragmentConnector = mClient.getContentsFragmentConnector(mMeta);
        CursorLoaderFragmentHelper.onActivityCreated(this, savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public Uri getUri() {
        return mContentsFragmentConnector.getUri(mMeta);
    }

    @Override
    public int getLoaderId() {
        return LOADER_ID;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public String[] getProjection() {
        return null;
    }

    @Override
    public String getSelection() {
        return mContentsFragmentConnector.getSelection(mMeta);
    }

    @Override
    public String[] getSelectionArgs() {
        return mContentsFragmentConnector.getSelectionArgs(mMeta);
    }


    @Override
    public String getOrder() {
        return mContentsFragmentConnector.getOrder(mMeta);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public CursorModel.CursorModelCreator getCursorModelCreator() {
        return CursorModel.CursorModelCreator.DEFAULT;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> cursorLoader = CursorLoaderFragmentHelper.onCreateLoader(this, this, id, args);
        return cursorLoader;
    }

    private class DetailsCursorModel extends CursorModel {

        public DetailsCursorModel(Cursor cursor) {
            super(cursor);
        }

        public DetailsCursorModel(Cursor cursor, boolean isMoveToFirst) {
            super(cursor, isMoveToFirst);
        }

        @Override
        public void doInBackground(Context context) {
            super.doInBackground(context);
        }
    }

    /**
     * Document document = Jsoup.parse(summaryContent);
     Elements imgs = document.select("img[height]");
     for (Element img : imgs) {
     //TODO calculate image height and width to adapted screen dimension
     }
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        if (CursorUtils.isEmpty(mCursor)) {

        }
        if (mPagerAdapter == null) {
            mPagerAdapter = new ViewPagerCursorAdapter(this, mCursor, R.layout.view_webview) {
                @Override
                public void init(View container, Cursor cursor) {
                    WebView webView = (WebView) container.findViewById(R.id.webView);
                    String summaryContent = CursorUtils.getString(Content.SUMMARY_CONTENT, cursor);
                    String contentContent = CursorUtils.getString(Content.CONTENT_CONTENT, cursor);
                    webView.loadDataWithBaseURL(StringUtil.EMPTY, StringUtil.isEmpty(contentContent) ? summaryContent : contentContent, "text/html", "UTF-8", StringUtil.EMPTY);
                }

                @Override
                protected void onViewItemCreated(View containerItem) {
                    super.onViewItemCreated(containerItem);
                    WebView webView = (WebView) containerItem.findViewById(R.id.webView);
                    /*webView.setOnTouchListener(new View.OnTouchListener() {

                        public boolean onTouch(View v, MotionEvent event) {
                            return (event.getAction() == MotionEvent.ACTION_MOVE);
                        }
                    });*/
                }
            };
            mViewPager.setAdapter(mPagerAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }

    @Override
    public void onCursorLoaderStartLoading() {

    }

    @Override
    public void onCursorLoaderStopLoading() {

    }
}
