package com.tambapps.p2p.peer_transfer.android;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.tambapps.p2p.peer_transfer.android.wifidirect.WDBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public abstract class WifiDirectActivity extends AppCompatActivity {

    private final List<WifiP2pDevice> devices = new ArrayList<>();
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private WifiP2pManager manager;
    private WDBroadcastReceiver receiver;
    private boolean wifiP2pEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(contentLayoutId());
        initializeP2PManager();
    }


    @LayoutRes
    abstract int contentLayoutId();

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


    public boolean isWifiP2pEnabled() {
        return wifiP2pEnabled;
    }

    // method to override
    public void onThisDeviceChanged(WifiP2pDevice device) { }

    public void onDiscoverPeersSuccess() {}

    public void onDiscoverPeersFailure(int reasonCode) {}

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

}
