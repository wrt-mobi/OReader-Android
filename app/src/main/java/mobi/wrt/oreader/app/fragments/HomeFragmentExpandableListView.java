package mobi.wrt.oreader.app.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import by.istin.android.xcore.fragment.XFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.utils.ContentUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.R;
import mobi.wrt.oreader.app.StreamActivity;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;
import mobi.wrt.oreader.app.image.Displayers;
import mobi.wrt.oreader.app.view.SymbolImageView;

public class HomeFragmentExpandableListView extends XFragment {

    private AnimatedExpandableListView mListView;
    private ExpandableListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
    }

    public void onListItemClick(Cursor cursor, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), StreamActivity.class);
        intent.putExtra(ClientEntity.META, CursorUtils.getString(ClientEntity.META, cursor));
        intent.putExtra(ClientEntity.TYPE, CursorUtils.getString(ClientEntity.TYPE, cursor));
        startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (AnimatedExpandableListView) view.findViewById(android.R.id.list);
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
        return ClientEntity.RATE + " = "+ ClientEntity.Rate.FOLDER.ordinal() + " OR " + ClientEntity.CATEGORIES + " IS NULL";
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
                    List<ContentValues> values = ContentUtils.getEntities(context, ClientEntity.class, ClientEntity.CATEGORIES +" like ?", "%"+categories+"%");
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

    private class ExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
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
            String title = item.getAsString(ClientEntity.TITLE);
            ((TextView) convertView.findViewById(R.id.label)).setText(title.substring(1));
            SymbolImageView imageView = (SymbolImageView) convertView.findViewById(R.id.icon);
            String meta = item.getAsString(ClientEntity.META);
            String encode = Uri.parse(meta).getQueryParameter(Subscriptions.WEBSITE);
            Log.xd(this, "icon:"+encode);
            //String uri = "http://g.etfv.co/" + encode;
            Uri parse = Uri.parse(encode);
            String uri = parse.getScheme() + "://"+parse.getHost() +"/favicon.ico";
            Log.xd(this, "icon:"+uri);
            imageView.setSymbol(title.substring(0,1));
            ImageLoader.getInstance().displayImage(uri, imageView, Displayers.BITMAP_DISPLAYER_ICON_BG);
            return convertView;
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
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.adapter_category, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.label)).setText(item.getString(ClientEntity.TITLE));

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

    }

}
