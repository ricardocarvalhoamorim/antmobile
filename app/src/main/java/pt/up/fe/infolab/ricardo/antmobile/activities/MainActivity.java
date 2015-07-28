package pt.up.fe.infolab.ricardo.antmobile.activities;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.adapters.AntLookupItemAdapter;
import pt.up.fe.infolab.ricardo.antmobile.models.SigarraIndividual;


public class MainActivity extends ActionBarActivity implements AdapterViewCompat.OnItemClickListener, Response.Listener<JSONArray>, Response.ErrorListener {

    private AntLookupItemAdapter mAdapter;
    private ArrayList<SigarraIndividual> lookupItems;
    private RecyclerView rvLookupItems;

    private final String baseQuery = "http://ant.fe.up.pt:4567/search?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.show();
        fab.setEnabled(true);

        rvLookupItems = (RecyclerView) findViewById(R.id.rv_lookup_items);
        lookupItems = new ArrayList<>();

        findViewById(R.id.intro_logo).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageView ivIntro = (ImageView) findViewById(R.id.intro_logo);
                ivIntro.setImageDrawable(
                        ContextCompat.getDrawable(
                                getApplicationContext(),
                                R.drawable.ic_developer_full_color));

                Toast.makeText(getApplicationContext(), "Full Power unleashed, take care", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        mAdapter = new AntLookupItemAdapter(lookupItems, this, this);
        mAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvLookupItems.setAdapter(mAdapter);
        rvLookupItems.setLayoutManager(layoutManager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachRecyclerView();
                dispatchQueryDialog();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((FloatingActionButton)findViewById(R.id.fab)).hide(true);
                return false;
            }
        });
        fab.show(true);

        attachEmptyView();
    }

    /**
     * Hides the recycler view and shows the initial screen
     */
    private void attachEmptyView() {
        findViewById(R.id.intro_layout).setVisibility(View.VISIBLE);
        rvLookupItems.setVisibility(View.GONE);
    }

    /**
     * Hides the recycler view and shows the initial screen
     */
    private void attachRecyclerView() {
        findViewById(R.id.intro_layout).setVisibility(View.GONE);
        rvLookupItems.setVisibility(View.VISIBLE);
    }

    /**
     * Inflates a dialog asking for query elements to search in the ant platform
     * and adds the request to the queue if applicable
     */
    private void dispatchQueryDialog() {
        lookupItems.clear();
        mAdapter.notifyDataSetChanged();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Tell me your wishes");
        alertDialog.setMessage("Looking for someone? a girl you saw yesterday? Kajir Helps you");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(ContextCompat.getDrawable(
                getApplicationContext(),
                R.drawable.ic_ant));


        alertDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String query = "";
                        query = input.getText().toString();
                        if (query.equals("")) {
                            Toast.makeText(getApplicationContext(), "Fine, no query", Toast.LENGTH_SHORT).show();
                            attachEmptyView();
                        } else {

                            Uri builtUri = Uri.parse(baseQuery)
                                    .buildUpon()
                                    .appendQueryParameter("q", query)
                                    .build();

                            String queryURL = builtUri.toString();
                            JsonArrayRequest req = new JsonArrayRequest(queryURL,
                                    MainActivity.this, MainActivity.this);

                            // Adding request to request queue
                            AppController.getInstance().addToRequestQueue(req);
                        }
                    }
                });

        alertDialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Fine, no query", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        attachEmptyView();
                    }
                });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(getApplicationContext(), "Fine, no query", Toast.LENGTH_SHORT).show();
                dialog.cancel();
                attachEmptyView();
            }
        });

        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterViewCompat<?> parent, View view, int position, long id) {

    }

    @Override
    public void onResponse(JSONArray response) {
        Log.d("", response.toString());

        if (response.length() == 0) {
            Toast.makeText(getApplicationContext(), "No results", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lookupItems == null) {
            lookupItems = new ArrayList<>();
        } else {
            lookupItems.clear();
        }

        try {
            for (int i = 0; i < response.length(); ++i) {
                JSONObject identityJson = (JSONObject) response
                        .get(i);

                SigarraIndividual identity =
                        new Gson().fromJson(identityJson.toString(), SigarraIndividual.class);

                lookupItems.add(identity);
            }
        } catch (JSONException e) {
            Toast.makeText(getApplication(), "Failed to parse response", Toast.LENGTH_SHORT).show();
        }

        mAdapter.notifyDataSetChanged();
        attachRecyclerView();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        VolleyLog.d("", "Error: " + error.getMessage());
        Toast.makeText(getApplicationContext(),
                error.getMessage(), Toast.LENGTH_SHORT).show();
    }


}
