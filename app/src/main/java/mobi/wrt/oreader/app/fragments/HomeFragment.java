package mobi.wrt.oreader.app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Random;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.UiUtil;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.StreamActivity;
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
        Intent intent = new Intent(getActivity(), StreamActivity.class);
        intent.putExtra(ClientEntity.META, CursorUtils.getString(ClientEntity.META, cursor));
        intent.putExtra(ClientEntity.TYPE, CursorUtils.getString(ClientEntity.TYPE, cursor));
        startActivity(intent);
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

    private static final int[] COLORS = {0xffF3B500,0xffFF0605, 0xff2811FD, 0xff3FB4B3, 0xffCB4437};

    private Random random = new Random();

    @Override
    protected boolean setAdapterViewText(TextView v, String value) {
        if (v.getId() == R.id.label) {
            v.setText(value.toUpperCase());
            v.setBackgroundColor(COLORS[random.nextInt(COLORS.length-1)]);
            return true;
        } else {
            return super.setAdapterViewText(v, value);
        }
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
