package org.jpegutil.fjpeg;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import org.jpegutil.fjpeg.minterface.NativeCallBack;

import java.io.InputStream;

/**
 * Created by FMY on 2017/8/25.
 */
public class ImageUtils {

    //用于打印日志
    private static boolean flag = true;

    private static final String TAG = "ImageUtils";

    static {
        if ("debug".equals(BuildConfig.BUILD_TYPE)) {
            flag = true;
        } else {
            flag = false;
        }


    }

    /**
     * 把图片转化为指定的ARGB8888格式
     *
     * @param bit 你想转化图片
     * @return
     */
    public static Bitmap changeARGB8888(Bitmap bit) {
        Log.d("native", "compress of native");
        if (bit.getConfig() != Bitmap.Config.ARGB_8888) {
            Bitmap result;

            result = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());
            canvas.drawBitmap(bit, null, rect, null);

            return result;


        } else {
            return bit;
        }
    }


    private static void e(String TAG, String description) {
        if (flag) {
            Log.e(TAG, description);
        }
    }

    /**
     * 质量压缩
     *
     * @param outpath          用哈夫曼压缩后文件保存路径
     * @param bitmap           需要压缩的bitmap图片
     * @param CompressionRatio 质量1-100    1是最低质量
     * @param isUseHoffman     是否使用哈夫曼编码
     * @param nativeCallBack   回调 如果是子线程调用那么回调在子线程
     * @param useNewThread     是否使用子线程去完成
     */
    public static void compressQC(final String outpath, final Bitmap bitmap, final int CompressionRatio, final boolean isUseHoffman, final NativeCallBack nativeCallBack, boolean useNewThread) {
        if (useNewThread) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    nativecompress(bitmap, nativeCallBack, outpath, CompressionRatio, isUseHoffman);

                }
            }.start();
        } else {
            nativecompress(bitmap, nativeCallBack, outpath, CompressionRatio, isUseHoffman);
        }



    }

    private static void nativecompress(Bitmap bitmap, NativeCallBack nativeCallBack, String outpath, int CompressionRatio, boolean isUseHoffman) {
        Bitmap bitmapBack = bitmap;
        if (bitmapBack == null) {
            e(TAG, "CompressQC==>>传入参数 bitmap 为空");
            return;
        }
        //校验格式
        if (bitmapBack.getConfig() != Bitmap.Config.ARGB_8888) {
            bitmapBack = changeARGB8888(bitmapBack);
        }
        if (nativeCallBack != null) {
            nativeCallBack.startCompress();
        }
        NativeCompress.nativeLibJpegCompress(outpath, bitmapBack, CompressionRatio, isUseHoffman, nativeCallBack);
    }

    /**
     * 通过缩放图片像素来减少图片占用内存大小
     *
     * @param data       图片字节数据
     * @param destWidth  宽
     * @param destHeight 高
     * @return
     */
    public static Bitmap compressPxSampleSize(byte[] data, int destWidth, int destHeight) {
        //第一次采样
        BitmapFactory.Options options = new BitmapFactory.Options();
        //该属性设置为true只会加载图片的边框进来，并不会加载图片具体的像素点
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        compressPxOptoion(destWidth, destHeight, options);

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * 通过缩放图片像素来减少图片占用内存大小
     *
     * @param is
     * @param destWidth
     * @param destHeight
     * @return
     */
    public static Bitmap compressPxSampleSize(InputStream is, int destWidth, int destHeight) {
        //第一次采样
        BitmapFactory.Options options = new BitmapFactory.Options();
        //该属性设置为true只会加载图片的边框进来，并不会加载图片具体的像素点
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        compressPxOptoion(destWidth, destHeight, options);

        return BitmapFactory.decodeStream(is, null, options);
    }

    /**
     * 通过缩放图片像素来减少图片占用内存大小
     *
     * @param resources
     * @param bitmapId   图片资源id
     * @param destWidth  目标宽度
     * @param destHeight 目标高度
     * @return
     */
    public static Bitmap compressPxSampleSize(Resources resources, int bitmapId, int destWidth, int destHeight) {
        //第一次采样
        BitmapFactory.Options options = new BitmapFactory.Options();
        //该属性设置为true只会加载图片的边框进来，并不会加载图片具体的像素点
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, bitmapId, options);
        compressPxOptoion(destWidth, destHeight, options);

        return BitmapFactory.decodeResource(resources, bitmapId, options);
    }

    /**
     * @param filePath   要加载的图片路径
     * @param destWidth  显示图片的控件宽度
     * @param destHeight 显示图片的控件的高度
     *                   通过缩放图片像素来减少图片占用内存大小
     * @return
     */
    public static Bitmap compressPxSampleSize(String filePath, int destWidth, int destHeight) {
        //第一次采样
        BitmapFactory.Options options = new BitmapFactory.Options();
        //该属性设置为true只会加载图片的边框进来，并不会加载图片具体的像素点
        options.inJustDecodeBounds = true;
        //第一次加载图片，这时只会加载图片的边框进来，并不会加载图片中的像素点
        BitmapFactory.decodeFile(filePath, options);
        compressPxOptoion(destWidth, destHeight, options);
        //加载图片并返回
        return BitmapFactory.decodeFile(filePath, options);
    }

    private static void compressPxOptoion(int destWidth, int destHeight, BitmapFactory.Options options) {
        //获得原图的宽和高
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        //定义缩放比例
        int sampleSize = 1;
        while (outHeight / sampleSize > destHeight || outWidth / sampleSize > destWidth) {
            //如果宽高的任意一方的缩放比例没有达到要求，都继续增大缩放比例
            //sampleSize应该为2的n次幂，如果给sampleSize设置的数字不是2的n次幂，那么系统会就近取值
            sampleSize *= 2;
        }
        /********************************************************************************************/
        //至此，第一次采样已经结束，我们已经成功的计算出了sampleSize的大小
        /********************************************************************************************/
        //二次采样开始
        //二次采样时我需要将图片加载出来显示，不能只加载图片的框架，因此inJustDecodeBounds属性要设置为false
        options.inJustDecodeBounds = false;
        //设置缩放比例
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    }
}
