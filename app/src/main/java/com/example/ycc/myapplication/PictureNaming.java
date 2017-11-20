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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PictureNaming extends AppCompatActivity {
    private Button btn_next;
    private Button btn_home;
    private ImageView img;

    private String outputfile = null;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    Date createdTime = new Date();
    public static final int RequestPermissionCode = 1;
    int index = 0;
    ArrayList<Integer> myImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_naming);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_home = (Button) findViewById(R.id.btn_home);
        img = (ImageView) findViewById(R.id.imgView1);

        myImageList.add(R.drawable.pic1);
        myImageList.add(R.drawable.pic2);
        myImageList.add(R.drawable.pic3);

// later...
        img.setImageResource(myImageList.get(index));

        if (checkPermission()) {
            File f = new File(Environment.getExternalStorageDirectory() + "/AudioRecorded/");
            if (!f.isDirectory()) {
                File audioDirct = new File(Environment.getExternalStorageDirectory() + "/AudioRecorded/");
                audioDirct.mkdirs();
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
            Toast.makeText(PictureNaming.this, "Start Recording", Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/AudioRecorded/")));

        btn_home.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View view){
                    mediaRecorder.stop();
                    Toast.makeText(PictureNaming.this, "Recording Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(PictureNaming.this, MainActivity.class));
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                if (index == myImageList.size() - 1) {
                   btn_next.setEnabled(false);
                    Toast.makeText(PictureNaming.this, "No more pictures, please return back to home page", Toast.LENGTH_LONG).show();
                } else {
                    index++;
                }
                img.setImageResource(myImageList.get(index));
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
        ActivityCompat.requestPermissions(PictureNaming.this, new
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
                        Toast.makeText(PictureNaming.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PictureNaming.this,"Permission Denied",Toast.LENGTH_LONG).show();
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