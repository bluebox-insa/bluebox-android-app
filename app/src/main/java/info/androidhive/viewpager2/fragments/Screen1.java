package info.androidhive.viewpager2.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import info.androidhive.viewpager2.R;

import static android.content.Context.MODE_PRIVATE;

public class Screen1 extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    EditText hostnameText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.screen_1, container, false);

        // retrieve SharedPreferences
        pref = getActivity().getSharedPreferences("all", MODE_PRIVATE);
        editor = pref.edit();
        Log.d("onCreateView", getResources().getString(R.string.hostnameDefaultVal));
        editor.putString("hostname", getResources().getString(R.string.hostnameDefaultVal));
        editor.commit();

        hostnameText = (EditText) v.findViewById(R.id.hostnameText);
        hostnameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String input = hostnameText.getText().toString();
                editor.putString("hostname", input);
                editor.commit();
                Log.d("AfterTextChanged", "hostname set to : " + input);
            }
        });

        return v;
    }
}
