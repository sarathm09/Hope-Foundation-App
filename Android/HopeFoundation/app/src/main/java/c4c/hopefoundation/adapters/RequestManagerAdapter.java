package c4c.hopefoundation.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import c4c.hopefoundation.R;

/**
 * Created by I323294 on 5/19/2016.
 */
public class RequestManagerAdapter extends RecyclerView.Adapter<RequestManagerAdapter.ViewHolder> {

    private static JSONArray mDataset;
    public static Context context;
    public static Activity activity;
    static RecyclerView assetsList;
    private String breadCrumb;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View view;

        TextView locName, qty, breadcrumb, requestedVal;
        LinearLayout status;
        public static HashMap<String, Integer> requestBody = new HashMap<>();

        public ViewHolder(View v) {
            super(v);
            view = v;

            locName = (TextView) v.findViewById(R.id.request_item_location);
            qty = (TextView) v.findViewById(R.id.request_item_count);
            breadcrumb = (TextView) v.findViewById(R.id.request_item_breadcrumb);
            requestedVal = (TextView) v.findViewById(R.id.request_item_requested_count);
            status = (LinearLayout) v.findViewById(R.id.request_item_status);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            int index = assetsList.getChildPosition(view);
            try {
                JSONObject data = mDataset.getJSONObject(index);
                final String loc = data.getString("Location");
                int maxCount = Integer.parseInt(data.getString("AvailableCount"));

                final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(context)
                        .minValue(0)
                        .maxValue(maxCount)
                        .defaultValue(1)
                        .backgroundColor(Color.WHITE)
                        .separatorColor(Color.TRANSPARENT)
                        .textColor(Color.BLACK)
                        .textSize(20)
                        .enableFocusability(false)
                        .wrapSelectorWheel(true)
                        .build();
                new AlertDialog.Builder(context)
                        .setTitle("Number of assets to request from " + loc)
                        .setView(numberPicker)
                        .setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((TextView) view.findViewById(R.id.request_item_requested_count)).setText(numberPicker.getValue() + "");
                                requestBody.put(loc, numberPicker.getValue());

                                Log.d("C4C: adapter", requestBody.toString());

                                if(numberPicker.getValue() != 0){
                                    view.findViewById(R.id.request_item_status).setBackgroundColor(context.getResources().getColor(R.color.status_green));
                                }else{
                                    view.findViewById(R.id.request_item_status).setBackgroundColor(context.getResources().getColor(R.color.status_grey));
                                }
                            }
                        })
                        .show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public RequestManagerAdapter(JSONArray myDataset, Activity activity, Context c, RecyclerView offersList, String breadCrumb) {
        this.mDataset = myDataset;
        this.context = c;
        this.activity = activity;
        this.assetsList = offersList;
        this.breadCrumb = breadCrumb;
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
            holder.breadcrumb.setText(this.breadCrumb);
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.status_grey));
            holder.requestedVal.setText("0");

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
