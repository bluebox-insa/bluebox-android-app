package com.bluebox.bluebox.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bluebox.bluebox.utils.Logger;
import com.bluebox.bluebox.R;

public class Screen4 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("Screen4() created");
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.screen_4, container, false);

        return v;
    }
}
