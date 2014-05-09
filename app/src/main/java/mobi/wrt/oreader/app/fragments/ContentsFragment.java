package mobi.wrt.oreader.app.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.utils.Log;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.view.ImagesViewGroup;

public class ContentsFragment extends XListFragment {

    public static Fragment newInstance(String meta, String type) {
        Fragment fragment = new ContentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ClientEntity.META, Uri.parse(meta));
        args.putString(ClientEntity.TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    //TODO need for future theme connector
    private ClientsFactory.IClient mClient;
    private ClientsFactory.IClient.IContentsFragmentConnector mContentsFragmentConnector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mClient = ClientsFactory.get(getActivity()).getClient(ClientsFactory.Type.valueOf(getType()));
        mContentsFragmentConnector = mClient.getContentsFragmentConnector(getMeta());
    }

    private int headerHeightMin = ContextHolder.get().getResources().getDimensionPixelSize(R.dimen.contents_header_height_min);

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        final View headerView = View.inflate(getActivity(), R.layout.view_fake_header, null);
        listView.addHeaderView(headerView, null, false);
        final View floatHeaderView = view.findViewById(R.id.header);
        ImageView headerImageView = (ImageView) floatHeaderView.findViewById(R.id.headerBackground);
        ImageLoader.getInstance().displayImage("http://www.desktopict.com/wp-content/uploads/2014/02/great-wallpapers-for-android-2-1024x576.jpg", headerImageView);
        setOnScrollListViewListener(new AbsListView.OnScrollListener() {

            private int last;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int bottom = headerView.getBottom();
                Log.xd(ContentsFragment.this, "top " + headerView.getTop() + " bottom " + bottom);
                int bottomValue = bottom - view.getPaddingTop();
                if (last == bottomValue) {
                    return;
                }
                if (bottomValue >= headerHeightMin) {
                    last = bottomValue;
                    ViewGroup.LayoutParams layoutParams = floatHeaderView.getLayoutParams();
                    layoutParams.height = bottomValue;
                    floatHeaderView.setLayoutParams(layoutParams);
                }
            }
        });
    }

    @Override
    public void onListItemClick(Cursor cursor, View v, int position, long id) {

    }

    @Override
    public long getCacheExpiration() {
        return 1l;
    }

    @Override
    public String getDataSourceKey() {
        return mContentsFragmentConnector.getDataSourceKey(getMeta());
    }

    @Override
    public String getSelection() {
        return mContentsFragmentConnector.getSelection(getMeta());
    }

    @Override
    public String[] getSelectionArgs() {
        return mContentsFragmentConnector.getSelectionArgs(getMeta());
    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_contens;
    }

    @Override
    public Uri getUri() {
        return mContentsFragmentConnector.getUri(getMeta());
    }

    @Override
    public String getUrl() {
        return mContentsFragmentConnector.getUrl(getMeta());
    }

    @Override
    public String getProcessorKey() {
        return mContentsFragmentConnector.getProcessorKey(getMeta());
    }

    @Override
    protected String[] getAdapterColumns() {
        return mContentsFragmentConnector.getAdapterColumns(getMeta());
    }

    @Override
    protected void onPageLoad(int newPage, int totalItemCount) {
        super.onPageLoad(newPage, totalItemCount);
        mContentsFragmentConnector.onPageLoad(this, getMeta(), newPage, totalItemCount);
    }

    @Override
    protected boolean isPagingSupport() {
        return mContentsFragmentConnector.isPagingSupport(getMeta());
    }

    @Override
    protected int[] getAdapterControlIds() {
        return new int[]{R.id.label, R.id.imagesViewGroup, R.id.description};
    }

    @Override
    protected SimpleCursorAdapter.ViewBinder getAdapterViewBinder() {
        return new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.imagesViewGroup) {
                    ImagesViewGroup imagesViewGroup = (ImagesViewGroup) view;
                    //TODO make configurable
                    imagesViewGroup.setDisplayMode(ImagesViewGroup.DisplayMode.CROP);
                    imagesViewGroup.setSrc(mContentsFragmentConnector.getImagesFromContent(cursor));
                    return true;
                }
                return false;
            }

        };
    }

    @Override
    public String getOrder() {
        return mContentsFragmentConnector.getOrder(getMeta());
    }

    @Override
    protected int getAdapterLayout() {
        return R.layout.adapter_content;
    }

    private Uri mMeta;

    public Uri getMeta() {
        if (mMeta == null) {
            mMeta = getArguments().getParcelable(ClientEntity.META);
        }
        return mMeta;
    }

    public String getType() {
        return getArguments().getString(ClientEntity.TYPE);
    }
}
