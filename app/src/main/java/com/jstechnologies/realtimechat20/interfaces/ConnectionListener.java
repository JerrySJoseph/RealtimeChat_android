package com.jstechnologies.realtimechat20.interfaces;

public interface ConnectionListener {
    void onConnectSuccess();
    void onConnectFailed(String reason);
    void onDisconnect();
    void onReconnecting();
    void onReconnectSuccess();
}
