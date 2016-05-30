package c4c.hopefoundation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class QRReaderActivity extends Activity {

    private QRCodeReaderView mydecoderview;
    SearchableSpinner numSelect;
    boolean firstTym = true;
    ArrayList<String> assetList;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);

        assetList = new ArrayList<>();
        assetList.add("Select Asset");
        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qr_decoder_view);
        numSelect = (SearchableSpinner) findViewById(R.id.qr_decoder_id_select);
        numSelect.setTitle("Select Asset");
        numSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (firstTym) {
                    firstTym = false;
                } else if(i!=0){
                    String id = String.valueOf(assetList.get(i)).split(":")[0];
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", id);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mydecoderview.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String text, PointF[] points) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", text);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void cameraNotFound() {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Camera not found");
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }

            @Override
            public void QRCodeNotFoundOnCamImage() {
            }
        });

        Ion.with(this)
                .load("http://c4c.rootone.xyz/asset_list.php?location=" + location)
                .asJsonArray()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonArray>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonArray> result) {
                        try {
                            JSONArray arr = new JSONArray(result.getResult().toString());
                            for(int i=0; i<arr.length(); i++){
                                JSONObject asset = arr.getJSONObject(i);
                                assetList.add(asset.getString("ID") + ": " + asset.getString("AssetType"));
                            }
                            ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<>(QRReaderActivity.this, android.R.layout.simple_spinner_item, assetList);
                            numSelect.setAdapter(categoryArrayAdapter);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "Cancelled");
        setResult(Activity.RESULT_CANCELED, returnIntent);
        mydecoderview.getCameraManager().stopPreview();
        finish();
    }
}
