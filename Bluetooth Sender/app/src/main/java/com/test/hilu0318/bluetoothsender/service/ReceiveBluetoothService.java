package com.test.hilu0318.bluetoothsender.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.hilu0318.bluetoothsender.activity.FileReceiveActivity;
import com.test.hilu0318.bluetoothsender.activity.ImageSelectActivity;
import com.test.hilu0318.bluetoothsender.activity.MusicSelectActivity;
import com.test.hilu0318.bluetoothsender.domain.Flag;
import com.test.hilu0318.bluetoothsender.domain.WriteObject;
import com.test.hilu0318.bluetoothsender.tag.Tag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hilu0 on 2017-12-06.
 */

public class ReceiveBluetoothService extends BluetoothService {

    private static final String ACCEPT_NAME = "BluetoothServer";

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private Message msg;
    private Handler handler;

    private ProgressBar progressBar;

    private boolean disconnected = false;

    private Map<String, Object> dmap = new HashMap<>();

    int progressVal = 0;

    private FileReceiveActivity activity;

    public ReceiveBluetoothService(FileReceiveActivity fileReceiveActivity, Handler handler, ProgressBar progressBar) {
        super();
        activity = fileReceiveActivity;
        this.handler = handler;
        this.progressBar = progressBar;
    }
    /*************************************/

    /******** 파일셋팅 **********/
    public Map<String, Object> getText(){return this.dmap; }
    public int getProgressVal(){ return this.progressVal; }

    /****************************/

