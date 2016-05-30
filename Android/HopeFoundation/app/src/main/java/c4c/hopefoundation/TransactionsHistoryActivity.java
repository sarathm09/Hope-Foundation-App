package c4c.hopefoundation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gigamole.library.navigationtabstrip.NavigationTabStrip;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;

import c4c.hopefoundation.fragments.AssetManagerPrimaryFragment;
import c4c.hopefoundation.fragments.TransactionHistoryOverviewFragment;
import c4c.hopefoundation.fragments.TransactionHistoryPrimaryFragment;
import c4c.hopefoundation.fragments.TransactionHistorySecFragment;

public class TransactionsHistoryActivity extends AppCompatActivity {

    com.melnykov.fab.FloatingActionButton fab;
    String location;
    ViewPager transViewPager;
    NavigationTabStrip navigationTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_history);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");

        initUi();
        applyTheme();

        findViewById(R.id.trans_history_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initUi() {

        transViewPager = (ViewPager) findViewById(R.id.trans_history_viewpager);
        transViewPager.setAdapter(new TransVPAdapter(getSupportFragmentManager()));

        navigationTabStrip = (NavigationTabStrip) findViewById(R.id.trans_history_nts);
        navigationTabStrip.setViewPager(transViewPager);
        navigationTabStrip.setTitles("Overview", "Internal", "External");
        navigationTabStrip.setTabIndex(0, true);
        navigationTabStrip.setTitleSize(35);
        navigationTabStrip.setStripColor(getResources().getColor(R.color.fab_red));
        navigationTabStrip.setStripWeight(9);
        navigationTabStrip.setStripFactor(2);
        navigationTabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        navigationTabStrip.setStripGravity(NavigationTabStrip.StripGravity.BOTTOM);
        navigationTabStrip.setCornersRadius(10);
        navigationTabStrip.setAnimationDuration(300);
        navigationTabStrip.setInactiveColor(Color.GRAY);
        navigationTabStrip.setActiveColor(Color.WHITE);

        transViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navigationTabStrip.setTabIndex(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        transViewPager.setCurrentItem(0);

        fab = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.transaction_history_download);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ion.with(TransactionsHistoryActivity.this)
                        .load("http://c4c.rootone.xyz/export_csv.php?location=" + location)
                        .progress(new ProgressCallback() {
                            @Override
                            public void onProgress(long downloaded, long total) {}
                        })
                        .write(new File("/sdcard/data.csv"))
                        .setCallback(new FutureCallback<File>() {
                            @Override
                            public void onCompleted(Exception e, File file) {
                                Toast.makeText(TransactionsHistoryActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    class TransVPAdapter extends FragmentStatePagerAdapter{

        public TransVPAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    TransactionHistoryOverviewFragment ov = new TransactionHistoryOverviewFragment();
                    ov.setContext(getApplicationContext());
                    return (ov);
                case 1:
                    TransactionHistoryPrimaryFragment prim = new TransactionHistoryPrimaryFragment();
                    prim.setContext(getApplicationContext());
                    return (prim);
                case 2:
                    TransactionHistorySecFragment sec = new TransactionHistorySecFragment();
                    sec.setContext(getApplicationContext());
                    return (sec);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void applyTheme() {

        SystemBarTintManager tm = new SystemBarTintManager(this);
        tm.setTintColor(getResources().getColor(R.color.primary_dark));
        tm.setNavigationBarTintEnabled(true);
        tm.setStatusBarTintEnabled(true);

        ((TextView)findViewById(R.id.trans_history_title_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/subheading.ttf"));

        ((Toolbar) findViewById(R.id.trans_history_toolbar)).setBackgroundColor(getResources().getColor(R.color.primary_dark));
    }
}
