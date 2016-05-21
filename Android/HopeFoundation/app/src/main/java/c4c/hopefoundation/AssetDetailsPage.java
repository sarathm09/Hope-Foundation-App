package c4c.hopefoundation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

import c4c.hopefoundation.adapters.TransactionManagerAdapter;

public class AssetDetailsPage extends Activity {

    RecyclerView trList;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_details_page);

        id = Integer.parseInt(getIntent().getExtras().get("id").toString());

        initList();
    }

    private void initList() {
        trList = (RecyclerView) findViewById(R.id.transactions_list);

        trList.setLayoutManager(new LinearLayoutManager(this));
        trList.setHasFixedSize(true);

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
                                ((TextView) findViewById(R.id.asset_details_id)).setText(results.getJSONObject(0).getString("ID"));
                                ((TextView) findViewById(R.id.asset_details_category)).setText(results.getJSONObject(0).getString("Category"));
                                ((TextView) findViewById(R.id.asset_details_location)).setText(results.getJSONObject(0).getString("Location"));
                                ((TextView) findViewById(R.id.asset_details_status)).setText(results.getJSONObject(0).getString("Status"));

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });

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
                                ((TextView) findViewById(R.id.asset_details_type)).setText(results.getJSONObject(0).getString("AssetType"));
                                trList.setAdapter(new TransactionManagerAdapter(results, AssetDetailsPage.this, AssetDetailsPage.this, trList));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
}
