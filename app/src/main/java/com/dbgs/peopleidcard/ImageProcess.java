package com.dbgs.peopleidcard;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/21.
 */

public class ImageProcess {
    static {
        System.loadLibrary("native-lib");

    }

    public native static  void findIdNumber(Bitmap src, Bitmap out, Bitmap tpl);





    public native static  Bitmap getIdNumber(Bitmap src, Bitmap tpl, Bitmap.Config config);
}
