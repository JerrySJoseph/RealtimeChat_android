package com.jstechnologies.realtimechat20;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jstechnologies.realtimechat20.interfaces.ActiveUserListener;
import com.jstechnologies.realtimechat20.interfaces.ConnectionListener;
import com.jstechnologies.realtimechat20.interfaces.MessageListener;
import com.jstechnologies.realtimechat20.interfaces.MessageResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class App extends Application {

    static ArrayList<JSONObject> activeUsers;
    static String name,room;
    static Context context;
    static Socket mSocket;
    static ConnectionListener connectionListener;
    static ActiveUserListener activeUserListener;
    static MessageListener messageListener;
    /*  String ENDPOINT="https://realtimechat-server1.herokuapp.com/";*/
    String ENDPOINT="http://192.168.1.14:5000/";
    @Override
    public void onCreate() {
        activeUsers= new ArrayList<>();
        context=getApplicationContext();
        try{
            mSocket= IO.socket(ENDPOINT);
        }catch (Exception e)
        {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        super.onCreate();
    }

    public static void setMessageListener(MessageListener messageListener) {
        App.messageListener = messageListener;
    }

    public static void setConnectionListener(ConnectionListener connectionListener) {
        App.connectionListener = connectionListener;
    }

    public static void setActiveUserListener(ActiveUserListener activeUserListener) {
        App.activeUserListener = activeUserListener;
    }

    public static void initConnection(String name, String room)
    {
        setName(name);
        setRoom(room);
        //Callback Events
        mSocket.on(Socket.EVENT_CONNECT,connectListener);
        mSocket.on(Socket.EVENT_RECONNECT,connectListener);
        mSocket.on(Socket.EVENT_ERROR,errorListener);
        mSocket.on(Socket.EVENT_DISCONNECT,disconnectListener);
        mSocket.on("error-join",joinerrorListener);
        mSocket.on("users",usersListener);
        mSocket.on("join-success",joinSuccessListener);
        mSocket.on("message",newMessageListener);
        mSocket.connect();
    }
    public static void Disconnect()
    {
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT,connectListener);
        mSocket.off(Socket.EVENT_RECONNECT,connectListener);
        mSocket.off(Socket.EVENT_ERROR,errorListener);
        mSocket.off(Socket.EVENT_DISCONNECT,disconnectListener);
        mSocket.off("error-join",joinerrorListener);
        mSocket.off("users",usersListener);
        mSocket.off("join-success",joinSuccessListener);
        mSocket.off("message",newMessageListener);
        mSocket.off("sendMessage-response");
        if(connectionListener!=null)
            connectionListener.onDisconnect();
    }
    public static void SendMessage(final String message, final MessageResponseListener listener)
    {
        if(mSocket.connected())
        {
            if(listener!=null)
                listener.onMessageSending();
            mSocket.once("sendMessage-response", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    try {
                        JSONObject data=(JSONObject)args[0];
                        if(data.has("success")&& data.getBoolean("success"))
                        {
                            JSONObject mes= new JSONObject();
                            mes.put("message",message);
                            mes.put("from",name);
                            mes.put("room",room);
                            mes.put("socket",mSocket.id());
                            if(listener!=null)
                                listener.onMessageSentSuccess(mes);
                        }
                        else
                        {
                            if(listener!=null)
                                listener.onMessageSentFailed(data.getString("status"));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            mSocket.emit("sendMessage",mSocket.id(),getRoom(),message);
        }
    }
    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        App.name = name;
    }

    public static String getRoom() {
        return room;
    }

    public static void setRoom(String room) {
        App.room = room;
    }

    public static ArrayList<JSONObject> getActiveUsers() {
        return activeUsers;
    }

    public static void setActiveUsers(ArrayList<JSONObject> activeUsers) {
        activeUsers = activeUsers;
    }
    public static void clearActiveUsers()
    {
        activeUsers.clear();
    }
    public static void addActiveuser(JSONObject user)
    {
        activeUsers.add(user);
    }
    public static int getActiveUserCount()
    {
        return activeUsers.size();
    }
    public static void parseActiveUsers(JSONObject data)
    {
        try{
            JSONArray online=data.getJSONArray("online");
            clearActiveUsers();
            for(int i=0;i<online.length();i++)
            {
                addActiveuser(online.getJSONObject(i));
            }
        }catch (Exception e)
        {
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    static Emitter.Listener connectListener= new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            try{
                JSONObject object= new JSONObject();
                object.put("name",name);
                object.put("rid",room);
                mSocket.emit("join",object);
            }catch (final Exception e)
            {
                if(connectionListener!=null)
                    connectionListener.onConnectFailed(e.getMessage());

            }

        }
    };
    static Emitter.Listener newMessageListener= new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(messageListener!=null)
                messageListener.onNewMessage((JSONObject)args[0]);
        }
    };
    static Emitter.Listener errorListener= new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(connectionListener!=null)
           connectionListener.onConnectFailed("Connection to server could not be established. Try again after some time");

        }
    };
    static Emitter.Listener joinSuccessListener= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(connectionListener!=null)
            connectionListener.onConnectSuccess();
        }
    };
    static Emitter.Listener usersListener= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            parseActiveUsers((JSONObject)args[0]);
            if(activeUserListener!=null)
            activeUserListener.onActiveUsersChange(getActiveUsers());
        }
    };
    static Emitter.Listener disconnectListener= new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(connectionListener!=null)
           connectionListener.onDisconnect();
        }
    };
    static Emitter.Listener joinerrorListener= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String msg;
            try{
                JSONObject data = (JSONObject) args[0];
                msg=data.getString("error");
            }
            catch (Exception e)
            {
                msg=e.getMessage();
            }
            if(connectionListener!=null)
            connectionListener.onConnectFailed(msg);

        }
    };

}
