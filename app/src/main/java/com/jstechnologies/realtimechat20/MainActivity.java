package com.jstechnologies.realtimechat20;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jstechnologies.realtimechat20.interfaces.ConnectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText name,room;
    Button btn;
    TextView socket_t;
    String nametext,roomtext;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Grabbing all the controls
        name=findViewById(R.id.name);
        room=findViewById(R.id.room);
        btn=findViewById(R.id.btn);
        socket_t=findViewById(R.id.socket);
        App.setConnectionListener(connectionListener);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog=new ProgressDialog(MainActivity.this);
                nametext=name.getText().toString().trim().toLowerCase();
                roomtext=room.getText().toString().trim().toLowerCase();
                App.initConnection(nametext,roomtext);
            }
        });
    }
    ConnectionListener connectionListener= new ConnectionListener() {
        @Override
        public void onConnectSuccess() {
            startActivity(new Intent(MainActivity.this,ChatRoomActivity.class));
        }

        @Override
        public void onConnectFailed(String reason) {
            Toast.makeText(getApplicationContext(),reason,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onDisconnect() {
            Toast.makeText(getApplicationContext(),"disconnected",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onReconnecting() {
            Toast.makeText(getApplicationContext(),"reconnecting",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onReconnectSuccess() {
            Toast.makeText(getApplicationContext(),"reconnect success",Toast.LENGTH_LONG).show();
        }
    };

}