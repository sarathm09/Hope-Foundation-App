package c4c.hopefoundation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class QRReaderActivity extends Activity {

    private QRCodeReaderView mydecoderview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qr_decoder_view);
        mydecoderview.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String text, PointF[] points) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", text);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void cameraNotFound() {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Camera not found");
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }

            @Override
            public void QRCodeNotFoundOnCamImage() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "Cancelled");
        setResult(Activity.RESULT_CANCELED, returnIntent);
        mydecoderview.getCameraManager().stopPreview();
        finish();
    }
}
