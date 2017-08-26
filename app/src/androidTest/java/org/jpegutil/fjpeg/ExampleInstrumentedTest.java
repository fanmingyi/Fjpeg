package org.jpegutil.fjpeg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.DisplayMetrics;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DisplayMetrics displayMetrics = appContext.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        int densityDpi = displayMetrics.densityDpi;




        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.mipmap.timg);



        Log.e("fmy","位图格式==>>"+bitmap.getConfig().name());
        Log.e("fmy","图片大小==>>"+bitmap.getByteCount());
        Log.e("fmy","density==>>"+density);
        Log.e("fmy","densityDpi==>>"+densityDpi);
    }
}
