package c4c.hopefoundation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        SharedPreferences pref = getSharedPreferences("cred", MODE_PRIVATE);
        if(pref.getBoolean("loggedin", false)){
            startActivity(new Intent(LoginPage.this, HomeScreen.class));
        }

        final TextView userId = (TextView) findViewById(R.id.userid);
        final TextView password = (TextView) findViewById(R.id.password);
        final CircularProgressButton circularProgressButton = (CircularProgressButton) findViewById(R.id.loginBtn);
        circularProgressButton.setIndeterminateProgressMode(true);
        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            //login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JsonObject json = new JsonObject();
                json.addProperty("username", userId.getText().toString());
                json.addProperty("password", password.getText().toString());

                if (circularProgressButton.getProgress() == 0) {
                    circularProgressButton.setProgress(50);//50
                    Ion.with(LoginPage.this)
                            .load("http://c4c.rootone.xyz/login_validation.php")
                            .setJsonObjectBody(json)
                            .asJsonArray()
                            .withResponse()
                            .setCallback(new FutureCallback<Response<JsonArray>>() {
                                @Override
                                public void onCompleted(Exception e, Response<JsonArray> result) {
                                    if (result.getResult() != null) {
                                        try {
                                            JSONArray results = new JSONArray(result.getResult().toString());
                                            JSONObject cred = results.getJSONObject(0);
                                            circularProgressButton.setProgress(100);
                                            SharedPreferences preferences = getSharedPreferences("cred", MODE_PRIVATE);
                                            preferences.edit().putString("user", cred.getString("UserName")).apply();
                                            preferences.edit().putString("loc", cred.getString("Location")).apply();
                                            preferences.edit().putBoolean("loggedin", true).apply();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(LoginPage.this, HomeScreen.class));
                                                }
                                            }, 1000);


                                        } catch (JSONException e1) {
                                            Toast toast = Toast.makeText(LoginPage.this, "error", Toast.LENGTH_LONG);
                                            toast.show();
                                            e1.printStackTrace();
                                            circularProgressButton.setProgress(-1);
                                        }
                                    } else {
                                        circularProgressButton.setProgress(-1);
                                        userId.setText("");
                                        password.setText("");
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                circularProgressButton.setProgress(0);
                                            }
                                        }, 1500);
                                    }
                                }
                            });
                }
            }
        });
    }
}
