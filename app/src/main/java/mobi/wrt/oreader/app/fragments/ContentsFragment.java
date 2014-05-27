package mobi.wrt.oreader.app.fragments;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashSet;
import java.util.Set;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.fragment.XListFragment;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;
import mobi.wrt.oreader.app.fragments.responders.IContentClick;
import mobi.wrt.oreader.app.image.Displayers;
import mobi.wrt.oreader.app.view.ImagesViewGroup;
import mobi.wrt.oreader.app.view.listeners.FloatHeaderScrollListener;
import mobi.wrt.oreader.app.view.listeners.SwipeToReadListViewTouchListener;
import mobi.wrt.oreader.app.view.utils.SymbolViewUtils;
import mobi.wrt.oreader.app.view.utils.TranslucentUtils;

public class ContentsFragment extends XListFragment {

    public static Fragment newInstance(String meta, String type, String title) {
        Fragment fragment = new ContentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ClientEntity.META, Uri.parse(meta));
        args.putString(ClientEntity.TYPE, type);
        args.putString(ClientEntity.TITLE, title);
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
    private int headerHeight = ContextHolder.get().getResources().getDimensionPixelSize(R.dimen.contents_header_height);

    private Set<Long> alreadyReadIds = new HashSet<Long>();

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        final ListView listView = (ListView) view.findViewById(android.R.id.list);
        final View headerView = View.inflate(getActivity(), R.layout.view_fake_header, null);
        listView.addHeaderView(headerView, null, false);
        final View floatHeaderView = view.findViewById(R.id.header);
        initHeader(floatHeaderView);
        ImageView headerImageView = (ImageView) floatHeaderView.findViewById(R.id.headerBackground);
        ImageLoader.getInstance().displayImage("https://pbs.twimg.com/profile_banners/816653/1398816318/1500x500", headerImageView);
        setOnScrollListViewListener(new FloatHeaderScrollListener(headerView, floatHeaderView, headerHeight, headerHeightMin));
        TranslucentUtils.applyTranslucentPaddingForView(listView, false, false, true);
        SwipeToReadListViewTouchListener touchListener =
                new SwipeToReadListViewTouchListener(
                        listView,
                        new SwipeToReadListViewTouchListener.OnDismissCallback() {

                            @Override
                            public boolean canDismiss(int position) {
                                return !alreadyReadIds.contains(getListAdapter().getItemId(position-1));
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                CursorAdapter listAdapter = (CursorAdapter) getListAdapter();
                                for (int pos : reverseSortedPositions) {
                                    alreadyReadIds.add(getListAdapter().getItemId(pos-1));
                                }
                                listAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        setOnScrollListViewListener(touchListener.makeScrollListener());
    }

    @Override
    public void onPause() {
        super.onPause();
        mClient.markAsRead(true, alreadyReadIds);
    }

    @Override
    protected View onAdapterGetView(SimpleCursorAdapter simpleCursorAdapter, int position, View view) {
        View resultView = super.onAdapterGetView(simpleCursorAdapter, position, view);
        if (alreadyReadIds.contains(simpleCursorAdapter.getItemId(position))) {
            resultView.findViewById(R.id.readMarker).setVisibility(View.VISIBLE);
        } else {
            resultView.findViewById(R.id.readMarker).setVisibility(View.INVISIBLE);
        }
        return resultView;
    }

    private void initHeader(View floatHeaderView) {
        String title = getTitle();
        TextView labelView = (TextView) floatHeaderView.findViewById(R.id.label);
        labelView.setText(title.substring(1));
        ((TextView) floatHeaderView.findViewById(R.id.symbol)).setText(title.substring(0, 1));

        Uri meta = getMeta();
        String encode = meta.getQueryParameter(Subscriptions.WEBSITE);
        Uri parse = Uri.parse(encode);
        String uri = parse.getScheme() + "://"+parse.getHost() +"/favicon.ico";
        ImageView imageView = (ImageView) floatHeaderView.findViewById(R.id.symbolBg);
        ImageLoader.getInstance().displayImage(uri, imageView, Displayers.BITMAP_DISPLAYER_ICON_BG, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                SymbolViewUtils.updateTextColor(view, SymbolViewUtils.DEFAULT_COLOR);
                view.setBackgroundColor(SymbolViewUtils.DEFAULT_COLOR);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                SymbolViewUtils.updateTextColor(view, SymbolViewUtils.DEFAULT_COLOR);
                view.setBackgroundColor(SymbolViewUtils.DEFAULT_COLOR);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                SymbolViewUtils.updateTextColor(view, loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }

        });
    }

    @Override
    public void onListItemClick(Cursor cursor, View v, int position, long id) {
        findFirstResponderFor(IContentClick.class).onContentClick(id, position);
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
        return R.layout.fragment_contents;
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
        return new int[]{R.id.label, R.id.imagesViewGroup, R.id.description, R.id.date};
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

    public String getTitle() {
        return getArguments().getString(ClientEntity.TITLE);
    }
}
