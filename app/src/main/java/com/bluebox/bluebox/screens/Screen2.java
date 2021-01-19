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
        View v = inflater.inflate(R.layout.screen_2, container);

        // Initiate the SharedPreferences object
        this.initSharedPreferences(false);

        // Initiate the SCAN and RESET buttons
        this.initButtons(v, "/reset_output");

        // Initiate the device list
        this.initDeviceList(v,"/connect_output", "/reset_output", getResources().getString(R.string.emoji_speaker));

        // Auto-scan
        final ProgressDialog pleaseWaitDialog = ProgressDialog.show(getContext(), "Recherche en cours","Veuillez patienter...", true);
        pleaseWaitDialog.setIcon(R.drawable.ic_bluetooth);

        v.postDelayed(() -> {
            ArrayList<Device> fakeDeviceList = new ArrayList<>();
            fakeDeviceList.add(new Device("BLP9820", "A1:B2:C3:D4:E5:F6", false));
            fakeDeviceList.add(new Device("Samsung A51", "A1:B2:C3:D4:E5:F6", false));
            fakeDeviceList.add(new Device("UE BOOM 2", "A1:B2:C3:D4:E5:F6", false));
            fakeDeviceList.add(new Device("PhilipsBT", "A1:B2:C3:D4:E5:F6", false));
            fakeDeviceList.add(new Device("Bose Revolve SoundLink", "A1:B2:C3:D4:E5:F6", false));

            Logger.d("fake request finished with size "+fakeDeviceList.size());

            pleaseWaitDialog.dismiss();
            this.deviceList.clear();
            this.deviceList.addAll(fakeDeviceList);
            writeDeviceListAndNotifyAdapter();
        }, 2000);

        return v;
    }



//    public void notifyDataSetChanged(){
//        Logger.d("dataset changed");
//        adapter.notifyDataSetChanged();
//    }

    /**
     * @param v the view
     * @param connectReq API route for connecting a device, can be /connect_input or /connect_output
     * @param resetReq API route for resetting, can be /reset_input or /reset_output
     * @param emoji emoji displayed next to a device name when it is connected
     */
    public void initDeviceList(View v, String connectReq, String resetReq, String emoji) {
        ListView devicesComponent = v.findViewById(R.id.deviceList);
        this.adapter = new DeviceAdapter(getContext(), this.deviceList);
        devicesComponent.setAdapter(this.adapter);

        devicesComponent.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {
                    // 1. retrieve informations from JSON string
                    String deviceName = this.deviceList.get(position).name;
                    String deviceMacAddress = this.deviceList.get(position).macAddress;
/*
                    // 2. do GET /connect/<mac_addr>
                    requests.makeRequest("http://fakeURL","fakeMessage",
                            (String response) -> {
                                Logger.d("in response >");
                                this.deviceList.get(position).setConnected(true);
                                this.deviceList.get(position).setEmoji(emoji);
                                writeDeviceListAndNotifyAdapter();
//                        adapter.notifyDataSetChanged();
//                        view.setEnabled(false);
//                        view.setOnClickListener(null);
                            },
                            (Object error) -> {
                                Logger.d("in error >");
                                this.deviceList.get(position).setConnected(true);
                                this.deviceList.get(position).setEmoji(emoji);
                                writeDeviceListAndNotifyAdapter();
//                        adapter.notifyDataSetChanged();
//                        view.setEnabled(false);
//                        view.setOnClickListener(null);
//
//                                    // edit sharedPreferences too
//                                    JSONArray deviceListJson = new JSONArray();
//                                    for (Device d: deviceList) {
//                                        deviceListJson.put(d.toJsonObject().toString());
//                                    }
//                                    editor.putString("devices", deviceListJson.toString());
//                                    Logger.d("sharedPreferences <= "+deviceListJson.toString());
//                                    editor.apply();
//                            });
                            }

                    );*/

                    // FAKE REQUEST
                    final ProgressDialog pleaseWaitDialog = ProgressDialog.show(getContext(), "Connexion en cours","Veuillez patienter...", true);
                    pleaseWaitDialog.setIcon(R.drawable.ic_bluetooth);

                    v.postDelayed(() -> {
                        pleaseWaitDialog.dismiss();
                        this.deviceList.get(position).setConnected(true);
                        this.deviceList.get(position).setEmoji(emoji);
                        writeDeviceListAndNotifyAdapter();
                    }, 2000);
                });
    }

    /**
     * @param v the view
     * @param resetReq API route for resetting, can be /reset_input or /reset_output
     */
    public void initButtons(View v, String resetReq) {
        Button scanButton = v.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v1 -> {
            readHostname();
            requests.makeRequestAndParseJsonArray(
                    this.hostname + "/scan",
                    "Recherche en cours",
                    (JSONArray jsonArray) -> {
                        // remove the former elements from the list
                        this.deviceList.clear();

                        // add the new elements
                        Device newDevice;
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                                if (!jsonObject.getString("name").equals("<unknown>")) {
                                    newDevice = new Device(jsonObject.getString("name"), jsonObject.getString("mac_address"), false);
                                    this.deviceList.add(newDevice);
                                }
                            }
                        } catch (JSONException e) {
                            Logger.e("error parsing JSON "+e);
                        }

                        writeDeviceListAndNotifyAdapter();
//                        this.adapter.notifyDataSetChanged();
                        if (this.deviceList.isEmpty()) {
                            makeText(getContext(), R.string.no_bluetooth_device_found, LENGTH_LONG);
                        }
                    });
        });

        Button resetButton = v.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v2 -> {
            readHostname();
            requests.makeRequest(
                    this.hostname + resetReq,
                    "Réinitialisation de BlueBox",
                    null,
                    null
            );
        });
        resetButton.setOnLongClickListener(v3 -> {
            readHostname();
            requests.makeRequest(
                    this.hostname + resetReq +"?hard",
                    "Hard-resetting BlueBox",
                    null,
                    null
            );
            return true;
        });
    }

    public void fakeScanDevices(){
        // get Bluetooth devices around by performing request
        requests.makeFakeScan((ArrayList<Device> fakeDeviceList) -> {
                deviceList.clear();
                deviceList.addAll(fakeDeviceList);
                Logger.d("fake scan finished with size "+deviceList.size());
                writeDeviceListAndNotifyAdapter();

                // edit sharedPreferences too
//                JSONArray deviceListJson = new JSONArray();
//                for (Device d: deviceList) {
//                    deviceListJson.put(d.toJsonObject().toString());
//                }
//                editor.putString("devices", deviceListJson.toString());
//                Logger.d("sharedPreferences <= "+deviceListJson.toString());
//                editor.apply();
        });
    }

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

    @Override
    protected void readDeviceList() {
        super.readDeviceList();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    protected void writeDeviceListAndNotifyAdapter() {
        super.writeDeviceList();
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        } else {
            Logger.e("WARNING: adapter is NULL");
        }
    }
}