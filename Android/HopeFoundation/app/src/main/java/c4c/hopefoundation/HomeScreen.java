package c4c.hopefoundation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeScreen extends AppCompatActivity {

    private String location;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");
        user = pref.getString("user", "");

        ((TextView) findViewById(R.id.homescreen_location)).setText(location);

        applyTheme();
        addNavigationBar();
        fetchMetadata();

        findViewById(R.id.homescreen_asset_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this, AssetsManager.class));
            }
        });
        findViewById(R.id.homescreen_detailsbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(HomeScreen.this, QRReaderActivity.class), 1);
            }
        });
        findViewById(R.id.homescreen_trans_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this, TransactionsHistoryActivity.class));
            }
        });
        findViewById(R.id.homescreen_transferbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this, RequestAcceptPage.class));
            }
        });
        findViewById(R.id.homescreen_notifbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(HomeScreen.this, ScanCodeCollector.class));
            }
        });

    }

    private void fetchMetadata() {
        Ion.with(this)
                .load("http://c4c.rootone.xyz/get_location_overview.php?location=" + location)
                .asJsonArray()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonArray>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonArray> result) {
                        if (e == null) {
                            try {
                                JSONArray arr = new JSONArray(result.getResult().toString());
                                JSONObject obj = arr.getJSONObject(0);

                                ((TextView) findViewById(R.id.homescreen_total_assets)).setText(obj.getString("TotalCount"));
                                ((TextView) findViewById(R.id.homescreen_inuse)).setText(obj.getString("InUse"));
                                ((TextView) findViewById(R.id.homescreen_unused)).setText(obj.getString("NotInUse"));
                                ((TextView) findViewById(R.id.homescreen_damaged)).setText(obj.getString("Damaged"));
                                ((TextView) findViewById(R.id.homescreen_shipping)).setText(obj.getString("Shipping"));

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void addNavigationBar() {

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_bg)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(user)
                                .withEmail(location)
                                .withIcon(getResources().getDrawable(R.drawable.ic_profile))
                )
                .build();

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar((Toolbar) findViewById(R.id.homescreen_toolbar))
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Account Settings"),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Transaction History"),
                        new PrimaryDrawerItem().withName("Categories"),
                        new PrimaryDrawerItem().withName("Contact Us"),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Logout"),
                        new PrimaryDrawerItem().withName("Exit")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == 3) {
                            startActivity(new Intent(HomeScreen.this, TransactionsHistoryActivity.class));
                        } else if (position == 7) {
                            SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
                            pref.edit().putBoolean("loggedin", false).apply();
                            startActivity(new Intent(HomeScreen.this, LoginPage.class));
                            finish();
                        } else if (position == 8) {
                            finish();
                        }
                        return false;
                    }
                })
                .build();
    }

    private void applyTheme() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission_group.CAMERA}, 200);
            int permissionCheck = checkSelfPermission(Manifest.permission.CAMERA);
        }

        SystemBarTintManager tm = new SystemBarTintManager(this);
        tm.setTintColor(getResources().getColor(R.color.primary_dark));
        tm.setNavigationBarTintEnabled(true);
        tm.setStatusBarTintEnabled(true);

        ((Toolbar) findViewById(R.id.homescreen_toolbar)).setBackgroundColor(getResources().getColor(R.color.primary_dark));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result;
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                result=data.getStringExtra("result");
                Log.d("C4C", "QR: " + result);
                Intent i = new Intent(this, AssetDetailsPage.class);
                i.putExtra("id", result);
                startActivity(i);
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

    @Override
    protected void onResume() {
        super.onResume();
        fetchMetadata();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchMetadata();
    }
}
