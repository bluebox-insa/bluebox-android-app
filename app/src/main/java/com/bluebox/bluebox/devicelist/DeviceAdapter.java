package com.bluebox.bluebox.devicelist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluebox.bluebox.R;
import com.bluebox.bluebox.devicelist.Device;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<Device> {

    public DeviceAdapter(Context context, List<Device> devices) {
        super(context, R.layout.device_item, devices);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_item, parent,false);
        }

        DeviceViewHolder viewHolder = (DeviceViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new DeviceViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.whole_layout);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Device> deviceList
        Device device = getItem(position);
        viewHolder.name.setText(device.name);

        //change color and text of TextView if connected !
        if (device.isConnected) {
            viewHolder.name.setTextColor(Color.BLACK);
            viewHolder.name.setText(device.emoji + "  " + device.name);
//            Logger.d("got emoji "+device.emoji+" at position "+position );

            viewHolder.layout.setBackgroundColor(Color.WHITE);
        } else {
            viewHolder.name.setTextColor(Color.WHITE);
        }
        return convertView;
    }

    private class DeviceViewHolder {
        public TextView name;
        public RelativeLayout layout;
    }
}