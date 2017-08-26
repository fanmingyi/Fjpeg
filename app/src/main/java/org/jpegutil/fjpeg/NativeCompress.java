package org.jpegutil.fjpeg;

import android.graphics.Bitmap;

import org.jpegutil.fjpeg.minterface.NativeCallBack;

/**
 * Created by FMY on 2017/8/25.
 */

public class NativeCompress {

    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 采用libjpeg压缩图片
     *
     * @param outpath          用哈夫曼压缩后文件保存路径
     * @param bitmap           需要压缩的bitmap图片
     * @param CompressionRatio 质量1-100 1表示最低质量
     * @param isUseHoffman     是否使用哈夫曼编码
     */
    public static native void nativeLibJpegCompress(String outpath, Bitmap bitmap, int CompressionRatio, boolean isUseHoffman, NativeCallBack nativeCallBack);

}
