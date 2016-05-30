package c4c.hopefoundation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import c4c.hopefoundation.R;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * Created by I323294 on 5/27/2016.
 */
public class TransactionHistoryOverviewFragment extends Fragment{

    View view;
    Context context;
    String location;
    PieChart chart;
    int nonLocCount = 0;
    PieDataSet dataSet;
    ArrayList<Entry> chartVals;
    private TableView recentTransTable;
    private ArrayList<String[]> tableData;

    public void setContext(Context context) {
        this.context = context;
    }

    public TransactionHistoryOverviewFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_trans_manager_overview, container, false);
        chart = (PieChart) view.findViewById(R.id.trans_overview_chart);
        recentTransTable = (TableView) view.findViewById(R.id.trans_overview_table);
        initUi();
        return view;
    }

    private void initUi() {

        SharedPreferences pref = context.getSharedPreferences("cred", context.MODE_PRIVATE);
        location = pref.getString("loc", "");
        tableData = new ArrayList<>();

        recentTransTable.setHeaderAdapter(new SimpleTableHeaderAdapter(context, "Time", "AssetType", "Items"));
        recentTransTable.setColumnWeight(0, 3);
        recentTransTable.setColumnWeight(1, 2);
        recentTransTable.setColumnWeight(2, 1);
        setChartData();

    }

    private void setChartData() {
        SharedPreferences pref = context.getSharedPreferences("cred", context.MODE_PRIVATE);
        location = pref.getString("loc", "");
        chart.setCenterTextColor(getResources().getColor(R.color.primary_dark));
        chart.setCenterTextSize((float) 17.0);
        chart.setUsePercentValues(false);
        chart.setHoleRadius(50);
        chart.setCenterText("");
        chart.setTransparentCircleRadius(55);
        chart.setTransparentCircleAlpha(120);
        chart.setDescription("");

        chart.setDrawSliceText(true);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        legend.setTextSize(15f);

        chartVals = new ArrayList<>();

        Ion.with(this)
                .load("http://c4c.rootone.xyz/trans_overview.php?location=" + location)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (e == null) {
                            try {
                                JSONObject obj = new JSONObject(result.getResult().toString());
                                chartVals.clear();

                                ArrayList<String> chartLabels = new ArrayList<>();
                                JSONArray chartArray = obj.getJSONArray("count");
                                for(int ci = 0; ci< chartArray.length(); ci++){
                                    chartVals.add(new Entry(Integer.parseInt(chartArray.getJSONObject(ci).getString("count")), ci));
                                    chartLabels.add(chartArray.getJSONObject(ci).getString("loc"));
                                    if(!chartArray.getJSONObject(ci).getString("loc").equalsIgnoreCase(location)){
                                        nonLocCount++;
                                    }
                                }
                                JSONArray tableArrray = obj.getJSONArray("trans");
                                for(int ci = 0; ci< tableArrray.length(); ci++){
                                    tableData.add(new String[]{
                                            tableArrray.getJSONObject(ci).getString("TimeStamp"),
                                            tableArrray.getJSONObject(ci).getString("assetType"),
                                            tableArrray.getJSONObject(ci).getString("count")});
                                }

                                JSONArray meta = obj.getJSONArray("meta");
                                for(int ci = 0; ci< meta.length(); ci++){
                                    ((TextView) view.findViewById(R.id.trans_overview_requests)).setText(meta.getJSONObject(ci).getString("req"));
                                    ((TextView) view.findViewById(R.id.trans_overview_total)).setText(meta.getJSONObject(ci).getString("total"));
                                    ((TextView) view.findViewById(R.id.trans_overview_other_loc)).setText(nonLocCount + "");
                                }

                                recentTransTable.setDataAdapter(new SimpleTableDataAdapter(context, tableData));

                                dataSet = new PieDataSet(chartVals, "");
                                dataSet.setSliceSpace(3f);
                                dataSet.setSelectionShift(5f);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
