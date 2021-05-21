package com.nikita.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private SearchActivity activity;

    public WifiBroadcastReceiver(WifiP2pManager manager, Channel channel, SearchActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            handleWifiP2pStateChangedAction(intent);
        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            handleWifiP2pDiscoveryChangedAction(intent);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            handleWifiP2pPeersChangedAction();
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            handleWifiP2pConnectionChangedAction(intent);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            handleWifiP2pThisDeviceChangedAction(intent);
        }
    }

    private void handleWifiP2pStateChangedAction(Intent intent) {
        int wifiState = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (wifiState == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            Toast.makeText(activity, "WIFI P2P is ENABLED.", Toast.LENGTH_SHORT).show();
        } else if (wifiState == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
            Toast.makeText(activity, "WIFI P2P is DISABLED.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleWifiP2pDiscoveryChangedAction(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
        if(state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
            Toast.makeText(activity, "Searching device has started.", Toast.LENGTH_SHORT).show();
            activity.toggleLoadingVisibility(false);
            activity.searchBtn.setEnabled(false);
            activity.connectBtn.setEnabled(false);
        } else {
            Toast.makeText(activity, "Searching device has ended.", Toast.LENGTH_SHORT).show();
            activity.toggleLoadingVisibility(false);
            activity.searchBtn.setEnabled(true);
        }
    }


    private void handleWifiP2pPeersChangedAction() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.requestPeers(channel, peerListListener);
        }

    }

    private void handleWifiP2pConnectionChangedAction(Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if(networkInfo.isConnected()) {
            Toast.makeText(activity, "Connection detected.", Toast.LENGTH_LONG).show();
            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {

                }
            });
        }
    }

    private void handleWifiP2pThisDeviceChangedAction(Intent intent) {

    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            List<WifiP2pDevice> refreshedPeers = (List<WifiP2pDevice>) peerList.getDeviceList();
            activity.updateDeviceList(refreshedPeers);
            TextView deviceCnt = activity.findViewById(R.id.deviceCnt);
            if(refreshedPeers.size() == 0) {
                deviceCnt.setText(activity.getText(R.string.no_device));
                activity.toggleLoadingVisibility(false);
                activity.searchBtn.setEnabled(true);
            }
            else {
                deviceCnt.setText(refreshedPeers.size() + " Devices Found.");
                activity.toggleLoadingVisibility(false);
                activity.searchBtn.setEnabled(true);
            }
        }
    };
}
