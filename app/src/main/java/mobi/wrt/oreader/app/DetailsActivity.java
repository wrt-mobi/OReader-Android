package mobi.wrt.oreader.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.jsoup.nodes.Element;

import java.util.List;

import by.istin.android.xcore.fragment.CursorLoaderFragmentHelper;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.model.CursorModelLoader;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UiUtil;
import by.istin.android.xcore.widget.ViewPagerCursorAdapter;
import by.istin.android.xcore.widget.XArrayAdapter;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Content;
import mobi.wrt.oreader.app.html.MediaContentRecognizer;
import mobi.wrt.oreader.app.html.elements.MediaElement;
import mobi.wrt.oreader.app.html.elements.PageElement;
import mobi.wrt.oreader.app.html.elements.TextElement;
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
            mPagerAdapter = new ViewPagerCursorAdapter(this, mCursor, R.layout.view_page) {
                @Override
                public void init(View container, Cursor cursor) {
                    Long id = CursorUtils.getLong(BaseColumns._ID, cursor);
                    String summaryContent = CursorUtils.getString(Content.SUMMARY_CONTENT, cursor);
                    String contentContent = CursorUtils.getString(Content.CONTENT_CONTENT, cursor);
                    final ListView listView = (ListView) container.findViewById(R.id.listView);
                    TextView textView = (TextView) container.findViewById(R.id.label);
                    textView.setText(CursorUtils.getString(Content.TITLE, cursor));
                    if (StringUtil.isEmpty(contentContent)) {
                        contentContent = summaryContent;
                    }
                    List<PageElement> elements = MediaContentRecognizer.recognize(contentContent);
                    listView.setAdapter(new XArrayAdapter<PageElement>(DetailsActivity.this, R.layout.adapter_page_element_text, elements) {

                        @Override
                        public int getViewTypeCount() {
                            return 2;
                        }

                        @Override
                        public int getItemViewType(int position) {
                            PageElement item = getItem(position);
                            if (item instanceof TextElement) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }

                        protected int getResource(int position) {
                            int itemViewType = getItemViewType(position);
                            if (itemViewType == 0) {
                                return R.layout.adapter_page_element_text;
                            }
                            return R.layout.adapter_page_element_image;
                        }

                        @Override
                        protected void bindView(int position, PageElement item, View view, ViewGroup parent) {
                            if (item instanceof TextElement) {
                                ((TextView) view).setText(((TextElement)item).getText(), TextView.BufferType.SPANNABLE);
                            } else {
                                Element element = ((MediaElement) item).getElement();
                                ImageView imageView = (ImageView) view;
                                if (element.tag().getName().equalsIgnoreCase("img")) {
                                    imageView.setImageBitmap(null);
                                    ImageLoader.getInstance().displayImage(element.attr("src"), imageView, new SimpleImageLoadingListener() {

                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                                            if (layoutParams == null) {
                                                return;
                                            }
                                            int imageHeight = (int) (((double) listView.getWidth() * (double) loadedImage.getHeight()) / (double) loadedImage.getWidth());
                                            layoutParams.width = listView.getWidth();
                                            layoutParams.height = imageHeight;
                                            view.setLayoutParams(layoutParams);
                                        }

                                    });
                                } else {
                                    imageView.setImageBitmap(null);
                                }
                            }
                        }
                    });
                }

                @Override
                protected void onViewItemCreated(View containerItem) {
                    super.onViewItemCreated(containerItem);
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
