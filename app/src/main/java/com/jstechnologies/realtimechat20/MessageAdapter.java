package com.jstechnologies.realtimechat20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter {


    static int REPLY=1;
    static int MESSAGE=2;
    static int ADMIN=3;
    ArrayList<JSONObject>models;
    String nickname;

    public MessageAdapter(ArrayList<JSONObject> models, String nickname) {
        this.models = models;
        this.nickname = nickname;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==REPLY)
            return new Replyholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply,parent,false));
        else if(viewType==ADMIN)
            return new AdminMessageholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_message,parent,false));
        return new Messageholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mesage,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if(models.get(position).getString("from").equals(nickname))
                ((Replyholder)holder).Bind(models.get(position));
            else if(models.get(position).getString("from").equals("admin"))
                ((AdminMessageholder)holder).Bind(models.get(position));
            else
                ((Messageholder)holder).Bind(models.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(int position) {
        JSONObject message=models.get(position);
        try {
            if(message.getString("from").equals(nickname))
                return REPLY;
            else if(message.getString("from").equals("admin"))
                return ADMIN;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MESSAGE;
    }

    public class Messageholder extends RecyclerView.ViewHolder {

        TextView msg,timestamp,name;
        public Messageholder(@NonNull View itemView) {
            super(itemView);
            msg=itemView.findViewById(R.id.msg);
            timestamp=itemView.findViewById(R.id.timestamp);
            name=itemView.findViewById(R.id.name);
        }
        public void Bind(JSONObject model)
        {
            try {
                msg.setText(model.getString("message"));
                String time=new SimpleDateFormat("hh:mm a").format(new Date());
                timestamp.setText(time);
                name.setText(model.getString("from"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public class AdminMessageholder extends RecyclerView.ViewHolder {

        TextView msg;
        public AdminMessageholder(@NonNull View itemView) {
            super(itemView);
            msg=itemView.findViewById(R.id.msg);
        }
        public void Bind(JSONObject model)
        {
            try {
                msg.setText(model.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class Replyholder extends RecyclerView.ViewHolder {

        TextView msg,timestamp;
        public Replyholder(@NonNull View itemView) {
            super(itemView);
            msg=itemView.findViewById(R.id.msg);
            timestamp=itemView.findViewById(R.id.timestamp);

        }
        public void Bind(JSONObject model)
        {
            try {
                msg.setText(model.getString("message"));
                String time=new SimpleDateFormat("hh:mm a").format(new Date());
                timestamp.setText(time);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
