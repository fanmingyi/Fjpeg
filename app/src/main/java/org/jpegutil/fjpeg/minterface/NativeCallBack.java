package org.jpegutil.fjpeg.minterface;

/**
 * Created by FMY on 2017/8/26.
 */

public interface NativeCallBack {

    void startCompress();

    void error(int errorNum, String description);

    void finish(String filePath);

}
