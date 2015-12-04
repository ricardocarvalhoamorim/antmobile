package pt.up.fe.infolab.ricardo.antmobile.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.fragments.SearchFragment;


public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout.Tab activeTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        activeTab = tabLayout.getTabAt(0);
        tabLayout.setOnTabSelectedListener(this);


        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.show();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptDialog();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment(), "todos");
        adapter.addFragment(new SearchFragment(), "estudante");
        adapter.addFragment(new SearchFragment(), "funcionário");
        adapter.addFragment(new SearchFragment(), "sala");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        activeTab = tab;
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


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

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.query_input_title));
        alertDialog.setMessage(getString(R.string.query_input_message));

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText("Ricardo Amorim");
        alertDialog.setView(input);
        alertDialog.setIcon(ContextCompat.getDrawable(
                this,
                R.drawable.ic_ant));


        alertDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String query = "";
                        query = input.getText().toString();
                        if (query.equals("")) {
                            Toast.makeText(MainActivity.this, "Please provide at least 1 term", Toast.LENGTH_SHORT).show();
                        } else {
                            if (activeTab == null)
                                return;


                            List<Fragment> fragments = getSupportFragmentManager().getFragments();
                            if (! (fragments.get(activeTab.getPosition()) instanceof SearchFragment))
                                return;

                            if (activeTab.getPosition() == 0) {
                                ((SearchFragment) fragments.get(activeTab.getPosition())).onQueryReady(query);
                            }
                            ((SearchFragment) fragments.get(activeTab.getPosition())).onQueryReady(query + " tipoentidade:" + activeTab.getText());
                        }
                    }
                });

        alertDialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        //setFeedbackMessage(getString(R.string.query_canceled), R.drawable.ic_ant);
                    }
                });


        alertDialog.show();
    }

}
