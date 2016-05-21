package c4c.hopefoundation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.vi.swipenumberpicker.SwipeNumberPicker;

import java.util.ArrayList;

public class ScanCodeCollector extends Activity {

    EditText qty;
    String referrer = "";
    Button start;
    TextView remaining;
    LinearLayout status;
    static int comp = 0;
    Boolean firstTym = true;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_code_collector);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");

        referrer = getIntent().getExtras().getString("referrer");
        initialiseVars();
    }

    private void initialiseVars() {
        qty = (EditText) findViewById(R.id.code_collect_quantity);
        start = (Button) findViewById(R.id.code_collect_start);
        remaining = (TextView) findViewById(R.id.code_collect_remaining);
        status = (LinearLayout) findViewById(R.id.code_collect_status);


        status.setVisibility(View.INVISIBLE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qty.setEnabled(false);
                if (firstTym) {
                    remaining.setText(qty.getText().toString());
                    firstTym = false;
                    status.setVisibility(View.VISIBLE);
                    if (Integer.parseInt(qty.getText().toString()) > 0) {
                        comp = Integer.parseInt(qty.getText().toString());
                    }
                }
                startActivityForResult(new Intent(ScanCodeCollector.this, QRReaderActivity.class), 200);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result;
        if (requestCode == 200) {
            if(resultCode == Activity.RESULT_OK){
                comp --;
                remaining.setText("" + comp);
                result = data.getStringExtra("result");
                Log.d("C4C", "QR: " + result + comp);
                manageData(result);
                if(comp == 0) {
                    start.setEnabled(false);
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                try{
                    result=data.getStringExtra("result");
                    Log.d("C4C", "C4C: Error: " + result);
                }catch (Exception e){
                    Log.d("C4C", "C4C: Error: Cancelled");
                }
            }
        }
    }

    private void manageData(String result) {
        switch (referrer){
            case "mark_damage":
                Ion.with(this).load("http://c4c.rootone.xyz/mark_damaged_asset.php?id=" + result)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                Toast.makeText(ScanCodeCollector.this, "Done", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case "mark_used":
                Ion.with(this)
                        .load("http://c4c.rootone.xyz/mark_unused_asset.php?id=" + result)
                        .asJsonObject()
                        .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        Toast.makeText(ScanCodeCollector.this, "Done", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case "recieve":
                Ion.with(this)
                        .load("http://c4c.rootone.xyz/receive_assets.php?location=" + location + "&id=" + result)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                Toast.makeText(ScanCodeCollector.this, "Done", Toast.LENGTH_SHORT).show();
                            }
                        });

                break;
        }
    }
}
