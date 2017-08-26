package org.jpegutil.example;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.jpegutil.fjpeg.ImageUtils;
import org.jpegutil.fjpeg.minterface.NativeCallBack;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private static final String TAG = "MainActivity";
    public void onClick(View view) {
        iv = (ImageView) findViewById(R.id.iv);

        Bitmap bitmap = ImageUtils.compressPxSampleSize(getResources(), R.mipmap.timg, 1000, 1000);
        iv.setImageBitmap(bitmap);

//        ImageUtils.compressPxSampleSize()
        ImageUtils.compressQC(new File(Environment.getExternalStorageDirectory(), "测试剥离框架.jpg").getAbsolutePath(), bitmap, 1, true, new NativeCallBack() {
            @Override
            public void startCompress() {
                Log.d(TAG, "startCompress() called");
            }

            @Override
            public void error(int errorNum, String description) {
                Log.d(TAG, "error() called with: errorNum = [" + errorNum + "], description = [" + description + "]");
            }

            @Override
            public void finish(String filePath) {
                Log.d(TAG, "finish() called with: filePath = [" + filePath + "]");
            }
        },true);

    }
}
