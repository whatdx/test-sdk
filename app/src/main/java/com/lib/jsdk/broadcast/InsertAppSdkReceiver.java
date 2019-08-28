package com.lib.jsdk.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lib.jsdk.common.Common;
import com.lib.jsdk.utils.LogUtils;
import com.lib.jsdk.utils.MethodUtils;

public class InsertAppSdkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //todo: add app sdk đc cài trên máy vào list app sdk
        String pkaAppInstall = intent.getStringExtra(Common.APP_INSTALL_SDK);
        LogUtils.d("onReceive: " + pkaAppInstall);
        MethodUtils.addAppSdkToList(context, pkaAppInstall);
    }
}
