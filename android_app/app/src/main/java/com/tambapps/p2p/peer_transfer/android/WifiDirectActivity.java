package com.tambapps.p2p.peer_transfer.android;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tambapps.p2p.peer_transfer.android.wifidirect.WDBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public abstract class WifiDirectActivity extends AppCompatActivity implements WifiP2pManager.ConnectionInfoListener {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 234567890;

    private final List<WifiP2pDevice> devices = new ArrayList<>();
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private WDBroadcastReceiver receiver;
    private boolean wifiP2pEnabled = false;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeP2PManager();
        checkWifiDirectPermissions();
    }

    private void checkWifiDirectPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    WifiDirectActivity.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), this.getString(R.string.wifi_direct_permissions_not_granted), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initializeP2PManager() {
        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WDBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void setWifiP2pEnabled(boolean wifiP2pEnabled) {
        this.wifiP2pEnabled = wifiP2pEnabled;
    }

    // check if wifiP2pEnabled
    protected void discoverPeers() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank. Code for peer discovery goes in the
                // onReceive method, detailed below.
                onDiscoverPeersSuccess();
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
                onDiscoverPeersFailure(reasonCode);
            }
        });
    }

    public void connect(final String deviceAddress) { // from wifiP2PDevice
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                onConnectSuccess(deviceAddress);
            }

            @Override
            public void onFailure(int reason) {
                onConnectFailure(reason);
            }
        });
    }

    public void disconnect() {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                onDisconnectSuccess();
            }

            @Override
            public void onFailure(int reasonCode) {
                onDisconnectFailure(reasonCode);

            }


        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isConnected) {
            disconnect();
        }
    }

    public boolean isWifiP2pEnabled() {
        return wifiP2pEnabled;
    }

    // methods to override
    public void onThisDeviceChanged(WifiP2pDevice device) { }
    public void onDiscoverPeersSuccess() {}
    public void onDiscoverPeersFailure(int reasonCode) {}
    public void onConnectFailure(int reason) {}
    public void onConnectSuccess(String deviceAddress) {}
    @Override public void onConnectionInfoAvailable(WifiP2pInfo info) { }
    public void onDisconnectSuccess() {}
    public void onDisconnectFailure(int reason) {}


    public List<WifiP2pDevice> getPeers() {
        return this.devices;
    }

    public final void onPeersAvailable(ArrayList<WifiP2pDevice> wifiP2pDevices) {
        if (!devices.equals(wifiP2pDevices)) {
            devices.clear();
            devices.addAll(wifiP2pDevices);
            onPeersChanged(wifiP2pDevices);
        }
    }

    public void onPeersChanged(List<WifiP2pDevice> devices) {}

    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }
}
