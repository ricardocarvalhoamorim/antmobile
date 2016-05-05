package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.Utils;
import pt.up.fe.infolab.ricardo.antmobile.adapters.AntLookupItemAdapter;
import pt.up.fe.infolab.ricardo.antmobile.models.SearchResult;


public class NewsFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject>, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<SearchResult> items;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AntLookupItemAdapter mAdapter;
    private RelativeLayout getNewsLayout;
    private Button btRefreshNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null
                && savedInstanceState.containsKey("news")) {
            items = new Gson().fromJson(savedInstanceState.getString("news"), new TypeToken<ArrayList<SearchResult>>() {
            }.getType());
        } else {
            items = new ArrayList<>();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_lookup_items);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        getNewsLayout = (RelativeLayout) rootView.findViewById(R.id.news_get_view);
        btRefreshNews = (Button) rootView.findViewById(R.id.bt_get_news);

        swipeRefreshLayout.setOnRefreshListener(this);
        btRefreshNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchQuery("");
            }
        });

        bindAdapter(items);
        return rootView;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        swipeRefreshLayout.setRefreshing(false);

        getNewsLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.INVISIBLE);

        btRefreshNews.setText("Hey, something went wrong. Tap here to try again...");
    }

    @Override
    public void onResponse(JSONObject response) {
        swipeRefreshLayout.setRefreshing(false);

        if (response.length() == 0) {
            getNewsLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
            btRefreshNews.setText("An empty response? hmm its weird... tap here to try again.");
            return;
        }

        getNewsLayout.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        if (items == null) {
            items = new ArrayList<>();
        }

        Data item = new Gson().fromJson(response.toString(), Data.class);

        if (item.getData().isEmpty()) {
            btRefreshNews.setText("No news...I guess its good news then. tap here to try again.");
            return;
        }

        items = item.getData();
        bindAdapter(items);
    }

    /**
     * Turns the recycler view visible and binds its elements
     * @param items list of elements to bind to the list
     */
    private void bindAdapter(ArrayList<SearchResult> items) {
        if (items.isEmpty()) {
            dispatchQuery("");
            return;
        }

        mAdapter = new AntLookupItemAdapter(items, getActivity());
        mAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * adds the request to the queue if applicable
     */
    private void dispatchQuery(String extra) {

        extra += " tipoentidade:noticia";

        swipeRefreshLayout.setRefreshing(true);
        //http://172.30.9.217:3000
        String baseQuery = Utils.antEndpoint + "/search?";
        Uri builtUri = Uri.parse(baseQuery)
                .buildUpon()
                .appendQueryParameter("q", extra)
                .appendQueryParameter("num", "20")
                .build();

        String queryURL = builtUri.toString();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, queryURL, null, NewsFragment.this, NewsFragment.this);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    @Override
    public void onRefresh() {
        dispatchQuery("");
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("news", new Gson().toJson(items));
        super.onSaveInstanceState(outState);
    }


}
