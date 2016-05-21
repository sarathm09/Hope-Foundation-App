package c4c.hopefoundation.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import c4c.hopefoundation.R;

/**
 * Created by I323294 on 5/19/2016.
 */
public class TransactionManagerAdapter extends RecyclerView.Adapter<TransactionManagerAdapter.ViewHolder> {
    private static JSONArray mDataset;
    public static Context context;
    public static Activity activity;
    static RecyclerView assetsList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        TextView fromTo, tId, asName, category, asId, time;

        public ViewHolder(View v) {
            super(v);
            view = v;

            fromTo = (TextView) v.findViewById(R.id.transaction_item_from_to);
            tId = (TextView) v.findViewById(R.id.transaction_item_tid);
            asName = (TextView) v.findViewById(R.id.transaction_item_asset_type);
            category = (TextView) v.findViewById(R.id.transaction_item_category);
            asId = (TextView) v.findViewById(R.id.transaction_item_asset_id);
            time = (TextView) v.findViewById(R.id.transaction_item_date);

        }
    }
    public TransactionManagerAdapter(JSONArray myDataset, Activity activity, Context c, RecyclerView offersList) {
        this.mDataset = myDataset;
        this.context = c;
        this.activity = activity;
        this.assetsList = offersList;
    }

    @Override
    public TransactionManagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.veiw_transaction_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject offer = mDataset.getJSONObject(position);

            holder.fromTo.setText(offer.getString("FromLocation") + " --> " + offer.getString("ToLocation"));
            holder.asName.setText(offer.getString("AssetType"));
            holder.time.setText(offer.getString("TimeStamp"));
            holder.tId.setText(offer.getString("TID"));
            holder.asId.setText(offer.getString("AssetID"));
            holder.category.setText(offer.getString("Category"));

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
