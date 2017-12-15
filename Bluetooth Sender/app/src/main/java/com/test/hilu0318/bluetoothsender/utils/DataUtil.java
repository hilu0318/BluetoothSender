package com.test.hilu0318.bluetoothsender.utils;

import android.util.Log;

import com.test.hilu0318.bluetoothsender.tag.Tag;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by hilu0 on 2017-12-12.
 */

public class DataUtil {
    public static byte[] makeByteData(String filepath) throws IOException {
        File file = new File(filepath);
        byte[] data = new byte[(int) file.length()];
        Log.i(Tag.TAG, "SERVICE : File Exist - "+file.exists()+" , Byte Length - "+file.length());
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        int result = dis.read(data);
        Log.i(Tag.TAG, "SERVICE : Read size - "+result);
        dis.close();
        return data;
    }
}
