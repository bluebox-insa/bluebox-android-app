package com.bluebox.bluebox.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluebox.bluebox.R;

import static android.widget.Toast.makeText;

public class Screen3 extends Screen2 {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (ViewGroup) inflater.inflate(R.layout.screen_3, null);
        init(v, "/connect_in", "/reset_in");
        return v;
    }
}
