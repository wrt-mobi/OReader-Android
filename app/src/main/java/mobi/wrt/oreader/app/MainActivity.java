package mobi.wrt.oreader.app;

import android.app.Activity;
import android.content.ContentValues;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.v4.support.internal.text.TypefaceSpan;

import by.istin.android.xcore.utils.UiUtil;
import mobi.wrt.oreader.app.application.Application;
import mobi.wrt.oreader.app.clients.db.ClientEntity;
import mobi.wrt.oreader.app.fragments.HomeFragmentExpandableListView;
import mobi.wrt.oreader.app.fragments.HomeFragmentMagazine;
import mobi.wrt.oreader.app.fragments.NavigationDrawerFragment;
import mobi.wrt.oreader.app.fragments.responders.IClientEntityClick;
import mobi.wrt.oreader.app.utils.PreferenceUtils;
import mobi.wrt.oreader.app.view.utils.TranslucentUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        IClientEntityClick{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private PreferenceUtils.HomeViewType mHomeViewType;

    private boolean isHideRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeViewType = PreferenceUtils.getHomeViewType();
        isHideRead = PreferenceUtils.isHideRead();

        UiUtil.setTranslucentNavigation(this);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setIcon(null);
        TranslucentUtils.applyTranslucentPaddingForView((ViewGroup)findViewById(R.id.container), true, true, false);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                if (mHomeViewType == PreferenceUtils.HomeViewType.GRID) {
                    fragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.container, HomeFragmentMagazine.newInstance(isHideRead))
                            .commit();
                } else if (mHomeViewType == PreferenceUtils.HomeViewType.LIST) {
                    fragmentManager.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.container, HomeFragmentExpandableListView.newInstance(isHideRead))
                        .commit();
                }
                break;
            case 1:
                Intent intent = new Intent(this, WizardActivity.class);
                intent.putExtra(WizardActivity.EXTRA_IGNORE_PREFERENCE, true);
                startActivity(intent);
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_home);
                break;
            case 2:
                mTitle = getString(R.string.title_connect);
                break;
            case 3:
                mTitle = getString(R.string.title_settings);
                break;
            case 4:
                mTitle = getString(R.string.title_support);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);


        SpannableString s = new SpannableString(mTitle.toString().toUpperCase());
        s.setSpan(new TypefaceSpan(this, Application.DEFAULT_FONT_AB), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        actionBar.setTitle(s);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            if (mHomeViewType == PreferenceUtils.HomeViewType.GRID) {
                getMenuInflater().inflate(R.menu.home_grid, menu);
            } else if (mHomeViewType == PreferenceUtils.HomeViewType.LIST) {
                getMenuInflater().inflate(R.menu.home_list, menu);
            }
            menu.findItem(R.id.action_unread_visibility).setChecked(isHideRead);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_view_type) {
            if (mHomeViewType == PreferenceUtils.HomeViewType.GRID) {
                mHomeViewType = PreferenceUtils.HomeViewType.LIST;
            } else {
                mHomeViewType = PreferenceUtils.HomeViewType.GRID;
            }
            PreferenceUtils.setHomeViewType(mHomeViewType);
            onNavigationDrawerItemSelected(0);
            supportInvalidateOptionsMenu();
            return true;
        }
        if (id == R.id.action_unread_visibility) {
            isHideRead = !isHideRead;
            PreferenceUtils.setHideRead(isHideRead);
            onNavigationDrawerItemSelected(0);
            item.setChecked(isHideRead);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClientEntityClick(View view) {
        ContentValues contentValues = (ContentValues) view.getTag();
        String meta = contentValues.getAsString(ClientEntity.META);
        String type = contentValues.getAsString(ClientEntity.TYPE);
        String title = contentValues.getAsString(ClientEntity.TITLE);
        onClientEntityClick(view, null, meta, type, title);
    }

    @Override
    public void onClientEntityClick(View v, String icon, String meta, String type, String title) {
        Intent intent = new Intent(this, StreamActivity.class);
        //Intent intent = new Intent(this, AmazingActivity.class);
        intent.putExtra(ClientEntity.META, meta);
        intent.putExtra(ClientEntity.TYPE, type);
        intent.putExtra(ClientEntity.TITLE, title);
        intent.putExtra(ClientEntity.ICON, icon);
        ImageView view = (ImageView) v.findViewById(R.id.icon);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.setTransitionGroup(false);
            ((ViewGroup)parent.getParent()).setTransitionGroup(false);
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(this, view, "photo_hero");
            startActivity(intent, options.toBundle());
            return;
        }
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
