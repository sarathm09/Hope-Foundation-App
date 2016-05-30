package c4c.hopefoundation;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class RegisterNewAsset extends Activity {

    SearchableSpinner category, assetType;
    EditText source, trans, desc, depreciation;
    MaterialNumberPicker quant;
    LinearLayout sourceBlock, transBlock;
    Button internal, external;
    String sourceType = "INTERNAL";

    String location;
    ArrayAdapter<String> assetTypeArrayAdapter;
    CircularProgressButton registerBtn;

    String categories[] = {"Furniture", "Electronics", "Stationary", "Lab Equipment", "Other"};
    String cat_furniture[] = {"Table", "Chair", "Cupboard"};
    String cat_electronics[] = {"Laptop", "CPU", "Monitor", "Projector", "Printer", "Key Board"};
    String cat_stationary[] = {"WhiteBoard", "Marker"};
    String cat_equipment[] = {"Test Tube", "Microscope", "Globe"};
    String cat_other[] = {"test"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_asset);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");

        initUi();
    }

    private void initUi() {

        category = (SearchableSpinner) findViewById(R.id.register_category);
        assetType = (SearchableSpinner) findViewById(R.id.register_assettype);
        quant = (MaterialNumberPicker) findViewById(R.id.register_quantity);
        source = (EditText) findViewById(R.id.register_source);
        desc = (EditText) findViewById(R.id.register_desc);
        sourceBlock = (LinearLayout) findViewById(R.id.register_source_block);
        transBlock = (LinearLayout) findViewById(R.id.register_transaction_block);
        internal = (Button) findViewById(R.id.request_button_internal);
        external = (Button) findViewById(R.id.request_button_external);
        trans = (EditText) findViewById(R.id.register_payment_id);
        depreciation = (EditText) findViewById(R.id.register_depreciation);

        transBlock.setVisibility(View.GONE);
        applyTheme();

        internal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                external.setBackgroundColor(getResources().getColor(R.color.divider));
                internal.setBackgroundColor(getResources().getColor(R.color.primary));
                transBlock.setVisibility(View.VISIBLE);
                sourceBlock.setVisibility(View.GONE);
                sourceType = "INTERNAL";
            }
        });
        external.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                external.setBackgroundColor(getResources().getColor(R.color.primary));
                internal.setBackgroundColor(getResources().getColor(R.color.divider));
                transBlock.setVisibility(View.GONE);
                sourceBlock.setVisibility(View.VISIBLE);
                sourceType = "EXTERNAL";
            }
        });

        ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryArrayAdapter);

        category.setTitle("Select Category");
        assetType.setTitle("Select Asset Type");
        assetType.setEnabled(false);

        registerBtn = (CircularProgressButton) findViewById(R.id.register_registerbutton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerBtn.setIndeterminateProgressMode(true);
                registerBtn.setProgress(50);
                String cat = category.getSelectedItem().toString();
                String asset = assetType.getSelectedItem().toString();
                String quantity = String.valueOf(quant.getValue());
                String sourc = source.getText().toString();
                String trId = trans.getText().toString();
                String descVal = desc.getText().toString();

                JsonObject json = new JsonObject();
                json.addProperty("category", cat);
                json.addProperty("assetType", asset);
                json.addProperty("location", location);
                json.addProperty("quantity", quantity);
                json.addProperty("source", sourc);
                json.addProperty("specifications", descVal);
                json.addProperty("owner", location);
                json.addProperty("status", "NOT_IN_USE");
                json.addProperty("type", sourceType);
                json.addProperty("depreciation", depreciation.getText().toString());

                Ion.with(RegisterNewAsset.this)
                        .load("http://c4c.rootone.xyz/register_assets.php")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                try {
                                    Toast.makeText(RegisterNewAsset.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                    registerBtn.setProgress(100);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            Notification n  = new Notification.Builder(RegisterNewAsset.this)
                                                    .setContentTitle("New Assets registered")
                                                    .setContentText("Please check your mail for the QR codes")
                                                    .setSmallIcon(R.drawable.ic_add_white_24dp)
                                                    .setAutoCancel(false).build();
                                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            notificationManager.notify(0, n);

                                            RegisterNewAsset.this.finish();

                                        }
                                    }, 1000);
                                } catch (Exception e2) {
                                    registerBtn.setProgress(-1);
                                }
                            }
                        });
            }
        });


        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                assetType.setEnabled(true);
                switch (i) {
                    case 0:
                        // furniture
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RegisterNewAsset.this, android.R.layout.simple_spinner_item, cat_furniture);
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                    case 1:
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RegisterNewAsset.this, android.R.layout.simple_spinner_item, cat_electronics);
                        // electronics
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                    case 2:
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RegisterNewAsset.this, android.R.layout.simple_spinner_item, cat_stationary);
                        //stationary
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                    case 3:
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RegisterNewAsset.this, android.R.layout.simple_spinner_item, cat_equipment);
                        //equipment
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                    case 4:
                        assetTypeArrayAdapter = new ArrayAdapter<String>(RegisterNewAsset.this, android.R.layout.simple_spinner_item, cat_other);
                        //other
                        assetType.setAdapter(assetTypeArrayAdapter);
                        break;
                }

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

        ((TextView)findViewById(R.id.register_title_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/subheading.ttf"));

        ((Toolbar) findViewById(R.id.register_toolbar)).setBackgroundColor(getResources().getColor(R.color.primary_dark));

        findViewById(R.id.register_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
