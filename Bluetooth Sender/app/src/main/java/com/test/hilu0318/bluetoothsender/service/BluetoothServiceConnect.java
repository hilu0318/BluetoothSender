package com.test.hilu0318.bluetoothsender.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;


/**
 * Created by hilu0 on 2017-12-05.
 */

public class BluetoothServiceConnect {

    private static final String TAG = "hilu0318";
    private Messenger serviceMsg;

    public BluetoothServiceConnect(Context context){

        Intent intent = new Intent();
        intent.setClassName("com.test.hilu0318.bluetoothsender",
                "com.test.hilu0318.bluetoothsender.service.BluetoothService");
        context.bindService(intent, connection, context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.i(TAG, "OnServiceConnected()");
            serviceMsg = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "OnServiceDisonnected()");
            serviceMsg = null;
        }
    };
}
