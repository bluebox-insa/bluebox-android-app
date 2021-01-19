package com.bluebox.bluebox.screens;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.bluebox.bluebox.utils.Logger;
import com.bluebox.bluebox.R;
import com.bluebox.bluebox.devicelist.Device;
import com.bluebox.bluebox.devicelist.DeviceAdapter;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.bluebox.bluebox.MainActivity.requests;

public class Screen2 extends Screen1 {

    protected DeviceAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("Screen2() created");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.screen_2, container);

        // Initiate the SharedPreferences object
        initSharedPreferences(false);

        // Initiate the SCAN and RESET buttons
        initButtons(view, "/reset_output");

        // Initiate the device list
        initDeviceList(view,"/connect_output", getResources().getString(R.string.emoji_speaker));

        // Auto-scan
        scanRequest();

        return view;
    }


    /**
     * @param view the view
     * @param connectReq API route for connecting a device, can be /connect_input or /connect_output
     * @param emoji emoji displayed next to a device name when it is connected
     */
    protected void initDeviceList(View view, String connectReq, String emoji) {
        ListView devicesComponent = view.findViewById(R.id.deviceList);
        this.adapter = new DeviceAdapter(getContext(), this.deviceList);
        devicesComponent.setAdapter(this.adapter);

        devicesComponent.setOnItemClickListener(
                (AdapterView<?> parent, View itemView, int position, long id) -> {
                    // 1. retrieve informations from the clicked Device
                    String deviceName = this.deviceList.get(position).name;
                    String deviceMacAddress = this.deviceList.get(position).macAddress;

                    // 2. make a request GET /connect/<mac_addr>
                    requests.makeRequest(
                            this.hostname + connectReq + deviceMacAddress,
                            "Connexion à " + deviceName,

                            // 3. on success, show that the device is connected
                            (String response) -> {
                                this.deviceList.get(position).setConnected(true);
                                this.deviceList.get(position).setEmoji(emoji);
                                writeDeviceListAndNotifyAdapter();
                            },
                            null
                    );
                }
        );
    }

    /**
     * @param view the view
     * @param resetReq API route for resetting, can be /reset_input or /reset_output
     */
    protected void initButtons(View view, String resetReq) {
        // click on SCAN button
        Button scanButton = view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v1 -> {
            scanRequest();
        });

        // click on RESET button
        Button resetButton = view.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v2 -> {
            readHostname();
            requests.makeRequest(
                    this.hostname + resetReq,
                    "Réinitialisation de BlueBox",
                    null,
                    null
            );
        });

        // long click on RESET button
        resetButton.setOnLongClickListener(v3 -> {
            readHostname();
            requests.makeRequest(
                    this.hostname + resetReq +"?hard",
                    "Hard-reset de la BlueBox",
                    null,
                    null
            );
            return true;
        });
    }

    /**
     * Perform a scan request and update the device list
     */
    protected void scanRequest() {
        // 1. make a request GET /scan
        requests.makeRequest(
                this.hostname + "/scan",
                "Recherche en cours",

                // 2. on success, parse the result and update the device list
                (String response) -> {
                    this.deviceList.clear();
                    Device d;
                    try {
                        JSONArray devicesFoundJson = new JSONArray(response);
                        for (int i = 0; i < devicesFoundJson.length(); i++) {
                            JSONObject deviceJson = new JSONObject(devicesFoundJson.getString(i));
                            if (!deviceJson.getString("name").equals("<unknown>")) {
                                d = new Device(
                                        deviceJson.getString("name"),
                                        deviceJson.getString("mac_address"),
                                        false,
                                        null
                                );
                                this.deviceList.add(d);
                            }
                        }
                    } catch (JSONException e) {
                        Logger.e("error parsing JSON "+e);
                    }

                    writeDeviceListAndNotifyAdapter();

                    // 3. if there is no result, print a toast message
                    if (this.deviceList.isEmpty()) {
                        makeText(getContext(), R.string.no_bluetooth_device_found, LENGTH_LONG);
                    }
                },
                // 4. on error, print a toast message
                (Object error) -> {
                    makeText(getContext(), R.string.no_bluetooth_device_found, LENGTH_LONG);
                }
        );
    }

    /**
     * Write the deviceList to SharedPreferences, and notify the adapter that the data changed
      */
    protected void writeDeviceListAndNotifyAdapter() {
        super.writeDeviceList();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        } else {
            Logger.w("WARNING: adapter is NULL");
        }
    }

    /**
     * When user switch from one screen to another, the former screen is put on pause.
     * When user comes back, onResume is called. If shared preferences have changed in between,
     * we should call readDeviceList() again.
     */
    @Override
    public void onResume() {
        super.onResume();

        Logger.d("Screen resumed: refreshing shared preferences");
        readHostname();
        readDeviceList();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }
}