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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.models.ParkItem;

public class ParkingFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONArray> {

    private ArrayList<ParkItem> parkItems;
    private TextView tvParkingStatus;
    private Button btRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null
                && savedInstanceState.containsKey("items")) {

            parkItems = new Gson().fromJson(
                    savedInstanceState.getString("items"),
                    new TypeToken<ArrayList<ParkItem>>() {}.getType());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_parking, container, false);
        tvParkingStatus = (TextView) rootView.findViewById(R.id.parking_status);
        btRefresh = (Button) rootView.findViewById(R.id.parking_refresh);

        if (parkItems != null && !parkItems.isEmpty()) {
            String result = "";
            for (int i = 0; i < parkItems.size(); ++i) {
                result += "<b>Parque " + (i+1) + ": </b>" + parkItems.get(i).getLugares() + "<br/> <br/>";
            }
            tvParkingStatus.setText(Html.fromHtml(result));
        } else
            AppController.getInstance().addToRequestQueue(getParkRequest());

        rootView.findViewById(R.id.parking_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btRefresh.setEnabled(false);
                AppController.getInstance().addToRequestQueue(getParkRequest());
            }
        });
        return rootView;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("VOLLEY", error.toString());
        tvParkingStatus.setText("Alguém fez asneira. Tens net por acaso?");
        btRefresh.setEnabled(true);
    }

    @Override
    public void onResponse(JSONArray response) {
        parkItems = new Gson().fromJson(response.toString(), new TypeToken<ArrayList<ParkItem>>() {
        }.getType());

        String result = "";
        for (int i = 0; i < parkItems.size(); ++i) {
            result += "<b>Parque " + (i+1) + ": </b>" + parkItems.get(i).getLugares() + "<br/> <br/>";
        }
        tvParkingStatus.setText(Html.fromHtml(result));
        btRefresh.setEnabled(true);
    }

    public JsonArrayRequest getParkRequest() {
        return new JsonArrayRequest(
                Request.Method.GET, "http://paginas.fe.up.pt/~rcamorim/ant/park.php",
                null, ParkingFragment.this, ParkingFragment.this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("items", new Gson().toJson(parkItems));
    }
}
