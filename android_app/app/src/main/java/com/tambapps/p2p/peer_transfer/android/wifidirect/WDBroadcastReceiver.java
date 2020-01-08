package com.tambapps.p2p.peer_transfer.android.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import com.tambapps.p2p.peer_transfer.android.WifiDirectActivity;

import java.util.ArrayList;
import java.util.List;

public class WDBroadcastReceiver extends BroadcastReceiver implements WifiP2pManager.PeerListListener {

    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;
    private final WifiDirectActivity activity;

    public WDBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiDirectActivity wifiDirectActivity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = wifiDirectActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            activity.setWifiP2pEnabled(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed! We should probably do something about
            // that.
            if (manager != null) {
                manager.requestPeers(channel, this);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed! We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = (WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Log.d("WIFI", "This device broadcast");
            activity.onThisDeviceChanged(device);
            /*
            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));

             */
        }
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        activity.onPeersAvailable(new ArrayList<>(peerList.getDeviceList()));
    }

}
