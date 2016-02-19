package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.models.Menu;


public class CanteenFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private ArrayList<Menu> canteenMenu;
    private TextView tvCanteenStatus;
    private Button btRefresh;

    private List<String> canteens = new ArrayList<>();
    private Spinner canteenList;
    private String pickedCanteen = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey("items")) {
                canteenMenu = new Gson().fromJson(
                        savedInstanceState.getString("items"),
                        new TypeToken<ArrayList<Menu>>() {}.getType());
            }

            if (savedInstanceState.containsKey("canteens")) {
                canteens = new Gson().fromJson(savedInstanceState.getString("canteens"),
                        new TypeToken<ArrayList<String>>() {}.getType());
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_canteen, container, false);
        tvCanteenStatus = (TextView) rootView.findViewById(R.id.canteen_status);
        btRefresh = (Button) rootView.findViewById(R.id.canteen_refresh);
        canteenList = (Spinner) rootView.findViewById(R.id.canteen_spinner);

        rootView.findViewById(R.id.canteen_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btRefresh.setEnabled(false);
                btRefresh.setText(getString(R.string.loading));
                tvCanteenStatus.setVisibility(View.INVISIBLE);
                if (!pickedCanteen.isEmpty())
                    AppController.getInstance().addToRequestQueue(getCanteenMenu(pickedCanteen));
            }
        });

        if (canteens.isEmpty()) {
            fetchCanteenList();
        } else {
            attachCanteenItems();
        }


        canteenList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pickedCanteen = canteens.get(position);
                btRefresh.callOnClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setMenuText();
        return rootView;
    }

    private void attachCanteenItems() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_spinner, canteens);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        canteenList.setAdapter(dataAdapter);
    }

    /**
     * Builds the canteen list request and adds it to the queue
     */
    private void fetchCanteenList() {
        JsonObjectRequest canteenRequest = new JsonObjectRequest(
                Request.Method.GET, "http://ant.fe.up.pt/sigarra-info/menu/places?version=1.1",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                CanteensData responseItem = new Gson().fromJson(response.toString(), CanteensData.class);
                Collections.sort(responseItem.getData());
                canteens = responseItem.getData();
                attachCanteenItems();
            }
        }, CanteenFragment.this);

        AppController.getInstance().addToRequestQueue(canteenRequest);
    }

    /**
     * Formats the data and applies its text to the interface
     */
    private void setMenuText() {

        if (canteenMenu == null || canteenMenu.isEmpty()) {
            btRefresh.setEnabled(false);
            tvCanteenStatus.setVisibility(View.INVISIBLE);
            btRefresh.setText(getString(R.string.loading));
            if (!pickedCanteen.isEmpty())
                AppController.getInstance().addToRequestQueue(getCanteenMenu(pickedCanteen));

            return;
        }

        if (canteenMenu.get(0).getMenu().isEmpty()) {
            tvCanteenStatus.setText(getString(R.string.no_menu));
            tvCanteenStatus.setVisibility(View.VISIBLE);
            btRefresh.setText(getString(R.string.update));
            btRefresh.setEnabled(true);
            return;
        }

        String responseString = "";
        for (Menu m : canteenMenu) {
            if (m.getMenu().isEmpty())
                continue;

            responseString += "<h1>" + m.getName() + "</h1>";

            //each top level array corresponds to one day
            for (ArrayList<Menu.MenuItem> dailyMenu : m.getMenu()) {

                responseString += "<br /><b>" + dailyMenu.get(0).getValue() + "</b><br/>";

                Iterator<Menu.MenuItem> it = dailyMenu.iterator();
                it.next();
                while (it.hasNext()) {
                    Menu.MenuItem mItem = it.next();
                    responseString += "<b>" + mItem.getField() + ":</b> " + mItem.getValue() + "<br/>";
                }

            }
        }

        tvCanteenStatus.setText(Html.fromHtml(responseString));
        tvCanteenStatus.setVisibility(View.VISIBLE);
        btRefresh.setText(getString(R.string.update));
        btRefresh.setEnabled(true);
    }

    @Override
    public void onResponse(JSONObject response) {
        Data responseItem = new Gson().fromJson(response.toString(), Data.class);
        canteenMenu = responseItem.getData();
        setMenuText();
    }



    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("VOLLEY", error.toString());
        tvCanteenStatus.setText(getString(R.string.volley_error));
        tvCanteenStatus.setVisibility(View.VISIBLE);
        btRefresh.setText(getString(R.string.update));
        btRefresh.setEnabled(true);
    }

    /**
     * Fetches the menu for the selected canteen
     * @param canteen chosen place
     * @return request to be added to the queue
     */
    public JsonObjectRequest getCanteenMenu(String canteen) {
        return new JsonObjectRequest(
                Request.Method.GET, "http://ant.fe.up.pt/sigarra-info/menu?version=1.1&place=" + canteen.replaceAll(" ", "%20"),
                null, CanteenFragment.this, CanteenFragment.this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("items", new Gson().toJson(canteenMenu));
        outState.putString("canteens", new Gson().toJson(canteens));
    }

    /**
     * Canteen menu class
     */
    private class Data implements Serializable {
        private ArrayList<Menu> data;

        public ArrayList<Menu> getData() {
            return data;
        }

        public void setData(ArrayList<Menu> data) {
            this.data = data;
        }
    }

    /**
     * Canteen list class
     */
    private class CanteensData implements Serializable {
        private ArrayList<String> data;

        public ArrayList<String> getData() {
            return data;
        }

        public void setData(ArrayList<String> data) {
            this.data = data;
        }
    }
}
