package mobi.wrt.oreader.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import by.istin.android.xcore.utils.UiUtil;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.fragments.ContentsFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StreamActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiUtil.setTranslucentStatus(this);
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

}
