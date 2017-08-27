# Fjpeg
封装libjpeg对图片进行压缩，和二次采样等代码封装

1. 我们知道在Android 6.0以下 系统对图片的编码采用'定长编码'之后才采用‘哈夫曼编码’。
所以本框架封装libjpeg 进行对图片进行编码 ，方便网络传输等
2. 我们知道Android加载Bitmap图片时候 如果图片分辨率过大 我们需要对图片进行分辨率裁剪 在进行显示
所以本框架封装了二次采样代码

#### 注意:</br>
libjpeg压缩不能改变图片分辨率，只能改变存储在硬盘中的大小。也就是说他的目的在于网络传输。
所以如果你想加载大图请使用本框架封装的二次采样代码即可

### 如何使用？
在你moudle的build.gradle中的dependencies添加以下代码</br>

</br>compile 'com.fmy:fjpeg:1.0.0@aar</br>

</br>比如：</br>
```
dependencies {
    ...   
    compile 'com.fmy:fjpeg:1.0.0@aar'
    ...
}
```

### 如何压缩图片（只改变在硬盘的存储大小）

```java
        /**
         *  第一个参数:压缩后 文件输出路径，
         *  第二个参数:需要压缩Bitmap对象
         *  第三个参数:压缩质量 1-100  1是最小的
         *  第四个参数:是否启用哈夫曼编码
         *  第五个参数:回调 包含 开始前、错误、结束
         *  第六个参数:是否使用子线程
         */
        ImageUtils.compressQC(new File(Environment.getExternalStorageDirectory(), "测试剥离框架.jpg").getAbsolutePath(), bitmap, 1, true, new NativeCallBack() {
          //开始前回调
            @Override
            public void startCompress() {
                Log.d(TAG, "startCompress() called");
            }

            //错误回调
            @Override
            public void error(int errorNum, String description) {
                Log.d(TAG, "error() called with: errorNum = [" + errorNum + "], description = [" + description + "]");
            }

            //完成结束回调  如果发生错误讲不会回调次方法
            @Override
            public void finish(String filePath) {
                Log.d(TAG, "finish() called with: filePath = [" + filePath + "]");
            }
        }, true);
```
### 如何改变图片分辨率（让其Bitmap对象可以加载到内存中）
```
        //从mipmap中读取。此方法ImageUtils.compressPxSampleSize存在多个重载版本
        //如：从文件 、从资源、从io流、字节数组等
        Bitmap bitmap = ImageUtils.compressPxSampleSize(getResources(), R.mipmap.timg, 1000, 1000);
```
#### 关于重载版本
![这里写图片描述](http://img.blog.csdn.net/20170827134209379?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcWZhbm1pbmd5aXE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
