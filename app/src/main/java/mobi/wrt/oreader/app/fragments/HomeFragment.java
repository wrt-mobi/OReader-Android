package mobi.wrt.oreader.app.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.provider.ModelContract;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.image.Displayers;

public class HomeFragment extends XListFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
    }

    @Override
    public void onListItemClick(Cursor cursor, View v, int position, long id) {

    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public Uri getUri() {
        return ModelContract.getUri(ClientEntity.class);
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getProcessorKey() {
        return null;
    }

    @Override
    protected boolean setAdapterViewImage(ImageView v, String value) {
        ImageLoader.getInstance().displayImage(value, v, Displayers.BITMAP_DISPLAYER_BLUR_OPTIONS);
        return true;
    }

    @Override
    protected String[] getAdapterColumns() {
        return new String[]{ClientEntity.ICON, ClientEntity.TITLE, ClientEntity.TYPE, ClientEntity.COUNT_AS_STRING};
    }

    @Override
    protected int[] getAdapterControlIds() {
        return new int[]{R.id.icon, R.id.label, R.id.clientType, R.id.count};
    }

    @Override
    public String getOrder() {
        return ClientEntity.STARRED + " ASC, " + ClientEntity.RATE + " ASC";
    }

    @Override
    protected int getAdapterLayout() {
        return R.layout.adapter_home_grid;
    }

}
