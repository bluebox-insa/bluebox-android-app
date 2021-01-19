package com.bluebox.bluebox.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluebox.bluebox.utils.Logger;
import com.bluebox.bluebox.R;

public class Screen3 extends Screen2 {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("Screen3() created");
        View v = (ViewGroup) inflater.inflate(R.layout.screen_3, container);
        init(v, "/connect_input", "/reset_input", getResources().getString(R.string.emoji_cellphone), false);
        return v;
    }
}