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

public class FakeScreen2 extends Screen2 {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("FakeScreen2() created");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.screen_2, container);

        // Initiate the SharedPreferences object
        initSharedPreferences(false);

        // Initiate the SCAN and RESET buttons
        initButtons(view, "/reset_output");

        // Initiate the device list
        initDeviceList(view,"/connect_output/", getResources().getString(R.string.emoji_speaker));

        // Fake auto-scan
        fakeAutoScan(view);

        return view;
    }


    /**
     * @param view the view
     * @param connectReq API route for connecting a device, can be /connect_input or /connect_output
     * @param emoji emoji displayed next to a device name when it is connected
     */
    @Override
    protected void initDeviceList(View view, String connectReq, String emoji) {
        ListView devicesComponent = view.findViewById(R.id.deviceList);
        this.adapter = new DeviceAdapter(getContext(), this.deviceList);
        devicesComponent.setAdapter(this.adapter);

        // Simulate a fake request on item click
        devicesComponent.setOnItemClickListener(
                (AdapterView<?> parent, View itemView, int position, long id) -> {
                    // create a progress dialog
                    final ProgressDialog pleaseWaitDialog = ProgressDialog.show(getContext(), "Connexion en cours","Veuillez patienter...", true);
                    pleaseWaitDialog.setIcon(R.drawable.ic_bluetooth);

                    // after 1 second, dismiss the dialog and set the item as connected
                    view.postDelayed(() -> {
                        pleaseWaitDialog.dismiss();
                        this.deviceList.get(position).setConnected(true);
                        this.deviceList.get(position).setEmoji(emoji);
                        writeDeviceListAndNotifyAdapter();
                    }, 1000);
                });
    }

    protected void fakeAutoScan(View view) {
        final ProgressDialog pleaseWaitDialog = ProgressDialog.show(getContext(), "Recherche en cours","Veuillez patienter...", true);
        pleaseWaitDialog.setIcon(R.drawable.ic_bluetooth);

        view.postDelayed(() -> {
            ArrayList<Device> fakeDeviceList = new ArrayList<>();
            fakeDeviceList.add(new Device("BLP9820", "A1:B2:C3:D4:E5:F6", false, null));
            fakeDeviceList.add(new Device("Samsung A51", "A1:B2:C3:D4:E5:F6", false, null));
            fakeDeviceList.add(new Device("UE BOOM 2", "A1:B2:C3:D4:E5:F6", false, null));
            fakeDeviceList.add(new Device("PhilipsBT", "A1:B2:C3:D4:E5:F6", false, null));
            fakeDeviceList.add(new Device("Bose Revolve SoundLink", "A1:B2:C3:D4:E5:F6", false, null));

            Logger.d("fake request finished with size "+fakeDeviceList.size());
            pleaseWaitDialog.dismiss();

            this.deviceList.clear();
            this.deviceList.addAll(fakeDeviceList);
            writeDeviceListAndNotifyAdapter();
        }, 2000);
    }
}