package c4c.hopefoundation;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import c4c.hopefoundation.adapters.RequestManagerAdapter;

public class RequestAssetActivity extends Activity {

    SearchableSpinner category, assetType;
    RecyclerView requestList;
    CircularProgressButton requestBtn;

    String categories[] = {"Furniture", "Electronics", "Stationary", "Lab Equipment", "Other"};
    String cat_furniture[] = {"Table","Chair","Cupboard"};
    String cat_electronics[] = {"Laptop","CPU","Monitor","Projector","Printer","Key Board"};
    String cat_stationary[] = {"WhiteBoard", "Marker"};
    String cat_equipment[] = {"Test Tube", "Microscope", "Globe"};
    String cat_other[] = {"test"};

    private ArrayAdapter<String> assetTypeArrayAdapter;
    String[] data[] = {cat_furniture, cat_electronics, cat_stationary, cat_equipment, cat_other};
    int selection = 0;
    String location;
    private String selAssetType;
    private String selCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_asset);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");

        initUi();

    }

    private void initUi() {

        applyTheme();
        findViewById(R.id.requests_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        requestList = (RecyclerView) findViewById(R.id.request_list);
        requestList.setHasFixedSize(true);
        requestList.setLayoutManager(new GridLayoutManager(this, 2));

        requestBtn = (CircularProgressButton) findViewById(R.id.request_request_button);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestBtn.setIndeterminateProgressMode(true);
                requestBtn.setProgress(10);
                Boolean found = false;
                if(RequestManagerAdapter.ViewHolder.requestBody == null){
                    requestBtn.setProgress(-1);
                    Toast.makeText(RequestAssetActivity.this, "No Asset Selected", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestBtn.setProgress(0);
                        }
                    }, 1000);
                }else{
                    Log.d("C4C", RequestManagerAdapter.ViewHolder.requestBody.toString());
                    for(int count: RequestManagerAdapter.ViewHolder.requestBody.values()){
                        if(count > 0)
                            found = true;
                    }
                    if(!found){
                        requestBtn.setProgress(-1);
                        Toast.makeText(RequestAssetActivity.this, "No Asset Selected", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestBtn.setProgress(0);
                            }
                        }, 1000);
                    }else{
                        Log.d("C4C", RequestManagerAdapter.ViewHolder.requestBody.toString());
                        for (HashMap.Entry<String, Integer> entry : RequestManagerAdapter.ViewHolder.requestBody.entrySet()) {
                            String loc = entry.getKey();
                            Integer count = entry.getValue();

                            JsonObject json = new JsonObject();
                            json.addProperty("assetType", selAssetType);
                            json.addProperty("category", selCategory);
                            json.addProperty("from", location);
                            json.addProperty("to", loc);
                            json.addProperty("quantity", count.toString());

                            if(count != 0){
                                Ion.with(RequestAssetActivity.this)
                                        .load("http://c4c.rootone.xyz/request_assets.php")
                                        .setJsonObjectBody(json)
                                        .asJsonObject()
                                        .withResponse()
                                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                                            @Override
                                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                                requestBtn.setProgress(100);
                                                Toast.makeText(RequestAssetActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        RequestAssetActivity.this.finish();
                                                    }
                                                }, 1000);

                                            }
                                        });
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestBtn.setProgress(0);
                            }
                        }, 1000);
                    }
                }
            }
        });

        // Init Spinners
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
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {

                selAssetType = data[selection][i];
                selCategory = categories[selection];

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
                                        if (results.length() == 0) {
                                            requestList.setVisibility(View.GONE);
                                            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            requestList.setVisibility(View.VISIBLE);
                                            findViewById(R.id.empty_view).setVisibility(View.GONE);
                                            String breadCrumb = categories[selection] + " -> " + data[selection][i];
                                            requestList.setAdapter(new RequestManagerAdapter(results, RequestAssetActivity.this, RequestAssetActivity.this, requestList, breadCrumb));
                                        }
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

    private void applyTheme() {
        SystemBarTintManager tm = new SystemBarTintManager(this);
        tm.setTintColor(getResources().getColor(R.color.primary_dark));
        tm.setNavigationBarTintEnabled(true);
        tm.setStatusBarTintEnabled(true);

        ((TextView)findViewById(R.id.request_title_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/heading.ttf"));
        ((TextView)findViewById(R.id.request_subtitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/subheading.ttf"));

        ((Toolbar) findViewById(R.id.request_toolbar)).setBackgroundColor(getResources().getColor(R.color.primary_dark));
    }
}
