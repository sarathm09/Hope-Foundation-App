package c4c.hopefoundation;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
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

import java.util.ArrayList;
import java.util.Arrays;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class HomeScreen extends Activity {

    private String location;
    private String user;
    private int notifCount;
    PieChart chart;
    PieDataSet dataSet;
    ArrayList<Entry> chartVals;
    private ArrayList<String> chartLabels = new ArrayList<>(Arrays.asList("Used", "Unused", "Damaged", "Transferred"));
    ArrayList<Integer> chartColors = new ArrayList<>(Arrays.asList(
            Color.argb(150, 57, 73, 171),
            Color.argb(150, 84, 110, 122),
            Color.argb(150, 255, 138, 101),
            Color.argb(150, 141, 110, 99)));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");
        user = pref.getString("user", "");

        SharedPreferences notif = getSharedPreferences("notif", MODE_PRIVATE);
        notifCount = notif.getInt("count", 0);

        if(notifCount > 0){
            ((ImageView) findViewById(R.id.homescreen_notif_icon)).setImageResource(R.drawable.notif_color);
        }
        ((TextView) findViewById(R.id.homescreen_location)).setText(location);
        ((TextView) findViewById(R.id.homescreen_notif_count)).setText(notifCount + "");


        applyTheme();
        addNavigationBar();
        manageButtonClicks();
//        setChartData();

    }

    private void setChartData() {
        chart = (PieChart) findViewById(R.id.homescreen_chart);

        chart.setCenterText("Asset Stats");
        chart.setCenterTextColor(getResources().getColor(R.color.primary_dark));
        chart.setCenterTextSize((float) 17.0);
        chart.setUsePercentValues(false);
        chart.setHoleRadius(50);
        chart.setTransparentCircleRadius(55);
        chart.setTransparentCircleAlpha(120);
        chart.setDescription("");

        chart.setDrawSliceText(false);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);
        legend.setTextSize(5f);

        chartVals = new ArrayList<>();

        Ion.with(this)
                .load("http://c4c.rootone.xyz/dummyJsons/dashboard.json")
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e == null) {
                            try {
                                JSONObject obj = new JSONObject(result.getResult().toString());
                                chartVals.clear();

                                ((TextView) findViewById(R.id.homescreen_total_assets)).setText(obj.getString("TotalCount"));

                                chartVals.add(new Entry(Integer.parseInt(obj.getString("InUse")), 0));
                                chartVals.add(new Entry(Integer.parseInt(obj.getString("NotInUse")), 1));
                                chartVals.add(new Entry(Integer.parseInt(obj.getString("Damaged")), 2));
                                chartVals.add(new Entry(Integer.parseInt(obj.getString("Borrowed")) + Integer.parseInt(obj.getString("Lent")), 3));

                                dataSet = new PieDataSet(chartVals, "");
                                dataSet.setSliceSpace(3f);
                                dataSet.setSelectionShift(5f);
                                dataSet.setColors(chartColors);
                                PieData data = new PieData(chartLabels, dataSet);
                                data.setValueFormatter(new ValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                        return String.valueOf(value).replace(".0", "");
                                    }
                                });

                                data.setValueTextSize(8f);
                                data.setValueTextColor(getResources().getColor(R.color.primary_text));
                                chart.setData(data);
                                chart.highlightValues(null);

                                chart.animateX(3000, Easing.EasingOption.EaseOutCirc);

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void manageButtonClicks() {
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
                new BottomSheet.Builder(HomeScreen.this)
                        .title("Select the transaction type")
                        .sheet(R.menu.transfer_options)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i;
                                switch (which) {
                                    case R.id.assets_transfer_create:
                                        i = new Intent(HomeScreen.this, RegisterNewAsset.class);
                                        startActivity(i);
                                        break;
                                    case R.id.assets_transfer_recieve:
                                        i = new Intent(HomeScreen.this, ScanCodeCollector.class);
                                        i.putExtra("referrer", "receive");
                                        startActivity(i);
                                        break;
                                    case R.id.assets_transfer_request:
                                        i = new Intent(HomeScreen.this, RequestAssetActivity.class);
                                        startActivity(i);
                                        break;
                                }
                            }
                        })
                        .icon(R.drawable.ic_swap_horiz_black_24dp)
                        .grid()
                        .build()
                        .show();
            }
        });
        findViewById(R.id.homescreen_contact_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(HomeScreen.this, ScanCodeCollector.class));
            }
        });
        findViewById(R.id.homescreen_notif_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(HomeScreen.this, ScanCodeCollector.class));
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
                        new PrimaryDrawerItem().withName("Notifications"),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Register Assets"),
                        new PrimaryDrawerItem().withName("Recieve Assets"),/*
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Contact Us"),*/
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("Logout"),
                        new PrimaryDrawerItem().withName("Exit")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                SharedPreferences p = getSharedPreferences("notif", MODE_PRIVATE);
                                p.edit().putInt("count", 7).apply();
                                break;
                            case 2:
                                break;
                            case 3:
                                startActivity(new Intent(HomeScreen.this, TransactionsHistoryActivity.class));
                                break;
                            case 4:
//                                startActivity(new Intent(HomeScreen.this, TransactionsHistoryActivity.class));
                                break;
                            case 5:
                                break;
                            case 6:
                                startActivity(new Intent(HomeScreen.this, RegisterNewAsset.class));
                                break;
                            case 7:
//                                startActivity(new Intent(HomeScreen.this, .class));
                                break;
                            case 8:
                                break;
                            case 9:
                                break;
                            case 10:
                                break;
                            case 11:
                                SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
                                pref.edit().putBoolean("loggedin", false).apply();
                                startActivity(new Intent(HomeScreen.this, LoginPage.class));
                                finish();
                                break;
                            case 12:
                                finish();
                                break;
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

        ((TextView)findViewById(R.id.homescreen_title_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/heading.ttf"));
        ((TextView)findViewById(R.id.homescreen_location)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/subheading.ttf"));

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
    protected void onStart() {
        super.onStart();
        setChartData();
        chart.animateX(3000, Easing.EasingOption.EaseOutCirc);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");
        user = pref.getString("user", "");

        SharedPreferences notif = getSharedPreferences("notif", MODE_PRIVATE);
        notifCount = notif.getInt("count", 0);
    }

}
