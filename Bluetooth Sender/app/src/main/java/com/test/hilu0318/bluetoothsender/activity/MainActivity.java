package com.test.hilu0318.bluetoothsender.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.test.hilu0318.bluetoothsender.R;
import com.test.hilu0318.bluetoothsender.dialog.WaitingDialog;
import com.test.hilu0318.bluetoothsender.service.BluetoothService;
import com.test.hilu0318.bluetoothsender.service.NewBluetoothService;
import com.test.hilu0318.bluetoothsender.tag.Tag;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int BLUETOOTH_RESULT_ACTIVITY = 100;

    private BluetoothAdapter bluetoothAdapter;
    private WaitingDialog waitingDialog;
    //private NewBluetoothService newBluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waitingDialog = new WaitingDialog(this, R.string.connecting);
        //newBluetoothService = NewBluetoothService.getInstance();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void mainOnClick(View v){
        switch (v.getId()){
            case R.id.main_btn:{
                if(bluetoothAdapter == null){
                    Toast.makeText(this, R.string.bluetooth_not_support, Toast.LENGTH_SHORT).show();
                }else{
                    if(bluetoothAdapter.isEnabled()){
                        startActivity(new Intent(MainActivity.this, SendAndReceiveActivity.class));
                    }else{
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        this.startActivityForResult(intent, BLUETOOTH_RESULT_ACTIVITY);
                    }
                }
                break;
            }
        }
    }

    @Override   //블루투스 온, 오프 응답에 따른 동작.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case BLUETOOTH_RESULT_ACTIVITY:{
                switch(resultCode){
                    case RESULT_OK:{
                        startActivity(new Intent(MainActivity.this, SendAndReceiveActivity.class));
                        break;
                    }
                    case RESULT_CANCELED:{
                        Toast.makeText(this, R.string.bluetooth_connect_cancel, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                break;
            }
        }
    }


}
