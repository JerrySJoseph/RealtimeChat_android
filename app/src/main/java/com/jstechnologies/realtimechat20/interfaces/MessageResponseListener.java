package com.jstechnologies.realtimechat20.interfaces;

import org.json.JSONObject;

public interface MessageResponseListener {
    void onMessageSentSuccess(JSONObject message);
    void onMessageSentFailed(String reason);
    void onMessageSending();
}
