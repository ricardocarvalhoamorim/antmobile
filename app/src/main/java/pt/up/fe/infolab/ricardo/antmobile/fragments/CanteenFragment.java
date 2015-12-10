package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Iterator;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.models.Menu;


public class CanteenFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    private ArrayList<Menu> canteenItems;
    private TextView tvCanteenStatus;
    private Button btRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null
                && savedInstanceState.containsKey("items")) {

            canteenItems = new Gson().fromJson(
                    savedInstanceState.getString("items"),
                    new TypeToken<ArrayList<Menu>>() {}.getType());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_canteen, container, false);
        tvCanteenStatus = (TextView) rootView.findViewById(R.id.canteen_status);
        btRefresh = (Button) rootView.findViewById(R.id.canteen_refresh);

        rootView.findViewById(R.id.canteen_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btRefresh.setEnabled(false);
                btRefresh.setText(getString(R.string.loading));
                tvCanteenStatus.setVisibility(View.INVISIBLE);
                AppController.getInstance().addToRequestQueue(getCanteenRequest());
            }
        });

        setMenuText();
        return rootView;
    }

    /**
     * Formats the data and applies its text to the interface
     */
    private void setMenuText() {

        if (canteenItems == null || canteenItems.isEmpty()) {
            btRefresh.setEnabled(false);
            tvCanteenStatus.setVisibility(View.INVISIBLE);
            btRefresh.setText(getString(R.string.loading));
            AppController.getInstance().addToRequestQueue(getCanteenRequest());
            return;
        }

        String responseString = "";
        for (Menu m : canteenItems) {
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
        canteenItems = responseItem.getData();
        setMenuText();
    }



    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("VOLLEY", error.toString());
        tvCanteenStatus.setText("Alguém fez asneira. Tens net por acaso?");
        tvCanteenStatus.setVisibility(View.VISIBLE);
        btRefresh.setEnabled(true);
    }

    public JsonObjectRequest getCanteenRequest() {
        return new JsonObjectRequest(
                Request.Method.GET, "http://ant.fe.up.pt/sigarra-info/menu",
                null, CanteenFragment.this, CanteenFragment.this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("items", new Gson().toJson(canteenItems));
    }

    private class Data implements Serializable {
        private ArrayList<Menu> data;

        public ArrayList<Menu> getData() {
            return data;
        }

        public void setData(ArrayList<Menu> data) {
            this.data = data;
        }
    }

}
