package com.test.hilu0318.bluetoothsender.domain;

import java.util.UUID;

/**
 * Created by hilu0 on 2017-12-06.
 */

public class Flag {
    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public static final UUID seruuid = UUID.fromString("00000003-0000-1000-8000-00805F9B34FB");
    public static final int DESTROY_ACTIVITY = -1;
    public static final int CONNECT_SUCCESS = 1;
    public static final int FILE_RECEIVE_MODE = 2;
    public static final int FILE_SEND_MODE = 3;
    public static final int FILE_WRITE_FINISH = 10;

}
