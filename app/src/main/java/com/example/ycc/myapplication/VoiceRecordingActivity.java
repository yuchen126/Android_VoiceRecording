package com.example.ycc.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class VoiceRecordingActivity extends AppCompatActivity {
    private Button btn_record;
    private Button btn_stop;
    private Button btn_play;
    private Button btn_stopplay;
    private String outputfile = null;
    private MediaRecorder mediaRecorder ;
    private MediaPlayer mediaPlayer ;
    Date createdTime = new Date();
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recording);

        btn_record = (Button) findViewById(R.id.btn_record);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_stopplay = (Button)findViewById(R.id.btn_stopPlay);

        btn_stop.setEnabled(false);
        btn_play.setEnabled(false);
        btn_stopplay.setEnabled(false);

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()) {
                    File f = new File(Environment.getExternalStorageDirectory() + "/AudioRecorded/");
                    if(!f.isDirectory()) {
                        File audioDirct = new File(Environment.getExternalStorageDirectory() + "/AudioRecorded/");
                        audioDirct .mkdirs();
                    }
                    outputfile = Environment.getExternalStorageDirectory() + "/AudioRecorded/" + createdTime + "AudioRecording.mp3";

                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    btn_record.setEnabled(false);
                    btn_stop.setEnabled(true);
                    Toast.makeText(VoiceRecordingActivity.this, "Start Recording", Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/AudioRecorded/")));
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btn_stop.setEnabled(false);
                btn_play.setEnabled(true);
                btn_record.setEnabled(true);
                btn_stopplay.setEnabled(false);

                Toast.makeText(VoiceRecordingActivity.this, "Recording Successfully", Toast.LENGTH_LONG).show();
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                btn_stop.setEnabled(false);
                btn_record.setEnabled(false);
                btn_stopplay.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(outputfile);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(VoiceRecordingActivity.this, "Playing Recording", Toast.LENGTH_LONG).show();
            }
        });

        btn_stopplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_stop.setEnabled(false);
                btn_record.setEnabled(true);
                btn_stopplay.setEnabled(false);
                btn_play.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputfile);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(VoiceRecordingActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(VoiceRecordingActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VoiceRecordingActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
}