package com.nikita.receiver;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DeviceAdapter extends ArrayAdapter<WifiP2pDevice> {

    private List<WifiP2pDevice> mDeviceList;

    public DeviceAdapter(Context context, List<WifiP2pDevice> deviceList) {
        super(context, 0);
        this.mDeviceList = deviceList;
    }

    public void refreshList(List<WifiP2pDevice> deviceList) {
        this.mDeviceList.clear();
        this.mDeviceList.addAll(deviceList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);

        TextView name = convertView.findViewById(R.id.device_name);
        TextView address = convertView.findViewById(R.id.device_type);
        name.setText(String.valueOf(mDeviceList.get(position).deviceName));
        address.setText(String.valueOf(mDeviceList.get(position).deviceAddress));

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getPosition(@Nullable WifiP2pDevice item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return mDeviceList.size();
    }
}
