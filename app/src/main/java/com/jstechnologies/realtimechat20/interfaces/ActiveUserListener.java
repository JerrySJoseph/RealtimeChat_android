package com.jstechnologies.realtimechat20.interfaces;

import org.json.JSONObject;

import java.util.ArrayList;

public interface ActiveUserListener {
    void onActiveUsersChange(ArrayList<JSONObject> userList);
}
