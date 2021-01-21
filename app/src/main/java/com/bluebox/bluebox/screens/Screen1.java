package com.bluebox.bluebox.screens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluebox.bluebox.MainActivity;
import com.bluebox.bluebox.R;
import com.bluebox.bluebox.utils.Logger;

import static com.bluebox.bluebox.utils.ScanIpAddresses.scanIpAddresses;

public class Screen1 extends Screen0 {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("\n");
        Logger.i("Screen1() created");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.screen_1, container);

        // Initiate the SharedPreferences object
        initSharedPreferences(false);

        init(view);

        return view;
    }

    public void init(View view) {
        Button openSettings = view.findViewById(R.id.openSettings);

        /* Redirect to settings app
        openSettings.setOnClickListener(v2 -> {
            Intent tetherSettings = new Intent();
            tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings");
            startActivity(tetherSettings);
        });

         */
        openSettings.setOnClickListener(v2 -> {


            String ipFound = scanIpAddresses();
            if (ipFound != null) {
                this.hostname = "http://" + ipFound + ":12345";
                writeHostname();

                //update view : bluebox connected to the hotspot
                ProgressBar progressBar=getView().findViewById(R.id.progressBar);
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                
                TextView textView_connexion=(TextView)getView().findViewById(R.id.textView_connexion);
                textView_connexion.setText("Connecté");
                textView_connexion.setTextColor(Color.GREEN);
            }

            //return true;
        });
    }
}
