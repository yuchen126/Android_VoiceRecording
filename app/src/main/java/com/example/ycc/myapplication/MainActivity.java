package com.example.ycc.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by yuchen on 2017/10/29.
 */

public class MainActivity extends AppCompatActivity {
    private Button btn_voiceRecord;
    private Button btn_viewAudio;
    private Button btn_picNaming;
    private Button btn_setid;
    private Button btn_setLevel;
    private String sbjId = "";
    final String[] levels = {"", "Bisyllabic", "Monosyllabic", "Multisyllabic"};
    final String[] freq = {"", "HighFreq", "LowFreq"};
    private String levelChosen = "";
    private String freqChosen = "";
    Spinner spinner1;
    Spinner spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_voiceRecord = (Button)findViewById(R.id.btn_voiceRecord);
        btn_viewAudio = (Button)findViewById(R.id.btn_andioFiles);
        btn_picNaming = (Button)findViewById(R.id.btn_picnaming);
        btn_setid = (Button)findViewById(R.id.btn_setid);
        btn_setLevel = (Button)findViewById(R.id.btn_setlevel);
        final ArrayAdapter<String> adp1 = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, levels);
        final ArrayAdapter<String> adp2 = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, freq);

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
                Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/RecordedAudio/");
                intent.setDataAndType(selectedUri,"audio/*");
                startActivity(intent);
            }
        });

        btn_picNaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sbjId.equals("")) {
                    Toast.makeText(MainActivity.this, "Please specifiy subject id!", Toast.LENGTH_SHORT).show();
                } else if (levelChosen.equals("") || freqChosen.equals("") ) {
                    Toast.makeText(MainActivity.this, "Please choose a level for test!", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(MainActivity.this, PictureNaming.class));
                }
            }
        });

        btn_setid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Set up subject Id");
                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sbjId = input.getText().toString();
                        SharedPreferences sp = getSharedPreferences("SID", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("subjectID", sbjId);
                        editor.apply();
                        Toast.makeText(MainActivity.this, "subject id saved", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
        btn_setLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("Setup test level");
                View holder = View.inflate(MainActivity.this, R.layout.spinner, null);
                builder.setView(holder);
                spinner1 = (Spinner)holder.findViewById(R.id.spinner1);
                spinner1.setAdapter(adp1);
                spinner2 = (Spinner)holder.findViewById(R.id.spinner2);
                spinner2.setAdapter(adp2);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        levelChosen = spinner1.getSelectedItem().toString();
                        freqChosen = spinner2.getSelectedItem().toString();
                        SharedPreferences sp = getSharedPreferences("LEVEL", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("type", levelChosen);
                        editor.putString("freq", freqChosen);
                        editor.apply();
                        Toast.makeText(MainActivity.this, "test level saved", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
    }
}
