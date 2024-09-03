package com.example.speechrecognizer_intent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Intent intent=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, 0);

        TextView msg=findViewById(R.id.msg);
        findViewById(R.id.speak).setOnClickListener(view -> {
            if(intent!=null){
                startActivityForResult(intent,0);
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "RECORD_AUDIO 퍼미션이 허용되었습니다.", Toast.LENGTH_SHORT).show();

            intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREA);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "명령어를 말하세요!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0&&resultCode==RESULT_OK&&data!=null){
            TextView msg=findViewById(R.id.msg);
            msg.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            sendMsg(msg.getText().toString());
        }
    }

    //UDP
    public void sendMsg(String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket ds=new DatagramSocket();
                    InetAddress ia=InetAddress.getByName("192.168.0.100");
                    DatagramPacket dp=new DatagramPacket(msg.getBytes(),msg.getBytes().length,ia,9999);
                    ds.send(dp);
                    ds.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}