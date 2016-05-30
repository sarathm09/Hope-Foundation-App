package c4c.hopefoundation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gigamole.library.navigationtabstrip.NavigationTabStrip;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;

import c4c.hopefoundation.R;
import c4c.hopefoundation.TransactionsHistoryActivity;
import c4c.hopefoundation.adapters.AssetsManagerAdapter;
import c4c.hopefoundation.adapters.TransactionManagerAdapter;

public class AssetManagerPrimaryFragment extends Fragment {

    View view;
    Context context;
    String location;
    RecyclerView assetList;

    RecyclerView trList;

    public void setContext(Context context) {
        this.context = context;
    }

    public AssetManagerPrimaryFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_asset_manager_primary, container, false);
        initUi();
        return view;
    }

    private void initUi() {

        SharedPreferences pref = context.getSharedPreferences("cred", context.MODE_PRIVATE);
        location = pref.getString("loc", "");
        assetList = (RecyclerView) view.findViewById(R.id.asset_manager_list_primary);
        assetList.setLayoutManager(new GridLayoutManager(context, 2));
        assetList.setHasFixedSize(true);


        String data="[{\"AssetType\":\"CPU\",\"Category\":\"Electronics\",\"ImageUrl\":\"http:\\/\\/c4c.rootone.xyz\\/Images\\/cpu.png\",\"Count\":\"3\",\"InUseCount\":\"3\",\"DamagedCount\":\"0\",\"UnusedCount\":\"0\"},{\"AssetType\":\"Laptop\",\"Category\":\"Electronics\",\"ImageUrl\":\"http:\\/\\/c4c.rootone.xyz\\/Images\\/laptop.png\",\"Count\":\"6\",\"InUseCount\":\"6\",\"DamagedCount\":\"0\",\"UnusedCount\":\"0\"},{\"AssetType\":\"Microscope\",\"Category\":\"Lab Equipment\",\"ImageUrl\":\"http:\\/\\/c4c.rootone.xyz\\/Images\\/microscope.png\",\"Count\":\"4\",\"InUseCount\":\"2\",\"DamagedCount\":\"0\",\"UnusedCount\":\"2\"},{\"AssetType\":\"Monitor\",\"Category\":\"Electronics\",\"ImageUrl\":\"http:\\/\\/c4c.rootone.xyz\\/Images\\/monitor.png\",\"Count\":\"21\",\"InUseCount\":\"20\",\"DamagedCount\":\"0\",\"UnusedCount\":\"1\"},{\"AssetType\":\"Printer\",\"Category\":\"Electronics\",\"ImageUrl\":\"http:\\/\\/c4c.rootone.xyz\\/Images\\/printer.png\",\"Count\":\"4\",\"InUseCount\":\"0\",\"DamagedCount\":\"0\",\"UnusedCount\":\"4\"},{\"AssetType\":\"Projector\",\"Category\":\"Electronics\",\"ImageUrl\":\"http:\\/\\/c4c.rootone.xyz\\/Images\\/projector.png\",\"Count\":\"8\",\"InUseCount\":\"7\",\"DamagedCount\":\"0\",\"UnusedCount\":\"1\"},{\"AssetType\":\"WhiteBoard\",\"Category\":\"Stationary\",\"ImageUrl\":\"http:\\/\\/c4c.rootone.xyz\\/Images\\/whiteboard.png\",\"Count\":\"2\",\"InUseCount\":\"2\",\"DamagedCount\":\"0\",\"UnusedCount\":\"0\"}]";
        

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
                                assetList.setAdapter(new AssetsManagerAdapter(results, getActivity(), context, assetList));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
}
