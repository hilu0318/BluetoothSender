package com.test.hilu0318.bluetoothsender.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.test.hilu0318.bluetoothsender.activity.DataActivity;
import com.test.hilu0318.bluetoothsender.domain.Flag;
import com.test.hilu0318.bluetoothsender.domain.WriteObject;
import com.test.hilu0318.bluetoothsender.tag.Tag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hilu0 on 2017-12-11.
 */

public class NewBluetoothService extends BluetoothService {
    public static final int RESPONSE_FILE_SEND_FINISH = 2300;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private DataActivity dataActivity;

    public void setDataActivity(DataActivity dataActivity){ this.dataActivity = dataActivity; }

    /********** 싱글톤. **********/
    private NewBluetoothService(){
        super();
    }
    private static final NewBluetoothService newBluetoothService = new NewBluetoothService();
    public static NewBluetoothService getInstance(){ return newBluetoothService; }
    /*************************************/

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
            Log.i(Tag.TAG, "SERVICE : ConnectThread - run() : In");
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
            Log.i(Tag.TAG, "SERVICE : ConnectThread - run() : Out");
        }

        public void close(){
            try {
                socket.close();
            } catch (IOException e) {
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
                Log.e(Tag.TAG, "SERVICE : ConnectedThread - Create!");
            } catch (IOException e) {
                Log.e(Tag.TAG, "SERVICE :ConnectedThread - ConnectedThread() : Getting InputStream, OutputStream is Fail");
                e.printStackTrace();
            }
            dataActivity.setConnectedStatus(true);
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
                    Log.e(Tag.TAG, "SERVICE : Server OK");

                    int code = dis.readInt();
                    if(code == -1) {
                        Log.e(Tag.TAG, "Server Out");
                        break;
                    }
                    Log.e(Tag.TAG, "SERVICE : Mode OK");
                    switch(code){
                        case Flag.FILE_WRITE_FINISH:{
                            dataActivity.closeSendingDialog();
                            Log.i(Tag.TAG, "SERVICE : Get Write Finish MSG!!");
                            break;
                        }
                        case Flag.CONNECT_SUCCESS:{

                            break;
                        }
                    }
                }
            }catch (IOException e){
                dataActivity.setConnectedStatus(false);
                e.printStackTrace();
            }
            dataActivity.setConnectedStatus(false);
        }

        public void write(WriteObject obj){
            try {
                dos.write("1".getBytes());  //정상 연결 상태 전송.
                dos.writeInt(obj.getStart());
                if(obj.getStart() == Flag.FILE_RECEIVE_MODE){
                    dos.writeUTF(obj.getFilename());
                    dos.writeUTF(obj.getMimetype());

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
            } catch (IOException e) {
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
                if(dataActivity.getConnecteStatus()){
                    Log.i(Tag.TAG, "SERVICE : Data Sending!! - IN");
                    connectedThread.write(obj);
                    break;
                }
            }
            Log.i(Tag.TAG, "SERVICE : Data Sending!! - OUT");
        }
    }

    public void writeOnlyData(WriteObject obj){
        WriteThread wt = new WriteThread(obj);
        wt.start();
    }
    /******************************************/

    /*************** 디바이스에 파일 저장메서드 *****************/
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
                dataActivity.sendBroadcastFileRegist(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registData(byte[] data, String filename) throws IOException {
        RegistDataThread rdt = new RegistDataThread(data, filename);
        rdt.start();
    }
    /******************************************/


}
