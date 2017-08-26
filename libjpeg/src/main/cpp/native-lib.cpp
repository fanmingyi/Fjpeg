#include <jni.h>
#include <string.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <stdio.h>
#include <android/bitmap.h>
#include <setjmp.h>
#include <math.h>
#include <stdint.h>
#include <time.h>
#include "mconst.h"
//告诉编译器以下文件是用c文件
extern "C" {
#include "jpeglib.h"
#include "jerror.h"		/* Common decls for cjpeg/djpeg applications */
#include "jmorecfg.h"	/* for version message */
#include "jconfig.h"
}
#define LOG_TAG "libjpeg"
#define LOGW(...)  __android_log_write(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
//全局引用
jobject callBack;
JNIEnv *menv;

//关闭资源调用此方法
void freeResource() {

    menv->DeleteGlobalRef(callBack);
    callBack = NULL;
    menv = NULL;
}

//定义一个别名 方便存储二进制数据
typedef typedef unsigned char BYTE;

#define true 1
#define false 0

char *error;
struct my_error_mgr {

    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

typedef struct my_error_mgr *my_error_ptr;



//错误的方法回调
METHODDEF(void) my_error_exit(j_common_ptr cinfo) {
    my_error_ptr myerr = (my_error_ptr) cinfo->err;
    (*cinfo->err->output_message)(cinfo);
    error = (char *) myerr->pub.jpeg_message_table[myerr->pub.msg_code];
//    LOGE("jpeg_message_table[%d]:%s", myerr->pub.msg_code,
//         myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
    // LOGE("addon_message_table:%s", myerr->pub.addon_message_table);
//  LOGE("SIZEOF:%d",myerr->pub.msg_parm.i[0]);
//  LOGE("sizeof:%d",myerr->pub.msg_parm.i[1]);
    jclass nativeCallBackClass = menv->FindClass("org/jpegutil/fjpeg/minterface/NativeCallBack");
    jmethodID errorMthodId = menv->GetMethodID(nativeCallBackClass, "error",
                                              "(ILjava/lang/String;)V");
    char description[100];
    sprintf(description, "jpeg_message_table[%d]:%s", myerr->pub.msg_code,
            myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
    if (callBack!=NULL) {

        menv->CallVoidMethod(callBack, errorMthodId, INTERNAL_ERROR,
                             menv->NewStringUTF(description));
    }


    //跳转setjmp 并且返回值为1结束
    longjmp(myerr->setjmp_buffer, 1);



}

int generateJPEG(BYTE *data, int w, int h, int quality,
                 const char *outfilename, jboolean optimize) {
    //回调java代码
    jclass nativeCallBackClass = menv->FindClass("org/jpegutil/fjpeg/minterface/NativeCallBack");
    //jpeg的结构体，保存的比如宽、高、位深、图片格式等信息，相当于java的类
    struct jpeg_compress_struct jcs;

    //当读完整个文件的时候就会回调my_error_exit这个退出方法。setjmp是一个系统级函数，是一个回调。
    struct my_error_mgr jem;
    jcs.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;
    //使用longjmp将跳转到这样
    if (setjmp(jem.setjmp_buffer)) {

        //关闭资源
        freeResource();
        return 0;
    }

    //初始化jsc结构体
    jpeg_create_compress(&jcs);
    //打开输出文件 wb:可写byte
    FILE *f = fopen(outfilename, "wb");
    if (f == NULL) {
//        LOGE("打开文件失败");
        jmethodID errorMthodId = menv->GetMethodID(nativeCallBackClass, "error",
                                                  "(ILjava/lang/String;)V");
        char description[100];
        sprintf(description, "以二进制打开读写文件路径[%s]失败",outfilename);
        if (callBack!=NULL) {

            menv->CallVoidMethod(callBack, errorMthodId,FILE_ERROR,
                                 menv->NewStringUTF(description));
        }
        //关闭资源
        freeResource();
        return 0;
    }

    //设置结构体的文件路径
    jpeg_stdio_dest(&jcs, f);
    jcs.image_width = w;//设置宽高
    jcs.image_height = h;
//	if (optimize) {
//		LOGI("optimize==ture");
//	} else {
//		LOGI("optimize==false");
//	}

    //看源码注释，设置哈夫曼编码：/* TRUE=arithmetic coding, FALSE=Huffman */
    jcs.arith_code = false;
    int nComponent = 3;
    /* 颜色的组成 rgb，三个 # of color components in input image */
    jcs.input_components = nComponent;
    //设置结构体的颜色空间为rgb
    jcs.in_color_space = JCS_RGB;
//	if (nComponent == 1)
//		jcs.in_color_space = JCS_GRAYSCALE;
//	else
//		jcs.in_color_space = JCS_RGB;

    //全部设置默认参数/* Default parameter setup for compression */
    jpeg_set_defaults(&jcs);
    //是否采用哈弗曼表数据计算 品质相差5-10倍
    jcs.optimize_coding = optimize;
    //设置质量 quality是个0～100之间的整数，表示压缩比率
    jpeg_set_quality(&jcs, quality, true);
    //开始压缩，(是否写入全部像素)
    jpeg_start_compress(&jcs, TRUE);

    JSAMPROW row_pointer[1];
    int row_stride;
    //一行的rgb数量
    row_stride = jcs.image_width * nComponent;
    //一行一行遍历
    while (jcs.next_scanline < jcs.image_height) {
        //得到一行的首地址
        row_pointer[0] = &data[jcs.next_scanline * row_stride];

        //此方法会将jcs.next_scanline加1
        jpeg_write_scanlines(&jcs, row_pointer, 1);//row_pointer就是一行的首地址，1：写入的行数
    }
    jpeg_finish_compress(&jcs);//结束
    jpeg_destroy_compress(&jcs);//销毁 回收内存
    fclose(f);//关闭文件



    jmethodID pID = menv->GetMethodID(nativeCallBackClass, "finish",
                                      "(Ljava/lang/String;)V");
    if (callBack!=NULL) {

        menv->CallVoidMethod(callBack,pID,menv->NewStringUTF(outfilename));
    }
    //关闭资源
    freeResource();
//    LOGE("完成");
    return 1;
}

extern "C"
//防止c++的命名规范导致jni找不到方法
JNIEXPORT void JNICALL
Java_org_jpegutil_fjpeg_NativeCompress_nativeLibJpegCompress(JNIEnv *env, jobject instance,
                                                             jstring outpath_, jobject bitmap,
                                                             jint CompressionRatio,
                                                             jboolean isUseHoffman,
                                                             jobject nativeCallBack) {
    //文件输出地址
    const char *outpath = env->GetStringUTFChars(outpath_, 0);
    //用于保存bitmap的二进制数据
    BYTE *pixelscolor;

    //保存全局引用
    callBack = env->NewGlobalRef(nativeCallBack);
    menv = env;

    // 得到bitmap一些信息
    AndroidBitmapInfo info;
    memset(&info, 0, sizeof(info));
    AndroidBitmap_getInfo(env, bitmap, &info);
    int w = info.width;
    int h = info.height;

    jclass nativeCallBackClass = env->FindClass("org/jpegutil/fjpeg/minterface/NativeCallBack");

    //校验图片合法性
    if (w <= 0 || h <= 0) {
//        LOGE("发生错误:传入的图片宽度或者高度不小于等于0 【width:%d】【height:%d】", w, h);
        jmethodID errorMthodId = env->GetMethodID(nativeCallBackClass, "error",
                                                  "(ILjava/lang/String;)V");
        char description[100];
        sprintf(description, "图高度或者宽为0，【高：%d】 【 宽：%d】", h, w);
        if (callBack!=NULL) {
            env->CallVoidMethod(nativeCallBack, errorMthodId, BITMAP_HEIGHT_WIDTH_ERROR,
                                env->NewStringUTF(description));
        }

        //关闭资源
        freeResource();
        return;
    }

    //校验图片格式
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
//        LOGE("发生错误:传入的图片不合法");
        jmethodID errorMthodId = env->GetMethodID(nativeCallBackClass, "error",
                                                  "(ILjava/lang/String;)V");

        if (callBack!=NULL) {
            env->CallVoidMethod(nativeCallBack, errorMthodId, BITMAP_FOMAT_ERROR,
                                env->NewStringUTF("图片格式错误"));
        }

        //关闭资源
        freeResource();
        return;
    }

//    LOGE("开始读取数据");
    //锁定bitmap 获取二进制数据
    AndroidBitmap_lockPixels(env, bitmap, (void **) &pixelscolor);
//
    //2.解析每一个像素点里面的rgb值(去掉alpha值)，保存到一维数组data里面
    BYTE *data;
    BYTE a, r, g, b;
    data = (BYTE *) malloc(w * h * 3);//每一个像素都有四个信息ARGB 并且ARGB8888每一个像素点为64位
    BYTE *tmpdata;
    tmpdata = data;//临时保存data的首地址

    int i, j;
    for (i = 0; i < h; ++i) {
        for (j = 0; j < w; ++j) {
            //读取指针指向数据（这里指向bitmap二进制数据的指针）
            int color = *((int *) pixelscolor);

            //得到透明度
            //*a = ((color & 0xFF000000) >> 24);
            r = ((color & 0x00FF0000) >> 16);
            g = ((color & 0x0000FF00) >> 8);
            b = ((color & 0x000000FF));

            //保存data中
            *data = b;
            *(data + 1) = g;
            *(data + 2) = r;
            // **(data + 3) = *a;

            //地址偏移4个字节
            data += 3;
            pixelscolor += 4;
        }
    }
    //解锁bitmap
    AndroidBitmap_unlockPixels(env, bitmap);
//    LOGE("读取数据完毕");
    //拷贝输出文件地址
    char *outPathBackup = (char *) malloc(sizeof(char) * (strlen(outpath) + 1));
    strcpy(outPathBackup, outpath);
//    LOGE("开始压缩");
    //压缩
    generateJPEG(tmpdata, w, h, CompressionRatio, outPathBackup, isUseHoffman);

    //释放资源
    env->ReleaseStringUTFChars(outpath_, outpath);
}

