package com.bluebox.bluebox.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bluebox.bluebox.Device;
import com.bluebox.bluebox.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class Screen1 extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.screen_1, container, false);

        // retrieve SharedPreferences
        pref = getActivity().getSharedPreferences("all", MODE_PRIVATE);
        editor = pref.edit();
        Log.d("onCreateView", getResources().getString(R.string.hostnameDefaultVal));
        editor.putString("hostname", getResources().getString(R.string.hostnameDefaultVal));

        // store result of scan across fragments
        ArrayList<Device> arrayList = new ArrayList<Device>();

        JSONArray jsonArray = new JSONArray(arrayList);

        editor.putString("devices", jsonArray.toString());
        editor.commit();
        return v;
    }
}
