package com.test.hilu0318.bluetoothsender.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.test.hilu0318.bluetoothsender.R;

/**
 * Created by hilu0 on 2017-12-01.
 */

public class ChoiceMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_menu_layout);
    }

    public void menuOnClick(View v){
        switch (v.getId()){
            case R.id.img_selet_btn:{
                startActivity(new Intent(this, ImageSelectActivity.class));
                break;
            }
            case R.id.music_selet_btn:{
                startActivity(new Intent(this, MusicSelectActivity.class));
            }
        }
    }
}
