package mobi.wrt.oreader.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.utils.Log;
import by.istin.android.xcore.utils.StringUtil;
import by.istin.android.xcore.utils.UiUtil;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.clients.feedly.db.Subscriptions;
import mobi.wrt.oreader.app.clients.twitter.TwitterRequestHelper;
import mobi.wrt.oreader.app.fragments.ContentsFragment;
import mobi.wrt.oreader.app.fragments.responders.IContentClick;
import mobi.wrt.oreader.app.image.Displayers;
import mobi.wrt.oreader.app.ui.StreamConfig;
import mobi.wrt.oreader.app.view.utils.SymbolViewUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StreamActivity extends ActionBarActivity implements IContentClick {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiUtil.setTranslucentBars(this);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(StringUtil.EMPTY);
        supportActionBar.setIcon(android.R.color.transparent);
        Drawable transparentDrawable = getResources().getDrawable(android.R.color.transparent);
        supportActionBar.setBackgroundDrawable(transparentDrawable);
        supportActionBar.setSplitBackgroundDrawable(transparentDrawable);
        supportActionBar.setStackedBackgroundDrawable(transparentDrawable);
        //supportActionBar.hide();
        setContentView(R.layout.activity_stream);
        Intent intent = getIntent();
        String meta = intent.getStringExtra(ClientEntity.META);
        String type = intent.getStringExtra(ClientEntity.TYPE);
        String title = intent.getStringExtra(ClientEntity.TITLE);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                    add(R.id.container, ContentsFragment.newInstance(StreamConfig.AdapterType.FULL, meta, type, title)).
                    commit();
        }
        final View floatHeaderView = findViewById(R.id.floatHeader);
        initHeader(title, meta, floatHeaderView);
        final ImageView headerImageView = (ImageView) floatHeaderView.findViewById(R.id.headerBackground);
        TwitterRequestHelper.searchProfile(this, title, new ISuccess<String>() {
            @Override
            public void success(String s) {
                Log.xd(StreamActivity.this, "banner_url: " + s);
                if (!StringUtil.isEmpty(s)) {
                    floatHeaderView.findViewById(R.id.twitterTab).setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(s, headerImageView);
                } else {
                    ImageLoader.getInstance().displayImage("assets://backgrounds/night_2.png", headerImageView);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_stream, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.mark_all_as_read) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initHeader(String title, String metaString, View floatHeaderView) {
        TextView labelView = (TextView) floatHeaderView.findViewById(R.id.label);
        labelView.setText(title.substring(1));
        ((TextView) floatHeaderView.findViewById(R.id.symbol)).setText(title.substring(0, 1));

        Uri meta = Uri.parse(metaString);
        String encode = meta.getQueryParameter(Subscriptions.WEBSITE);
        if (!StringUtil.isEmpty(encode)) {
            Uri parse = Uri.parse(encode);
            String uri = parse.getScheme() + "://" + parse.getHost() + "/favicon.ico";
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
        } else {
            //TODO make something for group
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onContentClick(long id, int position) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra(BaseColumns._ID, id);
        intent.putExtra(DetailsActivity.EXTRA_POSITION, position - 1);
        startActivity(intent);
    }
}
