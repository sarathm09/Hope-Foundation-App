package c4c.hopefoundation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

import c4c.hopefoundation.adapters.AssetsManagerAdapter;

public class AssetsManager extends Activity {

    RecyclerView assetList;
    private String location;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_manager);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");
        user = pref.getString("user", "");

//        addNavigationBar();
        applyTheme();
        initialiseList();

        findViewById(R.id.asset_add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AssetsManager.this, RegisterNewAsset.class));
            }
        });
        findViewById(R.id.assets_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initialiseList() {

        assetList = (RecyclerView) findViewById(R.id.asset_manager_asset_list);
        assetList.setLayoutManager(new GridLayoutManager(this, 2));
        assetList.setHasFixedSize(true);

        fetchData();
    }

    private void fetchData() {
        Ion.with(this)
                .load("http://c4c.rootone.xyz/get_assets.php?location=" + location)
                .asJsonArray()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonArray>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonArray> result) {
                        if (e == null) {
                            try {
                                JSONArray results = new JSONArray(result.getResult().toString());
                                assetList.setAdapter(new AssetsManagerAdapter(results, AssetsManager.this, AssetsManager.this, assetList));
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
                .withToolbar((Toolbar) findViewById(R.id.assets_toolbar))
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
                            startActivity(new Intent(AssetsManager.this, TransactionsHistoryActivity.class));
                        } else if (position == 7) {
                            SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
                            pref.edit().putBoolean("loggedin", false).apply();
                            startActivity(new Intent(AssetsManager.this, LoginPage.class));
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

        SystemBarTintManager tm = new SystemBarTintManager(this);
        tm.setTintColor(getResources().getColor(R.color.primary_dark));
        tm.setNavigationBarTintEnabled(true);
        tm.setStatusBarTintEnabled(true);

        ((TextView)findViewById(R.id.asset_manager_title_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/heading.ttf"));

        ((Toolbar) findViewById(R.id.assets_toolbar)).setBackgroundColor(getResources().getColor(R.color.primary_dark));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }
}
