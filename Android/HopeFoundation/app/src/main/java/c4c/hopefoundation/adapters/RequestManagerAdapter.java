package c4c.hopefoundation.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import c4c.hopefoundation.R;

/**
 * Created by I323294 on 5/19/2016.
 */
public class RequestManagerAdapter extends RecyclerView.Adapter<RequestManagerAdapter.ViewHolder> {
    private static JSONArray mDataset;
    public static Context context;
    public static Activity activity;
    static RecyclerView assetsList;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;

        TextView locName, qty;

        public ViewHolder(View v) {
            super(v);
            view = v;

            locName = (TextView) v.findViewById(R.id.request_loc);
            qty = (TextView) v.findViewById(R.id.request_count);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int index = assetsList.getChildPosition(view);
            DialogPlus dialog = DialogPlus.newDialog(context)
                    .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.request_confirm))
                    .setExpanded(true)
                    .create();
            final View v = dialog.getHolderView();
            v.findViewById(R.id.request_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String qty = ((EditText) v.findViewById(R.id.request_confirm_num)).getText().toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Successfully placed request", Toast.LENGTH_SHORT).show();
                        }
                    },1000);

                }
            });
            dialog.show();

        }
    }
    public RequestManagerAdapter(JSONArray myDataset, Activity activity, Context c, RecyclerView offersList) {
        this.mDataset = myDataset;
        this.context = c;
        this.activity = activity;
        this.assetsList = offersList;
    }

    @Override
    public RequestManagerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_request_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject offer = mDataset.getJSONObject(position);

            holder.locName.setText(offer.getString("Location"));
            holder.qty.setText(offer.getString("AvailableCount"));


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
