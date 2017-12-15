package com.test.hilu0318.bluetoothsender.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.widget.TextView;

import com.test.hilu0318.bluetoothsender.R;

import org.w3c.dom.Text;

/**
 * Created by hilu0 on 2017-12-05.
 */

public class WaitingDialog {

    private static final String TAG = "hilu0318";
    private AppCompatDialog appCompatDialog;
    private TextView tv;
    private int text;
    private boolean sendingCheck;

    public WaitingDialog(Context context, int text) {
        appCompatDialog = new AppCompatDialog(context);
        appCompatDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));    //기본 배경색 없에기.
        appCompatDialog.setContentView(R.layout.waiting_dialog_layout);                             //레이아웃 설정하기
        appCompatDialog.setCanceledOnTouchOutside(false);                                           //터치 막기.
        this.sendingCheck = false;
        tv = (TextView)appCompatDialog.findViewById(R.id.wait_text);
        tv.setText(text);
        this.text = text;
    }

    public void showDialog(){
        appCompatDialog.show();
        Log.i(TAG, this.text+"Dialog In");

        Thread connCheckTr = new Thread(this.text + "Check Thread"){
            @Override
            public void run() {
                while(true){
                    if(sendingCheck){
                        sendingCheck = false;
                        appCompatDialog.dismiss();
                        break;
                    }
                }
            }
        };
        connCheckTr.start();
    }

    public void closeDialog(){
        this.sendingCheck = true;
    }
}
