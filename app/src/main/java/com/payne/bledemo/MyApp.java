package com.payne.bledemo;

import android.app.Application;

import com.payne.okux.BleCommunication.RxBleHelper;

/**
 * ------------------------------------------------
 * Copyright © 2014-2021 CLife. All Rights Reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------
 *
 * @author huyongming
 * @version v1.0.0
 * @date 2021/6/17-9:21
 * @annotation ....
 */
public class MyApp extends Application {
    @Override
    public void onTerminate() {
        super.onTerminate();
        //退出
        RxBleHelper.getInstance(getApplicationContext()).release();
    }
}
