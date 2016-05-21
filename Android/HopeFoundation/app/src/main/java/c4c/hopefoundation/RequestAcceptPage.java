package c4c.hopefoundation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class RequestAcceptPage extends Activity {

    CardView rec, req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_accept_page);

        rec = (CardView) findViewById(R.id.accept_btn);
        req = (CardView) findViewById(R.id.request_btn);

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RequestAcceptPage.this, ScanCodeCollector.class);
                i.putExtra("referrer", "recieve");
                startActivity(i);
            }
        });

        req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(RequestAcceptPage.this, RequestAssetActivity.class));
            }
        });
    }

}
