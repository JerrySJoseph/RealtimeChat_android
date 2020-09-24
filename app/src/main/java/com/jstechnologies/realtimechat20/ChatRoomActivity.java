package com.jstechnologies.realtimechat20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jstechnologies.realtimechat20.interfaces.ActiveUserListener;
import com.jstechnologies.realtimechat20.interfaces.MessageListener;
import com.jstechnologies.realtimechat20.interfaces.MessageResponseListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    ArrayList<JSONObject> models=new ArrayList<>();
    MessageAdapter adapter;
    RecyclerView recyclerView;
    EditText msg;
    FloatingActionButton send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(App.getRoom()+" ("+App.getActiveUserCount()+")");
        recyclerView=findViewById(R.id.recycler);
        msg=findViewById(R.id.msg);
        send=findViewById(R.id.send);
        adapter=new MessageAdapter(models,App.getName());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        App.setActiveUserListener(activeUserListener);
        App.setMessageListener(messageListener);
     //   Toast.makeText(this,"Active Users: "+App.getActiveUserCount(),Toast.LENGTH_LONG).show();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.SendMessage(msg.getText().toString().trim(), messageResponseListener);
                msg.setText("");
            }
        });

    }
    ActiveUserListener activeUserListener= new ActiveUserListener() {
        @Override
        public void onActiveUsersChange(final ArrayList<JSONObject> userList) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),userList.toString(),Toast.LENGTH_LONG).show();
                    getSupportActionBar().setTitle(App.getRoom()+" ("+App.getActiveUserCount()+")");
                }
            });

        }
    };
    MessageListener messageListener= new MessageListener() {
        @Override
        public void onNewMessage(final JSONObject message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    models.add(message);
                    adapter.notifyItemInserted(models.size()-1);
                    recyclerView.smoothScrollToPosition(models.size()-1);
               //     Toast.makeText(getApplicationContext(),message.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    MessageResponseListener messageResponseListener=new MessageResponseListener() {
        @Override
        public void onMessageSentSuccess(final JSONObject message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                 /*   models.add(message);
                    adapter.notifyItemInserted(models.size()-1);
                    recyclerView.smoothScrollToPosition(models.size()-1);*/

                }
            });
        }

        @Override
        public void onMessageSentFailed(String reason) {
            Toast.makeText(getApplicationContext(),reason,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onMessageSending() {
        //    Toast.makeText(getApplicationContext(),"Sending",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.Disconnect();
    }
}
