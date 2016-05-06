package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.Utils;
import pt.up.fe.infolab.ricardo.antmobile.activities.MainActivity;
import pt.up.fe.infolab.ricardo.antmobile.adapters.AntLookupItemAdapter;
import pt.up.fe.infolab.ricardo.antmobile.interfaces.OnQueryReadyInterface;
import pt.up.fe.infolab.ricardo.antmobile.models.SearchResult;

public class SearchFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject>, OnQueryReadyInterface {

    private AntLookupItemAdapter mAdapter;
    private ArrayList<SearchResult> lookupItems;
    private RecyclerView rvLookupItems;
    private SwipeRefreshLayout swLayout;
    private String mCurrentTag;

    private LinearLayout fragmentMessageRoot;
    private TextView userMessage;
    private ImageView userMessageImg;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null
                && savedInstanceState.containsKey("items")) {
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

        Bundle args = getArguments();
        mCurrentTag = args.getString("current_tag");

        rvLookupItems = (RecyclerView) rootView.findViewById(R.id.rv_lookup_items);
        fragmentMessageRoot = (LinearLayout) rootView.findViewById(R.id.intro_layout);
        swLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        userMessage = (TextView) rootView.findViewById(R.id.intro_message);
        userMessageImg = (ImageView) rootView.findViewById(R.id.intro_logo);
        swLayout.setRefreshing(false);
        swLayout.setEnabled(false);

        switch (mCurrentTag) {
            case "todos":
                setFeedbackMessage(getString(R.string.message_all), R.drawable.ic_ant);
                break;
            case "funcionário":
                setFeedbackMessage(getString(R.string.message_staff), R.drawable.ic_supervisor_account_white_48dp);
                break;
            case "estudante":
                setFeedbackMessage(getString(R.string.message_students), R.drawable.ic_school_white_48dp);
                break;
            case "sala":
                setFeedbackMessage(getString(R.string.message_rooms), R.drawable.ic_rooms);
                break;
            case "cadeira":
                setFeedbackMessage(getString(R.string.message_uc), R.drawable.ic_library_books_white_48dp);
                break;
            case "curso":
                setFeedbackMessage(getString(R.string.message_uc), R.drawable.ic_course_92dp);
                break;
            case "noticia":
                setFeedbackMessage(getString(R.string.message_news), R.drawable.ic_newspaper);
                break;
        }

        mAdapter = new AntLookupItemAdapter(lookupItems, getActivity());
        mAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvLookupItems.setAdapter(mAdapter);
        rvLookupItems.setLayoutManager(layoutManager);

        if (lookupItems.isEmpty()) {
            fragmentMessageRoot.setVisibility(View.VISIBLE);

            if (!((MainActivity)getActivity()).getActiveQuery().isEmpty()) {
                dispatchQuery(((MainActivity)getActivity()).getActiveQuery());
            }
        } else
            fragmentMessageRoot.setVisibility(View.GONE);

        if (mCurrentTag.equals("noticia")) {
            dispatchQuery("");
        }
        return rootView;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        swLayout.setRefreshing(false);
        Log.e("VOLLEY", "" + error.getMessage());
        setFeedbackMessage(getString(R.string.volley_error), R.drawable.ic_sad);
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
            fragmentMessageRoot.setVisibility(View.GONE);
            return;
        }

        fragmentMessageRoot.setVisibility(View.VISIBLE);
        userMessage.setText(message);
        userMessageImg.setImageDrawable(
                ContextCompat.getDrawable(
                        getActivity(),
                        imageResource));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("items", new Gson().toJson(lookupItems));
        outState.putString("current_tag", mCurrentTag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onQueryReady(String query) {

        if (mCurrentTag == null)
            return;

        dispatchQuery(query);
    }

    /**
     * Creates a new fragment and sets its tag to the received string
     * @param tag tag to apply
     * @return searc fragment
     */
    public static SearchFragment newInstance(String tag) {
        SearchFragment f = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("current_tag", tag);
        f.setArguments(args);
        return f;
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
     * adds the request to the queue if applicable
     */
    private void dispatchQuery(String extra) {

        if (!mCurrentTag.equals("todos"))
            extra += " tipoentidade:" + mCurrentTag;
            //extra += " tipoentidade:" + mCurrentTag;

        Log.e("QUERY", extra);

        lookupItems.clear();
        mAdapter.notifyDataSetChanged();

        swLayout.setRefreshing(true);
        //String baseQuery = "http://172.30.9.217:3000/search.json?";

        String baseQuery = Utils.antEndpoint + "/search?";
        Uri builtUri = Uri.parse(baseQuery)
                .buildUpon()
                .appendQueryParameter("q", extra)
                .appendQueryParameter("num", "20")
                .build();

        String queryURL = builtUri.toString();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, queryURL, null, SearchFragment.this, SearchFragment.this);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}