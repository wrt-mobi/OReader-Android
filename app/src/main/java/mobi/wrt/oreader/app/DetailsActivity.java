package mobi.wrt.oreader.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.CursorModelLoader;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UiUtil;
import by.istin.android.xcore.widget.ViewPagerCursorAdapter;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Content;
import mobi.wrt.oreader.app.helpers.ReadUnreadHelper;
import mobi.wrt.oreader.app.html.MediaContentRecognizer;
import mobi.wrt.oreader.app.html.elements.PageElement;
import mobi.wrt.oreader.app.view.utils.TranslucentUtils;
import mobi.wrt.oreader.app.widget.ArticleAdapter;
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

    private ViewPagerCursorAdapter mPagerAdapter;

    private int mCurrentPosition;

    private ReadUnreadHelper mReadUnreadHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReadUnreadHelper = AppUtils.get(this, ReadUnreadHelper.APP_SERVICE_KEY);
        getSupportActionBar().hide();
        UiUtil.setTranslucentBars(this);
        setContentView(R.layout.activity_details);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!CursorUtils.isEmpty(mCursor)) {
                    mCursor.moveToPosition(position);
                    Long id = CursorUtils.getLong(BaseColumns._ID, mCursor);
                    mReadUnreadHelper.markAsRead(id);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        Intent intent = getIntent();
        mMeta = Uri.parse(intent.getStringExtra(ClientEntity.META));
        mType = intent.getStringExtra(ClientEntity.TYPE);
        mTitle = intent.getStringExtra(ClientEntity.TITLE);
        mCurrentPosition = intent.getIntExtra(EXTRA_POSITION, 0);
        mClient = ClientsFactory.get(getActivity()).getClient(ClientsFactory.Type.valueOf(mType));
        mContentsFragmentConnector = mClient.getContentsFragmentConnector(mMeta);
        CursorLoaderFragmentHelper.onActivityCreated(this, savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mClient.markAsRead(true, mReadUnreadHelper.getIds());
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
        if (mPagerAdapter == null) {
            mPagerAdapter = new ViewPagerCursorAdapter(this, mCursor, R.layout.view_page) {
                @Override
                public void init(View container, Cursor cursor) {
                    Long id = CursorUtils.getLong(BaseColumns._ID, cursor);
                    String summaryContent = CursorUtils.getString(Content.SUMMARY_CONTENT, cursor);
                    String contentContent = CursorUtils.getString(Content.CONTENT_CONTENT, cursor);
                    String date = CursorUtils.getString(Content.PUBLISHED_AS_STRING, cursor);
                    final ListView listView = (ListView) container.findViewById(R.id.listView);
                    View headerView = View.inflate(DetailsActivity.this, R.layout.view_page_header, null);
                    TextView textView = (TextView) headerView.findViewById(R.id.label);
                    textView.setText(CursorUtils.getString(Content.TITLE, cursor));
                    TextView dateTextView = (TextView) headerView.findViewById(R.id.date);
                    dateTextView.setText(date);
                    TranslucentUtils.applyTranslucentPaddingForView(listView, true, false, true);
                    listView.addHeaderView(headerView, null, false);
                    if (StringUtil.isEmpty(contentContent)) {
                        contentContent = summaryContent;
                    }
                    List<PageElement> elements = MediaContentRecognizer.recognize(contentContent);
                    listView.setAdapter(new ArticleAdapter(DetailsActivity.this, listView, R.layout.adapter_page_element_text, elements));
                }

                @Override
                protected void onViewItemCreated(View containerItem) {
                    super.onViewItemCreated(containerItem);
                }
            };
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setCurrentItem(mCurrentPosition);
            //huck to make faster switch
            mViewPager.setVisibility(View.VISIBLE);
            mCursor.moveToPosition(mCurrentPosition);
            mReadUnreadHelper.markAsRead(CursorUtils.getLong(BaseColumns._ID, mCursor));
        } else {
            mPagerAdapter.swapCursor(mCursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        if (mPagerAdapter != null) {
            mPagerAdapter.swapCursor(null);
        }
    }

    @Override
    public void onCursorLoaderStartLoading() {

    }

    @Override
    public void onCursorLoaderStopLoading() {

    }
}
