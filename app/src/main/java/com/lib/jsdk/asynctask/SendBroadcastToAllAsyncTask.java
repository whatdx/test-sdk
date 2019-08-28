package com.lib.jsdk.asynctask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.lib.jsdk.common.Common;
import com.lib.jsdk.utils.LogUtils;
import com.lib.jsdk.utils.MethodUtils;

import java.util.ArrayList;

public class SendBroadcastToAllAsyncTask extends AsyncTask<String, String, String> {

    private Context context;
    private ArrayList<String> allApps;

    public SendBroadcastToAllAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        allApps = MethodUtils.getAllApp(context);
        for (int i = 0; i < allApps.size(); i++) {
            String pka = allApps.get(i);
            LogUtils.d("doInBackground: " + pka);
            if (!pka.contains("google")) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(pka, "com.lib.jsdk.broadcast.InsertAppSdkReceiver"));
                    intent.setAction(Common.ACTION_INSERT_APP_SDK);
                    intent.putExtra(Common.APP_INSTALL_SDK, context.getPackageName());
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.sendBroadcast(intent);
                    LogUtils.d("okela: " + pka);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.d("error: " + pka);
                }
            }
        }


        return null;
    }
}