    public void connect(BluetoothDevice device){
        if(connectThread != null)
            connectThread.close();
        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    private void connected(BluetoothSocket socket){
        if(connectedThread != null)
            connectedThread.close();
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    private class ConnectThread extends Thread{

        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device){
            this.setName("Connect Thread Run");
            this.device = device;
            try {
                this.socket = this.device.createRfcommSocketToServiceRecord(Flag.seruuid);
            } catch (IOException e) {
                Log.e(Tag.TAG, "SERVICE : ConnectThread - ConnectThread() : Getting Socket is Fail");
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            getBluetoothAdapter().cancelDiscovery();
            try {

                socket.connect();
            } catch (IOException e) {
                Log.e(Tag.TAG, "Connection Fail");
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            connected(socket);
            activity.closeConnectingDialog();
            Log.e(Tag.TAG, "SERVICE : ConnectThread - run() : Out");
        }

        public void close(){
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread{

        private BluetoothSocket socket;
        private DataInputStream dis;
        private DataOutputStream dos;

        public ConnectedThread(BluetoothSocket socket){
            this.setName("Connected Thread Run");
            this.socket = socket;

            try {
                dis = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
                dos = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream(), 1024*10));
                Log.e(Tag.TAG, "ConnectedThread - Create");
            } catch (IOException e) {
                Log.e(Tag.TAG, "ConnectedThread - ConnectedThread() : Getting InputStream, OutputStream is Fail");
                e.printStackTrace();
            }
            activity.setConnectedStatus(true);
        }

        @Override
        public void run() {
            try{
                while(true){

                    int checkByte = dis.read();
                    if(checkByte == -1){
                        Log.e(Tag.TAG, "Server Out");
                        break;
                    }

                    int code = dis.readInt();
                    if(code == -1) {
                        Log.e(Tag.TAG, "Server Out");
                        break;
                    }
                    switch(code){
                        case Flag.FILE_RECEIVE_MODE:{
                            activity.setWritingStatus(true);
                            Log.i(Tag.TAG, "Receive Data Start!!");

                            String _filename = dis.readUTF();
                            String _mimetype = dis.readUTF();
                            int _filesize = dis.readInt();
                            msg = handler.obtainMessage(0);
                            Bundle bundle = new Bundle();
                            bundle.putString("filename", _filename);
                            bundle.putString("mimetype", _mimetype);
                            bundle.putInt("filesize", _filesize);
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                            //가변바이트를 생성할 수 있도록 바이트 배열 아웃스트림 생성.
                            ByteArrayOutputStream dos = new ByteArrayOutputStream(_filesize);
                            progressBar.setMax(_filesize);
                            //실제 파일 바이트배열로 받기.
                            int data = 0;
                            int copySize = 0;
                            while(true){
                                byte[] buffer = new byte[1024*10];
                                data = dis.read(buffer);
                                dos.write(buffer, 0, data);
                                copySize += data;
                                progressBar.setProgress(copySize);
                                if(copySize == _filesize) {
                                    Log.i(Tag.TAG, "CopySize is Full - Out Thread");
                                    break;
                                }
                            }

                            byte[] filedata = dos.toByteArray();
                            registData(filedata, _filename);
                            writeMessage(Flag.FILE_WRITE_FINISH);

                            activity.setWritingStatus(false);
                            msg = handler.obtainMessage(1);
                            handler.sendMessage(msg);
                            break;
                        }
                        case Flag.FILE_WRITE_FINISH:{
                            disconnected = true;
                            activity.setWritingStatus(false);
                            break;
                        }
                    }
                    if(disconnected){
                        disconnected = true;
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public void write(WriteObject obj){
            try {

                dos.write("1".getBytes());  //정상 연결 상태 전송.
                dos.writeInt(obj.getStart());
                if(obj.getStart() == 1){
                    dos.writeUTF(obj.getFilename());
                    dos.writeInt(obj.getFilesize());
                    dos.write(obj.getData());
                }
                dos.flush();

            } catch (IOException e) {
                Log.e(Tag.TAG, "Write Exception in ConnectedThread");
                e.printStackTrace();
            }
        }

        public void close(){
            try {
                this.dis.close();
                this.dos.close();
                this.socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeAll(){
        if(connectThread != null)
            connectThread.close();
        if(connectedThread != null)
            connectedThread.close();
    }

    public void destroyMsg(){
        connectedThread.write(makeNullWriteObject());
    }
    /*************** 서버에 메시지 전달 메서드 *****************/

    public void writeMessage(int msg){
        WriteThread wt = new WriteThread(makeMessageObject(msg));
        wt.start();
    }

    private WriteObject makeNullWriteObject(){
        WriteObject obj = new WriteObject();
        obj.setStart(Flag.DESTROY_ACTIVITY);
        return obj;
    }

    private WriteObject makeMessageObject(int msg) {
        WriteObject obj = new WriteObject();
        obj.setStart(msg);
        return obj;
    }
    /*******************************************************/

    /*************** 파일쓰기 메서드 *****************/
    private class WriteThread extends Thread{
        private WriteObject obj;
        public WriteThread(WriteObject obj){
            this.setName("Data Write Thread");
            this.obj = obj;
        }
        @Override
        public void run() {
            while(true){
                if(activity.getConnecteStatus()){
                    Log.i(Tag.TAG, "SERVICE : Data Sending!! - IN");
                    connectedThread.write(obj);
                    break;
                }
            }
            Log.i(Tag.TAG, "SERVICE : Data Sending!! - OUT");
        }
    }

    public void writeOnlyData(WriteObject obj){
        activity.setWritingStatus(true);
        WriteThread wt = new WriteThread(obj);
        wt.start();
    }
    /******************************************/

    private class RegistDataThread extends Thread{
        private byte[] data;
        private String filename;

        public RegistDataThread(byte[] data, String filename){
            this.setName("File Regist Thread");
            this.data = data;
            this.filename = filename;
        }

        @Override
        public void run() {
            try {

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;
                DataOutputStream fdos = new DataOutputStream(new FileOutputStream(new File(filePath)));
                fdos.write(data);
                fdos.close();
                activity.sendBroadcastFileRegist(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void registData(byte[] data, String filename) throws IOException {
        RegistDataThread rdt = new RegistDataThread(data, filename);
        rdt.start();
    }
}
