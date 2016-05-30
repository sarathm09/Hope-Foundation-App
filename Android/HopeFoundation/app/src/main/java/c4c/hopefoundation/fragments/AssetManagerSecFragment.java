package c4c.hopefoundation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;

import c4c.hopefoundation.R;
import c4c.hopefoundation.adapters.AssetsManagerAdapter;

/**
 * Created by I323294 on 5/27/2016.
 */
public class AssetManagerSecFragment extends Fragment {

    View view;
    Context context;
    String location;
    RecyclerView assetList;

    RecyclerView trList;

    public void setContext(Context context) {
        this.context = context;
    }

    public AssetManagerSecFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_asset_manager_sec, container, false);
        initUi();
        return view;
    }

    private void initUi() {

        SharedPreferences pref = context.getSharedPreferences("cred", context.MODE_PRIVATE);
        location = pref.getString("loc", "");
        assetList = (RecyclerView) view.findViewById(R.id.asset_manager_list_sec);
        assetList.setLayoutManager(new GridLayoutManager(context, 2));
        assetList.setHasFixedSize(true);



        Ion.with(this)
                .load("http://c4c.rootone.xyz/get_assets.php?location=" + location + "&filter=transferred")
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
