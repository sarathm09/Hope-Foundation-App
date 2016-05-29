package c4c.hopefoundation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import c4c.hopefoundation.TransactionsHistoryActivity;
import c4c.hopefoundation.adapters.TransactionManagerAdapter;

public class AssetManagerPrimaryFragment extends Fragment {

    View view;
    Context context;
    String location;

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

        trList = (RecyclerView) view.findViewById(R.id.transactions_list);

        trList.setLayoutManager(new LinearLayoutManager(context));
        trList.setHasFixedSize(true);
        Ion.with(this)
                .load("http://c4c.rootone.xyz/transaction_history.php?location=" + location)
                .asJsonArray()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonArray>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonArray> result) {
                        if (e == null) {
                            try {
                                JSONArray results = new JSONArray(result.getResult().toString());
                                trList.setAdapter(new TransactionManagerAdapter(results, getActivity(), context, trList));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
    }
}
