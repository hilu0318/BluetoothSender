package com.test.hilu0318.bluetoothsender.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.test.hilu0318.bluetoothsender.R;
import com.test.hilu0318.bluetoothsender.domain.Flag;
import com.test.hilu0318.bluetoothsender.service.ReceiveBluetoothService;
import com.test.hilu0318.bluetoothsender.tag.Tag;

import java.util.Set;

/**
 * Created by hilu0 on 2017-12-06.
 */

public class FileReceiveActivity extends DataActivity {

    private TextView filenameTv;
    private TextView filesizeTv;
    private TextView mimetypeTv;
    private ProgressBar progressBar;

    private boolean writing = false;

    private ReceiveBluetoothService rService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMode(Flag.FILE_RECEIVE_MODE);
        setContentView(R.layout.file_receive_layout);

        filenameTv = (TextView)findViewById(R.id.receive_filename_text);
        filesizeTv = (TextView)findViewById(R.id.receive_filesize_text);
        mimetypeTv = (TextView)findViewById(R.id.receive_mimetype_text);
        progressBar = (ProgressBar)findViewById(R.id.file_receive_pgb);
        rService = new ReceiveBluetoothService(this, handler, progressBar);
        showParingList();

    }

    @Override
    protected void onDestroy() {
        if(rService != null)
            rService.closeAll();
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if(writing){
                Toast.makeText(this, R.string.warning_sending, Toast.LENGTH_LONG).show();
            }else{
                startActivity(new Intent(this, SendAndReceiveActivity.class));
            }
        }else if(event.getKeyCode() == KeyEvent.KEYCODE_HOME){
            return super.dispatchKeyEvent(event);
        }
        return true;
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:{
                    Bundle data = msg.getData();
                    filenameTv.setText(data.getString("filename"));
                    filesizeTv.setText(data.getInt("filesize", 0)+"");
                    mimetypeTv.setText(data.getString("mimetype"));
                    progressBar.setMax(data.getInt("filesize", 0));
                    break;
                }
                case 1:{
                    Toast.makeText(FileReceiveActivity.this, R.string.receive_finish, Toast.LENGTH_SHORT).show();
                    filenameTv.setText("");
                    filesizeTv.setText("");
                    mimetypeTv.setText("");
                    progressBar.setProgress(0);
                    break;
                }
            }
        }
    };

    public void setWritingStatus(boolean status){ this.writing = status; }
    public boolean getWritingStatus(){ return this.writing; }

    private void showParingList(){

        Set<BluetoothDevice> devices = getBluetoothAdapter().getBondedDevices();
        final BluetoothDevice[] deviceList = devices.toArray(new BluetoothDevice[0]);
        String[] item = new String[deviceList.length];

        for(int i = 0; i< deviceList.length; i++){
            item[i] = deviceList[i].getName();
        }

        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        adBuilder.setTitle("Test AlertDialog");
        adBuilder.setCancelable(true);
        adBuilder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                showConnectingDialog();
                rService.connect(deviceList[i]);
                rService.writeMessage(Flag.FILE_SEND_MODE);
            }
        });
        adBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    startActivity(new Intent(FileReceiveActivity.this, SendAndReceiveActivity.class));
                }
                return true;
            }
        });
        adBuilder.create().show();
    }
}
