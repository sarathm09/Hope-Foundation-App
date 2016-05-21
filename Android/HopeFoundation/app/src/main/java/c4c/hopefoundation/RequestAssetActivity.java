package c4c.hopefoundation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;

import c4c.hopefoundation.adapters.AssetsManagerAdapter;
import c4c.hopefoundation.adapters.RequestManagerAdapter;

public class RequestAssetActivity extends Activity {

    SearchableSpinner category, assetType;
    String categories[] = {"Furniture", "Electronics", "Stationary", "Lab Equipment", "Other"};
    String cat_furniture[] = {"Table","Chair","Cupboard"};
    String cat_electronics[] = {"Laptop","CPU","Monitor","Projector","Printer","KeyBoard"};
    String cat_stationary[] = {"WhiteBoard", "Marker"};
    String cat_equipment[] = {"Test Tube", "Microscope", "Globe"};
    String cat_other[] = {"test"};
    RecyclerView requestList;
    private ArrayAdapter<String> assetTypeArrayAdapter;
    String[] data[] = {cat_furniture, cat_electronics, cat_stationary, cat_equipment, cat_other};
    int selection = 0;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_asset);

        requestList = (RecyclerView) findViewById(R.id.request_list);
        requestList.setHasFixedSize(true);
        requestList.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");

        initSpinners();


    }

    private void initSpinners() {

        category = (SearchableSpinner) findViewById(R.id.request_category);
        assetType = (SearchableSpinner) findViewById(R.id.request_assettype);

        ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryArrayAdapter);

        category.setTitle("Select Category");
        assetType.setTitle("Select Asset Type");
        assetType.setEnabled(false);

        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selection = i;
                assetType.setEnabled(true);
                switch (i) {
                    case 0:
                        // furniture
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RequestAssetActivity.this, android.R.layout.simple_spinner_item, cat_furniture);
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                    case 1:
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RequestAssetActivity.this, android.R.layout.simple_spinner_item, cat_electronics);
                        // electronics
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                    case 2:
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RequestAssetActivity.this, android.R.layout.simple_spinner_item, cat_stationary);
                        //stationary
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                    case 3:
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RequestAssetActivity.this, android.R.layout.simple_spinner_item, cat_equipment);
                        //equipment
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                    case 4:
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RequestAssetActivity.this, android.R.layout.simple_spinner_item, cat_other);
                        //other
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        assetType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Ion.with(RequestAssetActivity.this)
                        .load("http://c4c.rootone.xyz/assets_availability.php?location=" + location + "&assetType=" + data[selection][i])
                        .asJsonArray()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonArray>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonArray> result) {
                                if (e == null) {
                                    try {
                                        JSONArray results = new JSONArray(result.getResult().toString());
                                        requestList.setAdapter(new RequestManagerAdapter(results, RequestAssetActivity.this, RequestAssetActivity.this, requestList));
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }
}
