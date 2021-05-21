package com.nikita.receiver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cunoraz.gifview.library.GifView;
import com.nikita.receiver.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    GifView wifiGif;
    public Button searchBtn;
    public Button connectBtn;
    ListView deviceList;
    LinearLayout searchLoading;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager manager;
    private Channel channel;
    private List<WifiP2pDevice> peerList;
    private DeviceAdapter mAdapter;
    private WifiBroadcastReceiver receiver;

    int selectedDevice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        addActionsToIntentFilter();
        InitComponents();

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                SearchActivity.this.channel = manager.initialize(getApplicationContext(), getMainLooper(), this);
            }
        });

        receiver = new WifiBroadcastReceiver(manager, channel, this);
    }

    private void InitComponents() {
        searchLoading = findViewById(R.id.searchLoading);
        wifiGif = findViewById(R.id.wifiGif);
        wifiGif.setGifResource(R.drawable.wifi);
        toggleLoadingVisibility(false);

        searchBtn = findViewById(R.id.searchBtn);
        connectBtn = findViewById(R.id.connectBtn);
        deviceList = findViewById(R.id.deviceList);
        deviceList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                connectBtn.setEnabled(true);
                selectedDevice = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                connectBtn.setEnabled(false);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBtn.setEnabled(false);
                toggleLoadingVisibility(true);

                if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.discoverPeers(channel, discoveryListener);
            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiP2pDevice device = peerList.get(selectedDevice);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }



                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(SearchActivity.this, "Connection failed: " + reason, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        peerList = new ArrayList<WifiP2pDevice>();
        mAdapter = new DeviceAdapter(this, peerList);
        deviceList.setAdapter(mAdapter);
    }

    public void toggleLoadingVisibility(boolean flag) {
        if(flag) {
            searchLoading.setVisibility(View.VISIBLE);
            wifiGif.play();
        } else {
            wifiGif.pause();
            searchLoading.setVisibility(View.GONE);
        }
    }

    private void addActionsToIntentFilter() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void DiscoverPeers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionHelper.PERMISSION_FINE_LOCATION);
            return;
        }

        manager.discoverPeers(channel, discoveryListener);
    }

    public void updateDeviceList(List<WifiP2pDevice> updatedList) {
        mAdapter.refreshList(updatedList);
        deviceList.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionHelper.PERMISSION_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(SearchActivity.this, "Required permission is denied.", Toast.LENGTH_LONG).show();
                    SearchActivity.this.finish();
                }
                return;
        }
    }

    public WifiP2pManager.ActionListener discoveryListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            //toggleLoadingVisibility(false);
            //searchBtn.setEnabled(true);
        }

        @Override
        public void onFailure(int reason) {
            toggleLoadingVisibility(false);
            searchBtn.setEnabled(true);
            Toast.makeText(SearchActivity.this, "There is an error in finding devices.", Toast.LENGTH_LONG).show();
        }
    };
}