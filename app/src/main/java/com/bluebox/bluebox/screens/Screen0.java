package com.bluebox.bluebox.screens;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Screen0 extends Fragment {

    protected SharedPreferences pref;
    protected SharedPreferences.Editor editor;
    protected String hostname;
    protected ArrayList<Device> deviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("Screen0() created");

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.screen_0, container, false);

        // Initiate the SharedPreferences objects
        this.initSharedPreferences(true);

        return v;
    }

    protected void initSharedPreferences(boolean resetPreferences) {
        // retrieve SharedPreferences
        this.pref = getActivity().getSharedPreferences("all", MODE_PRIVATE);
        this.editor = this.pref.edit();

        this.hostname = getResources().getString(R.string.hostnameDefaultVal);
        this.deviceList = new ArrayList<>();

        // set default values
        if (resetPreferences) {
            Logger.d("Resetting shared preferences..");
            writeHostname();
            writeDeviceList();
        } else {
            readHostname();
            readDeviceList();
        }
    }

    /*              HOSTNAME            */
    protected void readHostname() {
        this.hostname = this.pref.getString("hostname", null);
        Logger.d("got hostname = "+this.hostname);
    }

    protected void writeHostname() {
        this.editor.putString("hostname", this.hostname);
        this.editor.apply();
        Logger.d("set hostname to "+this.hostname);
    }


    /*              DEVICE LIST            */
    protected void readDeviceList() {
        this.deviceList.clear();

        try {
            // retrieve JSON-formatted string
            String deviceListStr = this.pref.getString("devices", null);
            Logger.d("got deviceList(JSON) = "+deviceListStr);

            // convert to ArrayList
            JSONArray deviceListJson = new JSONArray(deviceListStr);
            Device d;
            for (int i = 0; i < deviceListJson.length(); i++) {
                JSONObject deviceJson = new JSONObject(deviceListJson.getString(i));

                if (!deviceJson.getString("name").equals("<unknown>")) {
                    d = new Device(deviceJson);
                    this.deviceList.add(d);
                }
            }
        } catch (JSONException e) {
            Logger.e("error parsing JSON "+e);
        }
        Logger.d("    deviceList(Object) = "+this.deviceList);
    }

    protected void writeDeviceList() {
        JSONArray deviceListJson = new JSONArray();
        for (Device d: this.deviceList) {
            deviceListJson.put(d.toJsonObject().toString());
        }
        Logger.d("set deviceList(JSON) to "+deviceListJson);
        editor.putString("devices", deviceListJson.toString());
        editor.apply();
        Logger.d("    deviceList(Object) to " + this.deviceList);
    }
}
