package mobi.wrt.oreader.app.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import by.istin.android.xcore.fragment.XListFragment;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.clients.ClientsFactory;
import mobi.wrt.oreader.app.clients.db.ClientEntity;

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
        return new int[]{R.id.label, R.id.description};
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
