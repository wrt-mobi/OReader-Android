package mobi.wrt.oreader.app.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import by.istin.android.xcore.ContextHolder;
import by.istin.android.xcore.fragment.XFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.ContentUtils;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;
import mobi.wrt.oreader.app.fragments.responders.IClientEntityClick;
import mobi.wrt.oreader.app.image.Displayers;

public class HomeFragmentExpandableListView extends XFragment {

    public static final int DEFAULT_COLOR = 0xffCB4437;
    private AnimatedExpandableListView mListView;
    private ExpandableListAdapter mAdapter;

    private static final String UNREAD = "extra_unread";

    public static Fragment newInstance(boolean unread) {
        Fragment fragment = new HomeFragmentExpandableListView();
        Bundle args = new Bundle();
        args.putBoolean(UNREAD, unread);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isUnread() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return false;
        } else {
            return arguments.getBoolean(UNREAD);
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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (AnimatedExpandableListView) view.findViewById(android.R.id.list);
        mListView.setGroupIndicator(null);
        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (mListView.isGroupExpanded(groupPosition)) {
                    mListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    mListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ContentValues contentValues = mAdapter.getChild(groupPosition, childPosition);
                String meta = contentValues.getAsString(ClientEntity.META);
                String type = contentValues.getAsString(ClientEntity.TYPE);
                findFirstResponderFor(IClientEntityClick.class).onClientEntityClick(meta, type);
                return true;
            }
        });
    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_home_expandable_list;
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
    protected String[] getAdapterColumns() {
        return new String[]{ClientEntity.TITLE};
    }

    @Override
    protected int[] getAdapterControlIds() {
        return new int[]{R.id.label};
    }

    @Override
    public String getSelection() {
        if (isUnread()) {
            return "(" + ClientEntity.RATE + " = " + ClientEntity.Rate.FOLDER.ordinal() + " OR " + ClientEntity.CATEGORIES + " IS NULL) AND " + ClientEntity.COUNT + " > 0";
        } else {
            return ClientEntity.RATE + " = " + ClientEntity.Rate.FOLDER.ordinal() + " OR " + ClientEntity.CATEGORIES + " IS NULL";
        }
    }

    @Override
    public String getOrder() {
        return ClientEntity.STARRED + " ASC, " + ClientEntity.RATE + " ASC";
    }

    @Override
    public CursorModel.CursorModelCreator getCursorModelCreator() {
        return new CursorModel.CursorModelCreator() {
            @Override
            public CursorModel create(Cursor cursor) {
                return new ExpandableCursorModel(cursor);
            }
        };
    }

    @Override
    protected void onLoadFinished(Cursor cursor) {
        if (mAdapter == null) {
            mAdapter = new ExpandableListAdapter(getActivity());
            mAdapter.setData((ExpandableCursorModel) cursor);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.setData((ExpandableCursorModel) cursor);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onLoaderReset() {
        if (mAdapter != null) {
            mAdapter.setData(null);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ExpandableCursorModel extends CursorModel {

        private ConcurrentHashMap<Long, List<ContentValues>> mCache = new ConcurrentHashMap<Long, List<ContentValues>>();

        public ExpandableCursorModel(Cursor cursor) {
            super(cursor);
        }

        public ExpandableCursorModel(Cursor cursor, boolean isMoveToFirst) {
            super(cursor, isMoveToFirst);
        }

        @Override
        public void doInBackground(Context context) {
            super.doInBackground(context);
            if (isEmpty()) {
                return;
            }
            for (int i = 0; i < size(); i++) {
                CursorModel model = get(i);
                Integer rate = model.getInt(ClientEntity.RATE);
                if (rate != ClientEntity.Rate.FOLDER.ordinal()) {
                    continue;
                }
                String categories = model.getString(ClientEntity.TITLE);
                if (!StringUtil.isEmpty(categories)) {
                    String selection = ClientEntity.CATEGORIES + " like ? ";
                    if (isUnread()) {
                        selection = selection + "AND " + ClientEntity.COUNT + " > 0";
                    }
                    List<ContentValues> values = ContentUtils.getEntities(context, ClientEntity.class, selection, "%"+categories+"%");
                    if (values != null) {
                        mCache.put(model.getLong(ClientEntity.ID), values);
                    }
                }
            }
        }

        public List<ContentValues> getItems() {
            return mCache.get(getLong(ClientEntity.ID));
        }

        @Override
        public void close() {
            super.close();
            mCache.clear();
        }
    }

    private class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter implements ImageLoadingListener{

        private int firstCharacterWidth = ContextHolder.get().getResources().getDimensionPixelSize(R.dimen.symbol_size);

        private int countsWidth = ContextHolder.get().getResources().getDimensionPixelSize(R.dimen.counts_width);

        private LayoutInflater inflater;

        private ExpandableCursorModel items;

        public ExpandableListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(ExpandableCursorModel items) {
            this.items = items;
        }

        @Override
        public ContentValues getChild(int groupPosition, int childPosition) {
            return getGroup(groupPosition).getItems().get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ContentValues item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_stream_item, parent, false);
            }
            initChild(convertView, item);
            return convertView;
        }

        public void initChild(View convertView, ContentValues item) {
            String title = item.getAsString(ClientEntity.TITLE);
            String meta = item.getAsString(ClientEntity.META);
            String count = item.getAsString(ClientEntity.COUNT_AS_STRING);

            initChild(convertView, title, meta, count);
        }

        public void initChild(View convertView, String title, String meta, String count) {
            TextView counts = (TextView) convertView.findViewById(R.id.counts);
            counts.setText(count);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.symbolBg);
            TextView labelView = (TextView) convertView.findViewById(R.id.label);
            int mListViewWidth = mListView.getWidth();
            int maxpixels = mListViewWidth - countsWidth - mListView.getPaddingLeft() - mListView.getPaddingRight() - firstCharacterWidth;
            labelView.setMaxWidth(maxpixels);

            labelView.setText(title.substring(1));
            String encode = Uri.parse(meta).getQueryParameter(Subscriptions.WEBSITE);
            Uri parse = Uri.parse(encode);
            String uri = parse.getScheme() + "://"+parse.getHost() +"/favicon.ico";
            ((TextView) convertView.findViewById(R.id.symbol)).setText(title.substring(0,1));
            ImageLoader.getInstance().displayImage(uri, imageView, Displayers.BITMAP_DISPLAYER_ICON_BG, this);
        }

        public void updateTextColor(View view, Bitmap bitmap) {
            int pixel = bitmap.getPixel(0, 0);
            updateTextColor(view, pixel);
        }

        public void updateTextColor(View view, int pixel) {
            TextView symbolView = (TextView) ((ViewGroup) view.getParent()).findViewById(R.id.symbol);
            if (symbolView == null) {
                return;
            }
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            int red = Color.red(pixel);
            double y = 0.2126*red + 0.7152*green + 0.0722*blue;
            symbolView.setTextColor(y < 128 ? Color.WHITE : Color.BLACK);
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            ExpandableCursorModel group = getGroup(groupPosition);
            if (group == null) {
                return 0;
            }
            List<ContentValues> items = group.getItems();
            if (items == null) {
                return 0;
            }
            return items.size();
        }

        @Override
        public ExpandableCursorModel getGroup(int groupPosition) {
            return ((ExpandableCursorModel)items.get(groupPosition));
        }

        @Override
        public int getGroupCount() {
            if (items == null) {
                return 0;
            }
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ExpandableCursorModel item = getGroup(groupPosition);
            Integer rate = item.getInt(ClientEntity.RATE);
            boolean isFolder = rate == ClientEntity.Rate.FOLDER.ordinal();
            if (convertView == null) {
                convertView = createGroupView(parent, isFolder);
            } else if (isFolder != convertView.getTag()) {
                convertView = createGroupView(parent, isFolder);
            }
            if (isFolder) {
                TextView labelView = (TextView) convertView.findViewById(R.id.label);
                labelView.setText(item.getString(ClientEntity.TITLE).toUpperCase());
                TextView countsView = (TextView) convertView.findViewById(R.id.counts);
                countsView.setText(item.getString(ClientEntity.COUNT_AS_STRING));
                labelView.setMaxWidth(mListView.getWidth() - countsView.getWidth() - mListView.getPaddingLeft() - mListView.getPaddingRight());
            } else {
                initChild(convertView, item.getString(ClientEntity.TITLE), item.getString(ClientEntity.META), item.getString(ClientEntity.COUNT_AS_STRING));
            }
            return convertView;
        }

        public View createGroupView(ViewGroup parent, boolean isFolder) {
            View convertView;
            if (isFolder) {
                convertView = inflater.inflate(R.layout.adapter_category, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.adapter_stream_item, parent, false);
            }
            convertView.setTag(isFolder);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            updateTextColor(view, DEFAULT_COLOR);
            view.setBackgroundColor(DEFAULT_COLOR);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            updateTextColor(view, DEFAULT_COLOR);
            view.setBackgroundColor(DEFAULT_COLOR);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            updateTextColor(view, loadedImage);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    }

}
