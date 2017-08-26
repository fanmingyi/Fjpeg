package org.jpegutil.fjpeg.enumerate;

/**
 * Created by FMY on 2017/8/26.
 */

public class JpegConst {
    //文件读写错误
    public static final int FILE_ERROR = 893;
    //压缩期间的错误
    public static final int INTERNAL_ERROR = FILE_ERROR+1;
    //Bitmap格式错误
    public static final int  BITMAP_FOMAT_ERROR= INTERNAL_ERROR+1;
    //Bitmap宽高错误
    public static final int  BITMAP_HEIGHT_WIDTH_ERROR= BITMAP_FOMAT_ERROR+1;
}
