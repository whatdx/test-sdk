package com.lib.jsdk.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lib.jsdk.common.Common;
import com.lib.jsdk.utils.MethodUtils;
import com.lib.jsdk.utils.TinyDB;

public class InsertAppSdkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //todo: add app sdk đc cài trên máy vào list app sdk
        String action = intent.getAction();
        assert action != null;
        String pkaAppInstall = intent.getStringExtra(Common.PACKAGE_NAME);
        long timeFirstOpen = intent.getLongExtra(Common.TIME_FIRST_OPEN, 0);
        MethodUtils.addAppSdkToList(context, pkaAppInstall, timeFirstOpen);
        Log.d("datdb", "onReceive: " + pkaAppInstall);
        if (action.equals(Common.ACTION_INSERT_NEW_APP_SDK)) {
            MethodUtils.sendBroadcastSdk(context, Common.ACTION_INSERT_OLD_APP_SDK, pkaAppInstall, new TinyDB(context).getLong(Common.TIME_FIRST_OPEN, 0));
        }
    }
}
