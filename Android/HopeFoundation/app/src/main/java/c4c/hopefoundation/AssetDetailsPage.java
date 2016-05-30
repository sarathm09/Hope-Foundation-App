package c4c.hopefoundation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

import c4c.hopefoundation.adapters.TransactionManagerAdapter;

public class AssetDetailsPage extends Activity implements OnMapReadyCallback {

    RecyclerView trList;
    int id;
    GoogleMap gmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_details_page);


        id = Integer.parseInt(String.valueOf(getIntent().getExtras().get("id")));
        Log.d("C4C", id +"");
        Toast.makeText(this, "" + id, Toast.LENGTH_SHORT).show();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initList();
    }

    private void initList() {
        trList = (RecyclerView) findViewById(R.id.transactions_list);

        trList.setLayoutManager(new LinearLayoutManager(this));
        trList.setHasFixedSize(true);
        SharedPreferences pref = getSharedPreferences("loc", MODE_PRIVATE);
        final String locdata = pref.getString("data","");

        Ion.with(this)
                .load("http://c4c.rootone.xyz/get_asset_data.php?id=" + id)
                .asJsonArray()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonArray>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonArray> result) {
                        if (e == null) {
                            try {
                                JSONArray results = new JSONArray(result.getResult().toString());

                                ((TextView) findViewById(R.id.asset_details_category)).setText(results.getJSONObject(0).getString("Category"));
                                ((TextView) findViewById(R.id.asset_details_status)).setText(results.getJSONObject(0).getString("Status"));
                                ((TextView) findViewById(R.id.asset_details_type)).setText(results.getJSONObject(0).getString("AssetType") + "( "+ results.getJSONObject(0).getString("Category") +" )");
                                ((TextView) findViewById(R.id.asset_details_details)).setText(results.getJSONObject(0).getString("Specifications"));
                                ((TextView) findViewById(R.id.asset_details_DepreciationValue)).setText(results.getJSONObject(0).getString("DepreciationValue"));
                                ((TextView) findViewById(R.id.asset_details_owner)).setText(results.getJSONObject(0).getString("Owner"));

                                if (locdata != "") {
                                    JSONArray mapLoc = new JSONArray(locdata);
                                    for (int i=0;i<results.length(); i++){
                                        String loc = results.getJSONObject(i).getString("Location");
                                        for(int j=0; j<mapLoc.length(); j++){
                                            if(mapLoc.getJSONObject(j).getString("Location").equalsIgnoreCase(loc)){
                                                gmap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(Double.parseDouble(mapLoc.getJSONObject(j).getString("lat")),
                                                                Double.parseDouble(mapLoc.getJSONObject(j).getString("lng"))))
                                                        .title(mapLoc.getJSONObject(j).getString("Location")));
                                                gmap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(mapLoc.getJSONObject(j).getString("lat")),
                                                        Double.parseDouble(mapLoc.getJSONObject(j).getString("lng")))));
                                            }
                                        }
                                    }

                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gmap = googleMap;

        Ion.with(this)
                .load("http://c4c.rootone.xyz/get_asset_transaction_history.php?id=" + id)
                .asJsonArray()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonArray>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonArray> result) {
                        if (e == null) {
                            try {
                                JSONArray results = new JSONArray(result.getResult().toString());
                                trList.setAdapter(new TransactionManagerAdapter(results, AssetDetailsPage.this, getApplicationContext(), trList));

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);

        gmap.moveCamera(CameraUpdateFactory.zoomTo(14));
    }
}
