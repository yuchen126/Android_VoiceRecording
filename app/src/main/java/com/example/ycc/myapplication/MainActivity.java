package com.example.ycc.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by yuchen on 2017/10/29.
 */

public class MainActivity extends AppCompatActivity {
    private Button btn_voiceRecord;
    private Button btn_viewAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_voiceRecord = (Button)findViewById(R.id.btn_voiceRecord);
        btn_viewAudio = (Button)findViewById(R.id.btn_andioFiles);
        btn_voiceRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VoiceRecordingActivity.class));
            }
        });
        btn_viewAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri selectedUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/Audio");
                intent.setDataAndType(selectedUri,"audio/*");
                startActivity(intent);
            }
        });
    }
}
