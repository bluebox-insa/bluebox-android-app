package com.bluebox.bluebox;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Device implements Serializable {

    public String name;
    public String macAddress;
    public boolean isConnected;

    public Device(String name, String macAddress, boolean isConnected) {
        this.name = name;
        this.macAddress = macAddress;
        this.isConnected = isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }


    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", this.name);
            jsonObject.put("macAddress", this.macAddress);
            jsonObject.put("isConnected", this.isConnected);
        } catch (JSONException e) {
            Log.e("ERROR", "Error casting to JSON\n"+e);
        }
        return jsonObject;
    }
}