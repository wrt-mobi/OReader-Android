package mobi.wrt.oreader.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import by.istin.android.xcore.utils.UiUtil;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.fragments.ContentsFragment;
import mobi.wrt.oreader.app.fragments.responders.IContentClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StreamActivity extends ActionBarActivity implements IContentClick {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiUtil.setTranslucentBars(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_stream);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Intent intent = getIntent();
            String meta = intent.getStringExtra(ClientEntity.META);
            String type = intent.getStringExtra(ClientEntity.TYPE);
            String title = intent.getStringExtra(ClientEntity.TITLE);
            fragmentTransaction.
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                    add(R.id.container, ContentsFragment.newInstance(meta, type, title)).
                    commit();
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
        intent.putExtra(DetailsActivity.EXTRA_POSITION, position);
        startActivity(intent);
    }
}
