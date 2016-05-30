package c4c.hopefoundation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.gigamole.library.navigationtabstrip.NavigationTabStrip;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import c4c.hopefoundation.fragments.AssetManagerPrimaryFragment;
import c4c.hopefoundation.fragments.AssetManagerSecFragment;

public class AssetsManager extends AppCompatActivity {

    private String location;
    private String user;
    ViewPager assetViewPager;
    NavigationTabStrip navigationTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets_manager);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");
        user = pref.getString("user", "");

        applyTheme();
        initUi();

    }

    private void initUi() {

        assetViewPager = (ViewPager) findViewById(R.id.asset_manager_viewpager);
        assetViewPager.setAdapter(new AssetManagerAdapter(getSupportFragmentManager()));

        navigationTabStrip = (NavigationTabStrip) findViewById(R.id.asset_manager_nts);
        navigationTabStrip.setViewPager(assetViewPager);
        navigationTabStrip.setTitles("All", "Transferred");
        navigationTabStrip.setTabIndex(0, true);
        navigationTabStrip.setTitleSize(35);
        navigationTabStrip.setStripColor(getResources().getColor(R.color.fab_red));
        navigationTabStrip.setStripWeight(9);
        navigationTabStrip.setStripFactor(2);
        navigationTabStrip.setStripType(NavigationTabStrip.StripType.LINE);
        navigationTabStrip.setStripGravity(NavigationTabStrip.StripGravity.BOTTOM);
        navigationTabStrip.setCornersRadius(3);
        navigationTabStrip.setAnimationDuration(300);
        navigationTabStrip.setInactiveColor(Color.GRAY);
        navigationTabStrip.setActiveColor(Color.WHITE);

        findViewById(R.id.assets_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BottomSheet.Builder(AssetsManager.this)
                        .title("Edit Assets")
                        .sheet(R.menu.asset_options)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.assets_mark_damage:
                                        Intent i = new Intent(AssetsManager.this, ScanCodeCollector.class);
                                        i.putExtra("referrer", "mark_damage");
                                        startActivity(i);
                                        break;
                                    case R.id.assets_mark_unused:
                                        Intent i2 = new Intent(AssetsManager.this, ScanCodeCollector.class);
                                        i2.putExtra("referrer", "mark_used");
                                        startActivity(i2);
                                        break;
                                    case R.id.assets_delete:

                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        assetViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        assetViewPager.setCurrentItem(0);

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

    class AssetManagerAdapter extends FragmentStatePagerAdapter {

        public AssetManagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    AssetManagerPrimaryFragment prim = new AssetManagerPrimaryFragment();
                    prim.setContext(getApplicationContext());
                    return (prim);
                case 1:
                    AssetManagerSecFragment sec = new AssetManagerSecFragment();
                    sec.setContext(getApplicationContext());
                    return (sec);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private void applyTheme() {

        SystemBarTintManager tm = new SystemBarTintManager(this);
        tm.setTintColor(getResources().getColor(R.color.primary_dark));
        tm.setNavigationBarTintEnabled(true);
        tm.setStatusBarTintEnabled(true);

        ((TextView)findViewById(R.id.asset_manager_title_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/subheading.ttf"));

        ((Toolbar) findViewById(R.id.assets_toolbar)).setBackgroundColor(getResources().getColor(R.color.primary_dark));
    }
}
