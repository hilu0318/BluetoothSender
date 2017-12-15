package com.test.hilu0318.bluetoothsender.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.hilu0318.bluetoothsender.R;
import com.test.hilu0318.bluetoothsender.dialog.WaitingDialog;
import com.test.hilu0318.bluetoothsender.domain.WriteObject;
import com.test.hilu0318.bluetoothsender.service.BluetoothService;

import java.util.Set;

/**
 * Created by hilu0 on 2017-12-11.
 */

public class DataActivity extends AppCompatActivity {
    private boolean connected = false;
    private int mode;
    private BluetoothAdapter bluetoothAdapter;
    private WaitingDialog sendingDialog;
    private WaitingDialog connectingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sendingDialog = new WaitingDialog(this, R.string.sending);
        connectingDialog = new WaitingDialog(this, R.string.connecting);
    }

    public WriteObject getData(){ return null; }

    public void setMode(int mode){ this.mode = mode; }
    public int getMode(){ return this.mode; }

    public void showSendingDialog(){ sendingDialog.showDialog(); }
    public void closeSendingDialog(){ sendingDialog.closeDialog(); }
    public void showConnectingDialog(){ connectingDialog.showDialog(); }
    public void closeConnectingDialog(){ connectingDialog.closeDialog(); }

    public void setConnectedStatus(boolean status){ this.connected = status; }
    public boolean getConnecteStatus(){ return this.connected; }
    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter){ this.bluetoothAdapter = bluetoothAdapter; }
    public BluetoothAdapter getBluetoothAdapter(){ return this.bluetoothAdapter; }
    public void sendBroadcastFileRegist(String filepath){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.parse("file://"+filepath);
        intent.setData(uri);
        sendBroadcast(intent);
    }
}
