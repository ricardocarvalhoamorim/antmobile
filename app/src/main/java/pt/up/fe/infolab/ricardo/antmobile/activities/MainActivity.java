package pt.up.fe.infolab.ricardo.antmobile.activities;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.fragments.AboutFragment;
import pt.up.fe.infolab.ricardo.antmobile.fragments.CanteenFragment;
import pt.up.fe.infolab.ricardo.antmobile.fragments.ParkingFragment;
import pt.up.fe.infolab.ricardo.antmobile.fragments.SearchFragment;
import pt.up.fe.infolab.ricardo.antmobile.models.SearchResult;


public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout.Tab activeTab;
    private ViewPagerAdapter adapter;
    private FloatingActionButton floatingActionButton;
    private ViewPager viewPager;
    private EditText etQuery;
    //private CardView cvSearch;
    private String lastQuery;
    private Map<String, ArrayList<SearchResult>> queryResults;

    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        etQuery = (EditText) findViewById(R.id.app_search);
        //cvSearch = (CardView) findViewById(R.id.card_search);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        tabLayout.setupWithViewPager(viewPager);
        activeTab = tabLayout.getTabAt(0);
        tabLayout.setOnTabSelectedListener(this);

        floatingActionButton.show();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptDialog();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(SearchFragment.newInstance("todos"), "todos");
        adapter.addFragment(SearchFragment.newInstance("estudante"), "estudante");
        adapter.addFragment(SearchFragment.newInstance("funcionário"), "funcionário");
        adapter.addFragment(SearchFragment.newInstance("sala"), "sala");
        adapter.addFragment(new CanteenFragment(), "Ementa");
        adapter.addFragment(new ParkingFragment(), "Estacionamento");
        adapter.addFragment(new AboutFragment(), "Sobre nós");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        activeTab = tab;

        viewPager.setCurrentItem(tab.getPosition());
        //last position, no need for the fab button
        if (activeTab.getPosition() >= adapter.getCount() - 3) {
            floatingActionButton.hide();
            appBarLayout.setExpanded(false);
        } else {
            floatingActionButton.show();
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

    /**
     * Shows a dialog to input the search query terms
     */
    private void promptDialog() {

        if (lastQuery != null)
            etQuery.setText(lastQuery);

        floatingActionButton.hide();

        //cvSearch.setVisibility(View.VISIBLE);
        etQuery.requestFocus();
        etQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    dispatchSearch(etQuery.getText().toString());
                    return true;
                }
                return false;
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etQuery, InputMethodManager.SHOW_IMPLICIT);
    }

    private void dispatchSearch(String query) {
        if (activeTab == null)
            return;

        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etQuery.getWindowToken(), 0);

        //cvSearch.setVisibility(View.INVISIBLE);
        floatingActionButton.show();

        lastQuery = query;

        /*
        if (adapter.getItem(activeTab.getPosition()) instanceof SearchFragment)
            ((SearchFragment) adapter.getItem(activeTab.getPosition())).onQueryReady(query, "" + activeTab.getText());
        */

        for (int i = 0; i < adapter.getCount(); ++i) {
            if (adapter.getItem(i) instanceof SearchFragment)
                ((SearchFragment) adapter.getItem(i)).onQueryReady(query);
        }
    }

    public String getActiveQuery() {
        return this.lastQuery == null ? "" : this.lastQuery;
    }
}
