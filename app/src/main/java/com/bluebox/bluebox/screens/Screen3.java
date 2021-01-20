package com.bluebox.bluebox.screens;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluebox.bluebox.utils.Logger;
import com.bluebox.bluebox.R;

public class Screen3 extends Screen2 {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("Screen3() created");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.screen_3, container);

        // Initiate the SharedPreferences object
        this.initSharedPreferences(false);

        // Initiate the SCAN and RESET buttons
        this.initButtons(view, "/reset_input");

        // Initiate the device list
        this.initDeviceList(view,"/connect_input/", getResources().getString(R.string.emoji_cellphone));

        return view;
    }
}