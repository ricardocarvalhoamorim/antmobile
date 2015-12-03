package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.activities.MainActivity;
import pt.up.fe.infolab.ricardo.antmobile.adapters.AntLookupItemAdapter;
import pt.up.fe.infolab.ricardo.antmobile.models.SearchResult;

/**
 * Created by ricardo on 12/3/15.
 */
public class SearchFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private AntLookupItemAdapter mAdapter;
    private ArrayList<SearchResult> lookupItems;
    private RecyclerView rvLookupItems;

    private final String baseQuery = "http://ant.fe.up.pt/search.json?";
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        rvLookupItems = (RecyclerView) rootView.findViewById(R.id.rv_lookup_items);
        lookupItems = new ArrayList<>();

        rootView.findViewById(R.id.intro_logo).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ImageView ivIntro = (ImageView) rootView.findViewById(R.id.intro_logo);
                ivIntro.setImageDrawable(
                        ContextCompat.getDrawable(
                                getActivity(),
                                R.drawable.ic_developer_full_color));

                Toast.makeText(getActivity(), "Full Power unleashed, take care", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        mAdapter = new AntLookupItemAdapter(lookupItems, getActivity());
        mAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvLookupItems.setAdapter(mAdapter);
        rvLookupItems.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchQueryDialog();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((FloatingActionButton)rootView.findViewById(R.id.fab)).hide(true);
                return false;
            }
        });
        fab.show(true);
        return rootView;
    }

    /**
     * Inflates a dialog asking for query elements to search in the ant platform
     * and adds the request to the queue if applicable
     */
    private void dispatchQueryDialog() {
        lookupItems.clear();
        mAdapter.notifyDataSetChanged();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.query_input_title));
        alertDialog.setMessage(getString(R.string.query_input_message));

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText("Ricardo Amorim");
        alertDialog.setView(input);
        alertDialog.setIcon(ContextCompat.getDrawable(
                getActivity(),
                R.drawable.ic_ant));


        alertDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String query = "";
                        query = input.getText().toString();
                        if (query.equals("")) {
                            setFeedbackMessage(getString(R.string.no_query), R.drawable.ic_ant);
                        } else {

                            Uri builtUri = Uri.parse(baseQuery)
                                    .buildUpon()
                                    .appendQueryParameter("q", query)
                                    .appendQueryParameter("num", "50")
                                    .build();

                            String queryURL = builtUri.toString();

                            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, queryURL, null, SearchFragment.this, SearchFragment.this);

                            // Adding request to request queue
                            AppController.getInstance().addToRequestQueue(req);
                        }
                    }
                });

        alertDialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: maybe some feedback?
                    }
                });


        alertDialog.show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        VolleyLog.e("Volley", "" + error.getMessage());
        Toast.makeText(getActivity(),
                getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d("", response.toString());

        if (response.length() == 0) {
            setFeedbackMessage(getString(R.string.bad_response), R.drawable.ic_ant);
            return;
        }

        if (lookupItems == null) {
            lookupItems = new ArrayList<>();
        } else {
            lookupItems.clear();
        }

        Data item = new Gson().fromJson(response.toString(), Data.class);

        if (item.getData().isEmpty()) {
            setFeedbackMessage(getString(R.string.no_results), R.drawable.ic_ant);
            return;
        }

        lookupItems = item.getData();
        mAdapter = new AntLookupItemAdapter(lookupItems, getActivity());
        mAdapter.notifyDataSetChanged();

        getActivity().findViewById(R.id.intro_layout).setVisibility(View.GONE);
        rvLookupItems.setVisibility(View.VISIBLE);
        rvLookupItems.setAdapter(mAdapter);
    }


    /**
     * Sets the home image to visible and applies the recieved message to the textview.
     * An drawable resource should be passed
     * @param message Message to display
     * @param imageResource image to apply below the text
     */
    private void setFeedbackMessage(String message, int imageResource) {
        getActivity().findViewById(R.id.intro_layout).setVisibility(View.VISIBLE);
        ((TextView) getActivity().findViewById(R.id.intro_message)).setText(message);

        ImageView ivIntro = (ImageView) getActivity().findViewById(R.id.intro_logo);
        ivIntro.setImageDrawable(
                ContextCompat.getDrawable(
                        getActivity(),
                        imageResource));
    }

    /**
     * Holder class for the root element of the response
     */
    public class Data {
        private ArrayList<SearchResult> data;

        public ArrayList<SearchResult> getData() {
            return data;
        }

        public void setData(ArrayList<SearchResult> data) {
            this.data = data;
        }
    }
}