package c4c.hopefoundation.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.cocosw.bottomsheet.BottomSheet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import c4c.hopefoundation.R;
import c4c.hopefoundation.RegisterNewAsset;
import c4c.hopefoundation.ScanCodeCollector;

/**
 * Created by I323294 on 5/19/2016.
 */
public class AssetsManagerAdapter extends RecyclerView.Adapter<AssetsManagerAdapter.ViewHolder> {
    private static JSONArray mDataset;
    public static Context context;
    public static Activity activity;
    static RecyclerView assetsList;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;

        TextView type, breadcrumb, count, inuse, unused, damaged;
        ImageView itembg, itemfab;

        public ViewHolder(View v) {
            super(v);
            view = v;

            type = (TextView) v.findViewById(R.id.asset_item_asset_type);
            breadcrumb = (TextView) v.findViewById(R.id.asset_item_breadcrumb);
            count = (TextView) v.findViewById(R.id.asset_item_count_assets);
            inuse = (TextView) v.findViewById(R.id.asset_item_inuse_assets);
            unused = (TextView) v.findViewById(R.id.asset_item_unused_assets);
            damaged = (TextView) v.findViewById(R.id.asset_item_damaged_assets);

            itembg = (ImageView) v.findViewById(R.id.asset_item_image);
            itemfab = (ImageView) v.findViewById(R.id.asset_item_fab);

            v.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            int index = assetsList.getChildPosition(view);
            JSONObject offer = null;
            try {
                offer = mDataset.getJSONObject(index);
                new BottomSheet.Builder(activity)
                        .title(offer.getString("AssetType"))
                        .sheet(R.menu.asset_options)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.assets_mark_damage:
                                        Intent i = new Intent(context, ScanCodeCollector.class);
                                        i.putExtra("referrer", "mark_damage");
                                        context.startActivity(i);
                                        break;
                                    case R.id.assets_mark_unused:
                                        Intent i2 = new Intent(context, ScanCodeCollector.class);
                                        i2.putExtra("referrer", "mark_used");
                                        context.startActivity(i2);
                                        break;
                                    case R.id.assets_delete:

                                        break;
                                }
                            }
                        })
                        .show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public AssetsManagerAdapter(JSONArray myDataset, Activity activity, Context c, RecyclerView offersList) {
        this.mDataset = myDataset;
        this.context = c;
        this.activity = activity;
        this.assetsList = offersList;
    }

    @Override
    public AssetsManagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_asset_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject offer = mDataset.getJSONObject(position);

            Picasso.with(context)
                    .load(R.drawable.drawer_bg)
                    .fit()
                    .into(holder.itembg);
            holder.type.setText(offer.getString("AssetType"));
            holder.breadcrumb.setText(offer.getString("Category") + " > " + offer.getString("AssetType"));
//            holder.description.setText(offer.getString("description"));
            holder.count.setText(offer.getString("Count"));
            holder.inuse.setText(offer.getString("InUseCount"));
            holder.unused.setText(offer.getString("UnusedCount"));
            holder.damaged.setText(offer.getString("DamagedCount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length();
    }
}
