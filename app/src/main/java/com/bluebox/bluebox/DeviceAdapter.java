package com.bluebox.bluebox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<Device> {

    public DeviceAdapter(Context context, List<Device> devices) {
        super(context, R.layout.device_item, devices);
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
//        viewHolder.avatar.setImageDrawable(new ColorDrawable(device.getColor()));

        return convertView;
    }

    private class DeviceViewHolder {
        public TextView name;
    }

}
