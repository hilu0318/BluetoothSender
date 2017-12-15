package com.test.hilu0318.bluetoothsender.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.test.hilu0318.bluetoothsender.R;

/**
 * Created by hilu0 on 2017-12-03.
 */

public class SendAndReceiveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_receive_layout);
    }

    public void srOnClick(View v){
        switch(v.getId()){
            case R.id.send_btn:{
                startActivity(new Intent(this, ChoiceMenuActivity.class));
                break;
            }
            case R.id.receive_btn:{
                startActivity(new Intent(this, FileReceiveActivity.class));
                //Toast.makeText(this, R.string.notyet, Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
