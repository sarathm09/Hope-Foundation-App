package c4c.hopefoundation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;

import c4c.hopefoundation.adapters.TransactionManagerAdapter;

public class TransactionsHistoryActivity extends Activity {

    com.melnykov.fab.FloatingActionButton fab;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_history);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        location = pref.getString("loc", "");

        initList();
    }

    private void initList() {


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
}
