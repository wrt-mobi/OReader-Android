package mobi.wrt.oreader.app.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Random;

import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.CursorUtils;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.fragments.responders.IClientEntityClick;
import mobi.wrt.oreader.app.image.Displayers;
import mobi.wrt.oreader.app.view.utils.TranslucentUtils;

public class HomeFragmentMagazine extends XListFragment {

    private boolean isUnread() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return false;
        } else {
            return arguments.getBoolean(HomeFragmentExpandableListView.UNREAD);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        TranslucentUtils.applyTranslucentPaddingForView((android.view.ViewGroup) view.findViewById(android.R.id.list), false, false, true);
    }

    @Override
    public void onListItemClick(Cursor cursor, View v, int position, long id) {
        String meta = CursorUtils.getString(ClientEntity.META, cursor);
        String type = CursorUtils.getString(ClientEntity.TYPE, cursor);
        String title = CursorUtils.getString(ClientEntity.TITLE, cursor);
        String icon = CursorUtils.getString(ClientEntity.ICON, cursor);
        findFirstResponderFor(IClientEntityClick.class).onClientEntityClick(v, icon, meta, type, title);
    }

    @Override
    protected View onAdapterGetView(SimpleCursorAdapter simpleCursorAdapter, int position, View view) {
        View resultView = super.onAdapterGetView(simpleCursorAdapter, position, view);
        resultView.findViewById(R.id.icon).setViewName("photo"+position);
        return resultView;
    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_home_magazine;
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
    public String getSelection() {
        if (isUnread()) {
            return ClientEntity.COUNT + " > 0";
        }
        return super.getSelection();
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

    public static Fragment newInstance(boolean isHideRead) {
        Fragment fragment = new HomeFragmentMagazine();
        Bundle args = new Bundle();
        args.putBoolean(HomeFragmentExpandableListView.UNREAD, isHideRead);
        fragment.setArguments(args);
        return fragment;
    }
}
