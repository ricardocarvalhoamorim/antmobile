package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.adapters.AntLookupItemAdapter;
import pt.up.fe.infolab.ricardo.antmobile.interfaces.OnQueryReadyInterface;
import pt.up.fe.infolab.ricardo.antmobile.models.SearchResult;

public class SearchFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject>, OnQueryReadyInterface{

    private AntLookupItemAdapter mAdapter;
    private ArrayList<SearchResult> lookupItems;
    private RecyclerView rvLookupItems;
    private LinearLayout introMessage;
    private SwipeRefreshLayout swLayout;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null &&
                savedInstanceState.containsKey("items")) {
            lookupItems = new Gson().fromJson(savedInstanceState.getString("items"), new TypeToken<ArrayList<SearchResult>>() {
            }.getType());

        } else {
            lookupItems = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        rvLookupItems = (RecyclerView) rootView.findViewById(R.id.rv_lookup_items);
        introMessage = (LinearLayout) rootView.findViewById(R.id.intro_layout);
        swLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);

        if (lookupItems.isEmpty())
            introMessage.setVisibility(View.VISIBLE);
        else
            introMessage.setVisibility(View.GONE);


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

        /*
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchQuery();
            }
        });

        fab.show(true);
        */
        return rootView;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        swLayout.setRefreshing(false);
        VolleyLog.e("Volley", "" + error.getMessage());
        Toast.makeText(getActivity(),
                getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        swLayout.setRefreshing(false);
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


        setFeedbackMessage("", 0);
        rvLookupItems.setVisibility(View.VISIBLE);
        rvLookupItems.setAdapter(mAdapter);
    }


    /**
     * Sets the home image to visible and applies the recieved message to the textview.
     * An drawable resource should be passed
     * @param message Message to display. If empty, the view will be hidden
     * @param imageResource image to apply below the text
     */
    private void setFeedbackMessage(String message, int imageResource) {

        if (message.isEmpty()) {
            introMessage.setVisibility(View.GONE);
            return;
        }

        introMessage.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.intro_layout).setVisibility(View.VISIBLE);
        ((TextView) getActivity().findViewById(R.id.intro_message)).setText(message);

        ImageView ivIntro = (ImageView) getActivity().findViewById(R.id.intro_logo);
        ivIntro.setImageDrawable(
                ContextCompat.getDrawable(
                        getActivity(),
                        imageResource));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("items", new Gson().toJson(lookupItems));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onQueryReady(String extra) {
        dispatchQuery(extra);
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

    /**
     * Inflates a dialog asking for query elements to search in the ant platform
     * and adds the request to the queue if applicable
     */
    private void dispatchQuery(String extra) {
        lookupItems.clear();
        mAdapter.notifyDataSetChanged();

        ((SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_layout)).setRefreshing(true);
        String baseQuery = "http://ant.fe.up.pt/search.json?";
        Uri builtUri = Uri.parse(baseQuery)
                .buildUpon()
                .appendQueryParameter("q", extra)
                .appendQueryParameter("num", "50")
                .build();

        String queryURL = builtUri.toString();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, queryURL, null, SearchFragment.this, SearchFragment.this);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}