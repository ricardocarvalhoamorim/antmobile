package pt.up.fe.infolab.ricardo.antmobile.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.fragments.AboutFragment;
import pt.up.fe.infolab.ricardo.antmobile.fragments.CanteenFragment;
import pt.up.fe.infolab.ricardo.antmobile.fragments.ParkingFragment;
import pt.up.fe.infolab.ricardo.antmobile.fragments.SearchFragment;


public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout.Tab activeTab;
    private ViewPagerAdapter adapter;
    private ViewPager viewPager;
    private String lastQuery;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout.setupWithViewPager(viewPager);
        activeTab = tabLayout.getTabAt(0);
        tabLayout.setOnTabSelectedListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SearchFragment.newInstance("todos"), getString(R.string.category_all));
        adapter.addFragment(SearchFragment.newInstance("estudante"), getString(R.string.category_student));
        adapter.addFragment(SearchFragment.newInstance("funcionário"), getString(R.string.category_staff));
        adapter.addFragment(SearchFragment.newInstance("sala"), getString(R.string.category_room));
        adapter.addFragment(SearchFragment.newInstance("cadeira"), getString(R.string.category_uc));
        adapter.addFragment(SearchFragment.newInstance("curso"), getString(R.string.category_course));
        adapter.addFragment(SearchFragment.newInstance("noticia"), getString(R.string.category_news));
        adapter.addFragment(new CanteenFragment(), getString(R.string.category_menu));
        adapter.addFragment(new ParkingFragment(), getString(R.string.category_parking));
        adapter.addFragment(new AboutFragment(), getString(R.string.category_about));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        activeTab = tab;

        viewPager.setCurrentItem(tab.getPosition());
        //last position, no need for the fab button
        if (activeTab.getPosition() >= adapter.getCount() - 4) {
            appBarLayout.setExpanded(false);
        } else {
            appBarLayout.setExpanded(true);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private void dispatchSearch(String query) {
        if (activeTab == null)
            return;

        lastQuery = query;

        for (int i = 0; i < adapter.getCount(); ++i) {
            if (adapter.getItem(i) instanceof SearchFragment)
                ((SearchFragment) adapter.getItem(i)).onQueryReady(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here u can get the value "query" which is entered in the search box.
                dispatchSearch(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }


    public String getActiveQuery() {
        return this.lastQuery == null ? "" : this.lastQuery;
    }
}
