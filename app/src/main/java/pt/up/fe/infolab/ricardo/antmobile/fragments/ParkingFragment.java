package pt.up.fe.infolab.ricardo.antmobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.LineNumberReader;
import java.util.ArrayList;

import pt.up.fe.infolab.ricardo.antmobile.AppController;
import pt.up.fe.infolab.ricardo.antmobile.R;
import pt.up.fe.infolab.ricardo.antmobile.models.ParkItem;

public class ParkingFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONArray> {

    private ArrayList<ParkItem> parkItems;
    private TextView tvParkingStatus;
    private Button btRefresh;

    private ProgressBar p1Progress;
    private ProgressBar p3Progress;
    private ProgressBar p4Progress;

    private TextView p1Status;
    private TextView p3Status;
    private TextView p4Status;

    private LinearLayout llParkingData;

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

        p1Progress = (ProgressBar) rootView.findViewById(R.id.p1_progress);
        p3Progress = (ProgressBar) rootView.findViewById(R.id.p3_progress);
        p4Progress = (ProgressBar) rootView.findViewById(R.id.p4_progress);

        p1Status = (TextView) rootView.findViewById(R.id.p1_slots);
        p3Status = (TextView) rootView.findViewById(R.id.p3_slots);
        p4Status = (TextView) rootView.findViewById(R.id.p4_slots);

        llParkingData = (LinearLayout) rootView.findViewById(R.id.parking_data);

        if (parkItems != null && !parkItems.isEmpty()) {
            attacthParkInfo();
        } else {
            llParkingData.setVisibility(View.INVISIBLE);
            AppController.getInstance().addToRequestQueue(getParkRequest());
        }

        rootView.findViewById(R.id.parking_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btRefresh.setEnabled(false);
                llParkingData.setVisibility(View.INVISIBLE);
                AppController.getInstance().addToRequestQueue(getParkRequest());
            }
        });
        return rootView;
    }

    private void attacthParkInfo() {
        int p1Free = parkItems.get(0).getLugares();
        int p3Free = parkItems.get(1).getLugares();
        int p4Free = parkItems.get(2).getLugares();

        p1Progress.setProgress((p1Free/500)*100);
        p3Progress.setProgress((p3Free/325)*100);
        p4Progress.setProgress((p4Free/71)*100);

        p1Status.setText(p1Free + " Livres");
        p3Status.setText(p3Free + " Livres");
        p4Status.setText(p4Free + " Livres");

        llParkingData.setVisibility(View.VISIBLE);
        btRefresh.setEnabled(true);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        llParkingData.setVisibility(View.INVISIBLE);
        Log.e("VOLLEY", error.toString());
        tvParkingStatus.setText(getString(R.string.volley_error));
        btRefresh.setEnabled(true);
    }

    @Override
    public void onResponse(JSONArray response) {
        parkItems = new Gson().fromJson(response.toString(), new TypeToken<ArrayList<ParkItem>>() {
        }.getType());

        attacthParkInfo();
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
