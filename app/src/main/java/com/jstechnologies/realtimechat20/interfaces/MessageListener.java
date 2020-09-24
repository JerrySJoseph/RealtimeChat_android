package com.jstechnologies.realtimechat20.interfaces;

import org.json.JSONObject;

public interface MessageListener {
    void onNewMessage(JSONObject message);
}
