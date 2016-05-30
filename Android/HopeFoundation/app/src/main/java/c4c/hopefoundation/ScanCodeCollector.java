package c4c.hopefoundation;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ScanCodeCollector extends Activity {

    String referrer = "";
    Button start, stop;
    TextView scanned;
    CardView status;
    static int comp = 0;
    String location;
    SortableTableView scannedList;
    ArrayList<String[]> tableData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_collector);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");

        referrer = getIntent().getExtras().getString("referrer");
        initialiseVars();
        applyTheme();

        findViewById(R.id.code_collect_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initialiseVars() {

        switch (referrer) {
            case "mark_damage":
                ((TextView) findViewById(R.id.code_collect_subtitle)).setText("Scan the Asset codes to \nmark as Damaged");
                break;
            case "mark_used":
                ((TextView) findViewById(R.id.code_collect_subtitle)).setText("Scan the Asset codes to \ntoggle Used Status");
                break;
            case "receive":
                ((TextView) findViewById(R.id.code_collect_subtitle)).setText("Scan the Asset codes to \naccept them");
                break;
        }

        start = (Button) findViewById(R.id.code_collect_start);
        stop = (Button) findViewById(R.id.code_collect_stop);
        scanned = (TextView) findViewById(R.id.code_collect_scanned);
        status = (CardView) findViewById(R.id.code_collect_status);
        scannedList = (SortableTableView) findViewById(R.id.code_collect_table);

        scannedList.setHeaderAdapter(new SimpleTableHeaderAdapter(this, "Sl", "AssetID", "AssetType"));

        status.setVisibility(View.INVISIBLE);
        scanned.setText("0");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ScanCodeCollector.this, QRReaderActivity.class), 200);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notification n  = new Notification.Builder(ScanCodeCollector.this)
                        .setContentTitle("Changes Made")
                        .setContentText("The requested changes have been made to the Asset(s).")
                        .setSmallIcon(R.drawable.ic_save_white_24dp)
                        .setAutoCancel(false).build();
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, n);

                Toast.makeText(ScanCodeCollector.this, "Changes Made", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result;
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                result = data.getStringExtra("result");
                if (isNumeric(result)) {
                    boolean alreadyScanned = false;
                    for(int i=0; i<tableData.size(); i++){
                        if(tableData.get(i)[0].equalsIgnoreCase(result)){
                            alreadyScanned = true;
                            Toast.makeText(ScanCodeCollector.this, "Code already Scanned", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(! alreadyScanned)
                        manageData(result);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                try {
                    result = data.getStringExtra("result");
                    Log.d("C4C", "C4C: Error: " + result);
                } catch (Exception e) {
                    Log.d("C4C", "C4C: Error: Cancelled");
                }
            }
        }
    }

    private void manageData(final String scannedVal) {
        switch (referrer) {
            case "mark_damage":
                Ion.with(this).load("http://c4c.rootone.xyz/mark_damaged_asset.php?id=" + scannedVal)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                if(String.valueOf(result.getResult().get("status")).equalsIgnoreCase("\"success\"")){
                                    status.setVisibility(View.VISIBLE);
                                    comp++;
                                    scanned.setText("" + comp);
                                    tableData.add(new String[]{comp + "", scannedVal, String.valueOf(result.getResult().get("AssetType"))});
                                    scannedList.setDataAdapter(new SimpleTableDataAdapter(ScanCodeCollector.this, tableData));
                                }else{
                                    Toast.makeText(ScanCodeCollector.this, "Error: " + String.valueOf(result.getResult().get("status")), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                break;
            case "mark_used":
                Ion.with(this)
                        .load("http://c4c.rootone.xyz/toggle_asset_usages.php?id=" + scannedVal)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                if(String.valueOf(result.getResult().get("status")).equalsIgnoreCase("\"success\"")){
                                    status.setVisibility(View.VISIBLE);
                                    comp++;
                                    scanned.setText("" + comp);
                                    tableData.add(new String[]{comp + "", scannedVal, String.valueOf(result.getResult().get("AssetType"))});
                                    scannedList.setDataAdapter(new SimpleTableDataAdapter(ScanCodeCollector.this, tableData));
                                }else{
                                    Toast.makeText(ScanCodeCollector.this, "Error: " + String.valueOf(result.getResult().get("status")), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case "receive":
                Ion.with(this)
                        .load("http://c4c.rootone.xyz/receive_assets.php?location=" + location + "&id=" + scannedVal)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                if(String.valueOf(result.getResult().get("status")).equalsIgnoreCase("\"success\"")){
                                    status.setVisibility(View.VISIBLE);
                                    comp++;
                                    scanned.setText("" + comp);
                                    tableData.add(new String[]{comp + "", scannedVal, String.valueOf(result.getResult().get("AssetType"))});
                                    scannedList.setDataAdapter(new SimpleTableDataAdapter(ScanCodeCollector.this, tableData));
                                }else{
                                    Toast.makeText(ScanCodeCollector.this, "Error: " + String.valueOf(result.getResult().get("status")), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                break;
        }
    }

    public static boolean isNumeric(String str) {
        try {
            int d = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void applyTheme() {

        SystemBarTintManager tm = new SystemBarTintManager(this);
        tm.setTintColor(getResources().getColor(R.color.primary_dark));
        tm.setNavigationBarTintEnabled(true);
        tm.setStatusBarTintEnabled(true);

        ((TextView)findViewById(R.id.code_collect_title_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/heading.ttf"));
        ((TextView)findViewById(R.id.code_collect_subtitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/subheading.ttf"));

        ((Toolbar) findViewById(R.id.code_collect_toolbar)).setBackgroundColor(getResources().getColor(R.color.primary_dark));
    }
}
