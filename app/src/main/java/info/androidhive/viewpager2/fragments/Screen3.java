package info.androidhive.viewpager2.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.viewpager2.R;
import info.androidhive.viewpager2.RequestHelper;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;
import static info.androidhive.viewpager2.MainActivity.request;

public class Screen3 extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String sharedHostname;

    private List<String> deviceList;
    private ArrayAdapter<String> adapter;
    private ListView devicesComponent;
    private Button scanButton;
    private Button resetButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (ViewGroup) inflater.inflate(R.layout.screen_3, null);

        // retrieve SharedPreferences
        pref = getActivity().getSharedPreferences("all", MODE_PRIVATE);
        editor = pref.edit();
        sharedHostname = pref.getString("hostname", null);


        /**-------------------------------------------------------------------------
         *                                  devicesList
         *
         * - the SCAN button retrieves an array of surrounding Bluetooth devices
         *   in JSON-format and adds them to the list
         * - click on a device to connect to it
         * - long-press on a device to copy its MAC address to the macAddr EditText
         *--------------------------------------------------------------------------*/
        deviceList = new ArrayList<String>();
        deviceList.add("a");
        deviceList.add("b");
        deviceList.add("c");

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_activated_1, deviceList);
        devicesComponent = (ListView) v.findViewById(R.id.deviceList);
        devicesComponent.setAdapter(adapter);
        devicesComponent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 1. retrieve informations from JSON string
                String deviceMacAddress = null;
                String deviceName = null;
                try {
                    JSONObject jsonObject = new JSONObject(deviceList.get(position));
                    deviceMacAddress = jsonObject.getString("mac_address");
                    deviceName = jsonObject.getString("name");
                } catch (JSONException e) {
                    makeText(getContext(), "error there is nothing", LENGTH_LONG).show();
                    Log.e("setOnItemClickListener", e.toString());
                    return;
                }

                // 2. do GET /connect/<mac_addr>
                request.makeRequest( sharedHostname +"/connect_input/" + deviceMacAddress, true, deviceName+" is connected ", null);
            }
        });



        // BUTTONS
        scanButton = v.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.makeRequestAndParseJsonArray(sharedHostname + "/scan", true, null, new RequestHelper.CallbackJsonArray() {
                    @Override
                    public void onResponse(JSONArray jsonArray) throws JSONException {
                        deviceList.clear();                                // remove the former elements from the list
                        for (int i = 0; i < jsonArray.length(); i++) {      // add the new elements
                            deviceList.add(jsonArray.getString(i));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        resetButton = v.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeText(getContext(), "reset button", LENGTH_LONG).show();
                request.makeRequest(sharedHostname+"/disconnect_all", true, "BlueBox is reset ", null);
            }
        });
        resetButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                request.makeRequest(sharedHostname+"/remove_all", true, "BlueBox is hard-reset", null);
                return true;
            }
        });

        return v;
    }
}
