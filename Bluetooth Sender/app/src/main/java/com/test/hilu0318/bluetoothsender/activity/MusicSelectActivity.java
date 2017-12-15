package com.test.hilu0318.bluetoothsender.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.test.hilu0318.bluetoothsender.R;
import com.test.hilu0318.bluetoothsender.adapter.MusicSelectAdapter;
import com.test.hilu0318.bluetoothsender.dialog.WaitingDialog;
import com.test.hilu0318.bluetoothsender.domain.Flag;
import com.test.hilu0318.bluetoothsender.domain.ImageInfo;
import com.test.hilu0318.bluetoothsender.domain.MusicInfo;
import com.test.hilu0318.bluetoothsender.domain.WriteObject;
import com.test.hilu0318.bluetoothsender.service.BluetoothService;
import com.test.hilu0318.bluetoothsender.service.NewBluetoothService;
import com.test.hilu0318.bluetoothsender.tag.Tag;
import com.test.hilu0318.bluetoothsender.utils.DataUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by hilu0 on 2017-12-04.
 */

public class MusicSelectActivity extends DataActivity{

    private ListView listView;
    private MusicSelectAdapter musicSelectAdapter;
    private List<MusicInfo> data;

    private NewBluetoothService newBluetoothService;

    private int selectDataNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_select_layout);

        newBluetoothService = NewBluetoothService.getInstance();
        newBluetoothService.setDataActivity(this);
        setBluetoothAdapter(newBluetoothService.getBluetoothAdapter());

        data = getMusicDB();

        musicSelectAdapter = new MusicSelectAdapter(this, R.layout.music_select_list_item, R.id.ad_ms_filename, data);

        listView = (ListView)findViewById(R.id.music_list);
        listView.setAdapter(musicSelectAdapter);
        listView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void onDestroy() {
        if(newBluetoothService != null)
            newBluetoothService.closeAll();
        super.onDestroy();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
            selectDataNum = (int)id;
            showParingList();
        }
    };

    private List<MusicInfo> getMusicDB(){
        List<MusicInfo> msList = new ArrayList<>();

        String[] datalist =
                {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.MIME_TYPE};

        CursorLoader cl =
                new CursorLoader(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, datalist,
                        null, null, null);

        Cursor cs = cl.loadInBackground();
        cs.moveToFirst();

        for(int j = 0; j<cs.getCount(); j++){
            MusicInfo msInfo = new MusicInfo();

            int[] index = new int[datalist.length];
            for(int i = 0; i<index.length; i++){
                index[i] = cs.getColumnIndexOrThrow(datalist[i]);
            }
            if((cs.getString(index[3])).equals("audio/mpeg")){
                msInfo.setFilepath(cs.getString(index[0]));
                msInfo.setFilename(cs.getString(index[1]));
                msInfo.setFilesize(cs.getInt(index[2]));
                msInfo.setMimetype(cs.getString(index[3]));

                msList.add(msInfo);
            }

            if(!cs.moveToNext())
                break;
        }
        return msList;
    }

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
                showSendingDialog();
                newBluetoothService.connect(deviceList[i]);
                newBluetoothService.writeOnlyData(makeData(data.get(selectDataNum)));
            }
        });
        adBuilder.create().show();
    }

    public WriteObject makeData(MusicInfo info) {
        WriteObject obj = new WriteObject();
        try {
            obj.setFilename(info.getFilename());
            obj.setFilesize(info.getFilesize());
            obj.setMimetype(info.getMimetype());
            obj.setStart(Flag.FILE_RECEIVE_MODE);
            obj.setData(DataUtil.makeByteData(info.getFilepath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
