package com.bluebox.bluebox.screens;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.bluebox.bluebox.utils.Logger;
import com.bluebox.bluebox.R;
import com.bluebox.bluebox.devicelist.Device;
import com.bluebox.bluebox.devicelist.DeviceAdapter;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.bluebox.bluebox.MainActivity.requests;

public class Screen2 extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private List<Device> deviceList;
    private ListView devicesComponent;
    private DeviceAdapter adapter;
    private Button scanButton;
    private Button resetButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("Screen2() created");
        View v = inflater.inflate(R.layout.screen_2, container);
        init(v,"/connect_output", "/reset_output", getResources().getString(R.string.emoji_speaker), true);
        return v;
    }

    public void notifyDataSetChanged(){
        Logger.d("dataset changed");
        adapter.notifyDataSetChanged();
    }

    public void init(View v, String connectReq, String resetReq, String emoji, boolean autoscanOnOpen) {
        // retrieve SharedPreferences
        pref = getActivity().getSharedPreferences("all", MODE_PRIVATE);
        editor = pref.edit();

        /*-------------------------------------------------------------------------
         *                                  devicesList
         *
         * - the SCAN button retrieves an array of surrounding Bluetooth devices
         *   in JSON-format and adds them to the list
         * - click on a device to connect to it
         * - long-press on a device to copy its MAC address to the macAddr EditText
         *--------------------------------------------------------------------------*/

        // instantiate deviceList and retrieve devices around
        Logger.d("new ArrayList()");
        deviceList = new ArrayList<>();
        try {
            String sharedDeviceList = pref.getString("devices", null);
            Logger.d("retrieving sharedDeviceList: "+sharedDeviceList);
            JSONArray jsonArray = new JSONArray(sharedDeviceList);
            Device newDevice;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                if (!jsonObject.getString("name").equals("<unknown>")) {
                    newDevice = new Device(jsonObject.getString("name"), jsonObject.getString("macAddress"), jsonObject.getBoolean("isConnected"), jsonObject.getString("emoji"));
                    deviceList.add(newDevice);
                }
            }
        } catch (JSONException e) {
            Logger.e("error parsing JSON "+e);
        }

        devicesComponent = v.findViewById(R.id.deviceList);
        adapter = new DeviceAdapter(getContext(), deviceList);
        devicesComponent.setAdapter(adapter);

        devicesComponent.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {
                    // 1. retrieve informations from JSON string
                    String deviceName = deviceList.get(position).name;
                    String deviceMacAddress = deviceList.get(position).macAddress;

                    // 2. do GET /connect/<mac_addr>
                    String sharedHostname = pref.getString("hostname", null);
                    requests.makeRequest("","",
                            (String response) -> {
                                    deviceList.get(position).setConnected(true);
                                    deviceList.get(position).setEmoji(emoji);
                                    adapter.notifyDataSetChanged();
                                    view.setEnabled(false);
                                    view.setOnClickListener(null);

                                    // edit sharedPreferences too
                                    JSONArray deviceListJson = new JSONArray();
                                    for (Device d: deviceList) {
                                        deviceListJson.put(d.toJsonObject().toString());
                                    }
                                    editor.putString("devices", deviceListJson.toString());
                                    Logger.d("sharedPreferences <= "+deviceListJson.toString());
                                    editor.apply();
                                },

                            error -> {
                                    deviceList.get(position).setConnected(true);
                                    deviceList.get(position).setEmoji(emoji);
                                    adapter.notifyDataSetChanged();
                                    view.setEnabled(false);
                                    view.setOnClickListener(null);

                                    // edit sharedPreferences too
                                    JSONArray deviceListJson = new JSONArray();
                                    for (Device d: deviceList) {
                                        deviceListJson.put(d.toJsonObject().toString());
                                    }
                                    editor.putString("devices", deviceListJson.toString());
                                    Logger.d("sharedPreferences <= "+deviceListJson.toString());
                                    editor.apply();
                            });
                });

        // BUTTONS
        scanButton = v.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v1 -> {
            String sharedHostname = pref.getString("hostname", null);
            requests.makeRequestAndParseJsonArray(
                    sharedHostname + "/scan",
                    "Recherche en cours",
                    (JSONArray jsonArray) -> {
                        // remove the former elements from the list
                        deviceList.clear();

                        // add the new elements
                        Device newDevice;
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                                if (!jsonObject.getString("name").equals("<unknown>")) {
                                    newDevice = new Device(jsonObject.getString("name"), jsonObject.getString("mac_address"), false);
                                    deviceList.add(newDevice);
                                }
                            }
                        } catch (JSONException e) {
                            Logger.e("error parsing JSON "+e);
                        }

                        adapter.notifyDataSetChanged();
                        if (deviceList.isEmpty()) {
                            makeText(getContext(), R.string.no_bluetooth_device_found, LENGTH_LONG);
                        }
                    });
        });

        resetButton = v.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharedHostname = pref.getString("hostname", null);
                requests.makeRequest(sharedHostname + resetReq, "Réinitialisation de BlueBox", null, null);
            }
        });
        resetButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String sharedHostname = pref.getString("hostname", null);
                requests.makeRequest(sharedHostname + resetReq +"?hard", "Hard-resetting BlueBox", null, null);
                return true;
            }
        });

        if (autoscanOnOpen) {
            mockScanDevices();
        }
    }

    public void mockScanDevices(){
        String sharedHostname = pref.getString("hostname", null);

        // get Bluetooth devices around by performing request
        requests.makeFakeScan((ArrayList<Device> mockDevicesList) -> {
                deviceList.clear();
                deviceList.addAll(mockDevicesList);
                adapter.notifyDataSetChanged();
                Logger.d("scan finished with size "+deviceList.size());

                // edit sharedPreferences too
                JSONArray deviceListJson = new JSONArray();
                for (Device d: deviceList) {
                    deviceListJson.put(d.toJsonObject().toString());
                }
                editor.putString("devices", deviceListJson.toString());
                Logger.d("sharedPreferences <= "+deviceListJson.toString());
                editor.apply();
        });
    }
}