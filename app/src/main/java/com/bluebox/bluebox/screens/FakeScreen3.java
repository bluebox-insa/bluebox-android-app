package com.bluebox.bluebox.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluebox.bluebox.R;
import com.bluebox.bluebox.utils.Logger;

public class FakeScreen3 extends FakeScreen2 {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("FakeScreen3() created");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.screen_3, container);

        // Initiate the SharedPreferences object
        initSharedPreferences(false);

        // Initiate the SCAN and RESET buttons
        initButtons(view, "/reset_input");

        // Initiate the device list
        initDeviceList(view,"/connect_input/", getResources().getString(R.string.emoji_cellphone));

        // No fake auto-scan, contrary to FakeScreen2
        // fakeAutoScan(view);

        return view;
    }
}
