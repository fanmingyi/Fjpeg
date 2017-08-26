package org.jpegutil.fjpeg;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.jpegutil.fjpeg.minterface.NativeCallBack;

import java.io.File;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);


    }


    public void onClick(View view) {
        //for test1
//        Bitmap bitmap = ImageUtils.compressPxSampleSize(getResources(), R.mipmap.timg, 10, 10);
//        iv.setImageBitmap(bitmap);

        //for test2
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timg);
        File file = new File(Environment.getExternalStorageDirectory(), "fmy测试.jpg");
        ImageUtils.compressQC(file.getAbsolutePath(), bitmap, 1, true, new NativeCallBack() {
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
        });

    }
}
