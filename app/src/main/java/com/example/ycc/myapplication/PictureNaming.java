package com.example.ycc.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.net.Uri;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PictureNaming extends AppCompatActivity {
    private Button btn_next;
    private Button btn_home;
    private ImageView img;
    private Button btn_hint;
    private String outputfile = null;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    Date createdTime = new Date();
    public static final int RequestPermissionCode = 1;
    ArrayList<String> listImages;
    private Context mContext;
    int index = 0;
    ArrayList<Integer> myImageList = new ArrayList<>();
    String sbjid;
    String type;
    String freq;
    String[] images;
    String line;
    InputStream in;
    BufferedReader reader;
    ArrayList<String> values;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_naming);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_hint = (Button) findViewById(R.id.btn_hint);
        img = (ImageView) findViewById(R.id.imgView1);

        sbjid = getSubjectId();
        type = getTestLevel();
        freq = getTestFreq();
        Log.wtf("type",String.valueOf(type));
        Log.wtf("freq",String.valueOf(freq));
        try {

            images = this.getAssets().list(type + "/" + freq);
            listImages = new ArrayList<String>(Arrays.asList(images));
            InputStream inputstream = this.getAssets().open(type + "/" + freq + "/"+ listImages.get(index));
            Drawable drawable = Drawable.createFromStream(inputstream, null);
            img.setImageDrawable(drawable);
//            img.setImageResource(myImageList.get(index));

            BufferedReader bReader = new BufferedReader(new InputStreamReader(getAssets().open("text/" + type + "/" + freq + ".txt")));
            values = new ArrayList<String>();
            String line = bReader.readLine();
            while (line != null) {
                values.add(line);
                line = bReader.readLine();
            }
            bReader.close();
        } catch (IOException e) {

        }

        if (checkPermission()) {
            File f = new File(Environment.getExternalStorageDirectory() + "/BTAP_PictureMaming/");
            if (!f.isDirectory()) {
                File audioDirct = new File(Environment.getExternalStorageDirectory() + "/BTAP_PictureMaming/");
                audioDirct.mkdirs();
            }
            outputfile = Environment.getExternalStorageDirectory() + "/BTAP_PictureMaming/sbjId_" + sbjid + "_Recorded.mp3";

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
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/BTAP_PictureMaming/")));

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
                if (index ==listImages.size() - 1) {
                   btn_next.setEnabled(false);
                    Toast.makeText(PictureNaming.this, "Finish, please return back to home page", Toast.LENGTH_LONG).show();
                } else {
                    index++;
                    try {
                        InputStream inputstream = getAssets().open(type + "/" + freq + "/"+ listImages.get(index));
                        Drawable drawable = Drawable.createFromStream(inputstream, null);
                        img.setImageDrawable(drawable);
                    } catch (IOException e) {

                    }
                }
            }
        });
        btn_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
               final String speakNow = values.get(index).split(",")[1];
                tts = new TextToSpeech(PictureNaming.this, new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        tts.setLanguage(Locale.US);
                        Log.wtf("tts", speakNow);
                        tts.speak(speakNow, TextToSpeech.QUEUE_FLUSH, null);
                    }
                });
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

    public String getSubjectId() {
        SharedPreferences sp = getSharedPreferences("SID", Context.MODE_PRIVATE);
        return sp.getString("subjectID", "");
    }

    public String getTestLevel() {
        SharedPreferences sp = getSharedPreferences("LEVEL", Context.MODE_PRIVATE);
        return sp.getString("type", "");
    }

    public String getTestFreq() {
        SharedPreferences sp = getSharedPreferences("LEVEL", Context.MODE_PRIVATE);
        return sp.getString("freq", "");
    }
}