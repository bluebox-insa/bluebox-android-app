package com.bluebox.bluebox.screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bluebox.bluebox.devicelist.Device;
import com.bluebox.bluebox.utils.Logger;
import com.bluebox.bluebox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Screen1 extends Fragment {

    protected SharedPreferences pref;
    protected SharedPreferences.Editor editor;
    private String hostname;
    private ArrayList<Device> deviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("Screen1() created");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.screen_1, container, false);

        // Initiate the SharedPreferences objects
        this.initSharedPreferences();
        return v;
    }

    protected void initSharedPreferences() {
        // retrieve SharedPreferences
        this.pref = getActivity().getSharedPreferences("all", MODE_PRIVATE);
        this.editor = this.pref.edit();

        // set default values
        Logger.d("Set default values to shared preferences");
        setHostname(getResources().getString(R.string.hostnameDefaultVal));
        setDeviceList(new ArrayList<>());
    }

    protected String getHostname() {
        this.hostname = this.pref.getString("hostname", null);
        Logger.d("got hostname = "+this.hostname);
        return this.hostname;
    }

    protected void setHostname(String v) {
        this.editor.putString("hostname", v);
        this.editor.apply();

        this.hostname = v;
        Logger.d("set hostname = "+this.hostname);
    }

    protected ArrayList<Device> getDeviceList() {
        this.deviceList = new ArrayList<>();

        try {
            // retrieve JSON-formatted string
            String deviceListStr = this.pref.getString("devices", null);
            Logger.d("retrieved String: "+deviceListStr);

            // convert to ArrayList
            JSONArray deviceListJson = new JSONArray(deviceListStr);
            Device d;
            for (int i = 0; i < deviceListJson.length(); i++) {
                JSONObject deviceJson = new JSONObject(deviceListJson.getString(i));

                if (!deviceJson.getString("name").equals("<unknown>")) {
                    d = new Device(
                            deviceJson.getString("name"),
                            deviceJson.getString("macAddress"),
                            deviceJson.getBoolean("isConnected"),
                            deviceJson.getString("emoji")
                    );

                    this.deviceList.add(d);
                }
            }
        } catch (JSONException e) {
            Logger.e("error parsing JSON "+e);
        }

        Logger.d("got deviceList = "+this.deviceList);
        return this.deviceList;
    }

    protected void setDeviceList(ArrayList<Device> list) {
        JSONArray deviceListJson = new JSONArray();
        for (Device d: list) {
            deviceListJson.put(d.toJsonObject().toString());
        }
        editor.putString("devices", deviceListJson.toString());
        editor.apply();

        this.deviceList = new ArrayList<>();
        this.deviceList.addAll(list);
        Logger.d("set deviceList to "+this.deviceList);
    }
}
