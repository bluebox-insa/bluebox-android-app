package com.bluebox.bluebox;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<Device> {
    String emoji;

    public DeviceAdapter(Context context, List<Device> devices, String emoji) {
        super(context, R.layout.device_item, devices);
        this.emoji = emoji;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_item, parent,false);
        }

        DeviceViewHolder viewHolder = (DeviceViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new DeviceViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Device> deviceList
        Device device = getItem(position);
        viewHolder.name.setText(device.name);

        //change color and text of TextView if connected !
        if (device.isConnected) {
            viewHolder.name.setTextColor(Color.BLACK);
            viewHolder.name.setText(emoji + "  " + viewHolder.name.getText());
        }else{
            viewHolder.name.setTextColor(Color.WHITE);
        }
        return convertView;
    }

    private class DeviceViewHolder {
        public TextView name;
    }
}
