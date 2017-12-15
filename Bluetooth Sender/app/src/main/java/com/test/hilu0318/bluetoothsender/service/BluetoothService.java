package com.test.hilu0318.bluetoothsender.service;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import com.test.hilu0318.bluetoothsender.domain.Flag;
import com.test.hilu0318.bluetoothsender.domain.WriteObject;
import com.test.hilu0318.bluetoothsender.tag.Tag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by hilu0 on 2017-12-01.
 */

public class BluetoothService {

    private BluetoothAdapter bluetoothAdapter;

    public BluetoothService(){ this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); }

    public BluetoothAdapter getBluetoothAdapter(){ return this.bluetoothAdapter; }

}
