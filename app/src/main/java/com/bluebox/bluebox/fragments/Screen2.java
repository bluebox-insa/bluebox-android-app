package com.bluebox.bluebox.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import com.bluebox.bluebox.R;
import com.bluebox.bluebox.RequestHelper;
import com.bluebox.bluebox.Device;
import com.bluebox.bluebox.DeviceAdapter;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static com.bluebox.bluebox.MainActivity.request;

public class Screen2 extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private List<Device> deviceList;
    private DeviceAdapter adapter;
    private ListView devicesComponent;
    private Button scanButton;
    private Button resetButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (ViewGroup) inflater.inflate(R.layout.screen_2, null);
        init(v,"/connect_output", "/reset_output", getResources().getString(R.string.emoji_speaker));
        return v;
    }

    public void init(View v, String connectReq, String resetReq, String emoji) {
        // retrieve SharedPreferences
        pref = getActivity().getSharedPreferences("all", MODE_PRIVATE);
        editor = pref.edit();

        /**-------------------------------------------------------------------------
         *                                  devicesList
         *
         * - the SCAN button retrieves an array of surrounding Bluetooth devices
         *   in JSON-format and adds them to the list
         * - click on a device to connect to it
         * - long-press on a device to copy its MAC address to the macAddr EditText
         *--------------------------------------------------------------------------*/

        devicesComponent = (ListView) v.findViewById(R.id.deviceList);
        deviceList = new ArrayList<Device>();
        deviceList.add(new Device("Enceinte 1", "A1:B2:C3:D4:E5:F6", false));
        deviceList.add(new Device("Enceinte 2", "A1:B2:C3:D4:E5:F6", false));
        deviceList.add(new Device("Appuyez sur SCAN pour ajouter des enceintes", "A1:B2:C3:D4:E5:F6", false));
        deviceList.add(new Device("Appuyez sur RESET pour déconnecter toutes les enceintes", "A1:B2:C3:D4:E5:F6", false));
        adapter = new DeviceAdapter(getContext(), deviceList, emoji);
        devicesComponent.setAdapter(adapter);

        devicesComponent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 1. retrieve informations from JSON string
                String deviceName = deviceList.get(position).name;;
                String deviceMacAddress = deviceList.get(position).macAddress;
                makeText(getContext(), "mac_addr is "+deviceMacAddress, LENGTH_LONG);

                // 2. do GET /connect/<mac_addr>
                String sharedHostname = pref.getString("hostname", null);
                Boolean res = request.makeRequest( sharedHostname + connectReq+ "/" + deviceMacAddress, true, "Connexion à " + deviceName, null);
                Log.d("Screen2 makrequests", res.toString());

                // change item state and UI when connected
                if (res == true) {
                    deviceList.get(position).isConnected = true;
                    adapter.notifyDataSetChanged();
                    view.setBackgroundColor(Color.WHITE);
                    view.setEnabled(false);
                    view.setOnClickListener(null);
                }else{
                    deviceList.get(position).isConnected = false;
                }
            }
        });

        // BUTTONS
        scanButton = v.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharedHostname = pref.getString("hostname", null);
                request.makeRequestAndParseJsonArray(sharedHostname + "/scan", true, null, new RequestHelper.CallbackJsonArray() {
                    @Override
                    public void onResponse(JSONArray jsonArray) throws JSONException {
                        // remove the former elements from the list
                        deviceList.clear();

                        // add the new elements
                        Device newDevice;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                            if (!jsonObject.getString("name").equals("<unknown>")) {
                                newDevice = new Device(jsonObject.getString("name"), jsonObject.getString("mac_address"), false);
                                deviceList.add(newDevice);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if(deviceList.isEmpty()){
                            makeText(getContext(), R.string.no_bluetooth_device_found, LENGTH_LONG);
                        }
                    }
                });
            }
        });

        resetButton = v.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharedHostname = pref.getString("hostname", null);
                request.makeRequest(sharedHostname + resetReq, true, "Réinitialisation de BlueBox", null);
            }
        });
        resetButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String sharedHostname = pref.getString("hostname", null);
                request.makeRequest(sharedHostname + resetReq +"?hard", true, "Hard-resetting BlueBox", null);
                return true;
            }
        });
    }
}
