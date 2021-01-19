package com.bluebox.bluebox.devicelist;

import com.bluebox.bluebox.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Device implements Serializable {

    public String name;
    public String macAddress;
    public String emoji;
    public boolean isConnected;

    public Device(String name, String macAddress, boolean isConnected) {
        this.name = name;
        this.macAddress = macAddress;
        this.isConnected = isConnected;
        this.emoji = "";
    }

    public Device(String name, String macAddress, boolean isConnected, String emoji) {
        this(name, macAddress, isConnected);

        if(emoji != null) {
            this.emoji = emoji;
        }
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", this.name);
            jsonObject.put("macAddress", this.macAddress);
            jsonObject.put("isConnected", this.isConnected);
            jsonObject.put("emoji", this.emoji);
        } catch (JSONException e) {
            Logger.e("Error casting to JSON: "+e);
        }
        return jsonObject;
    }
}