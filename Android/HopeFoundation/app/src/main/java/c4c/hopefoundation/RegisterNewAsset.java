package c4c.hopefoundation;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.transition.CircularPropagation;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.vi.swipenumberpicker.OnValueChangeListener;
import com.vi.swipenumberpicker.SwipeNumberPicker;

public class RegisterNewAsset extends Activity {

    SearchableSpinner category, assetType;
    MaterialEditText source;
    EditText quant;
    String location;
    ArrayAdapter<String> assetTypeArrayAdapter;
    CircularProgressButton registerBtn;

    String categories[] = {"Furniture", "Electronics", "Stationary", "Lab Equipment", "Other"};
    String cat_furniture[] = {"Table","Chair","Cupboard"};
    String cat_electronics[] = {"Laptop","CPU","Monitor","Projector","Printer","KeyBoard"};
    String cat_stationary[] = {"WhiteBoard", "Marker"};
    String cat_equipment[] = {"Test Tube", "Microscope", "Globe"};
    String cat_other[] = {"test"};

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_asset);


            location = "Whitefield";

        initSpinners();
        registerBtn = (CircularProgressButton) findViewById(R.id.register_registerbutton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerBtn.setIndeterminateProgressMode(true);
                registerBtn.setProgress(50);
                String cat = category.getSelectedItem().toString();
                String asset = assetType.getSelectedItem().toString();
                String quantity = String.valueOf(quant.getText());
                String sourc = source.getText().toString();

                JsonObject json = new JsonObject();
                json.addProperty("category", cat);
                json.addProperty("assetType", asset);
                json.addProperty("location", location);
                json.addProperty("quantity", quantity);
                json.addProperty("source", sourc);

                Ion.with(RegisterNewAsset.this)
                        .load("http://c4c.rootone.xyz/register_assets.php")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                try{
                                    Toast.makeText(RegisterNewAsset.this, result.getResult().get("message").toString(), Toast.LENGTH_SHORT).show();
                                    registerBtn.setProgress(100);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            RegisterNewAsset.this.finish();
                                        }
                                    }, 1000);
                                }catch (Exception e2){
                                    registerBtn.setProgress(-1);
                                }
                            }
                        });
            }
        });

    }

    private void initSpinners() {

        category = (SearchableSpinner) findViewById(R.id.register_category);
        assetType = (SearchableSpinner) findViewById(R.id.register_assettype);
        quant = (EditText) findViewById(R.id.register_quantity);
        source = (MaterialEditText) findViewById(R.id.register_source);

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
}
