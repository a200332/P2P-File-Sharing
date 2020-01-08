package com.tambapps.p2p.peer_transfer.android;


import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WifiDirectConnectionActivity extends WifiDirectActivity {

    private RecyclerView.Adapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        recyclerAdapter = new MyAdapter(getPeers());
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    int contentLayoutId() {
        return R.layout.activity_wifi_direct;
    }

    @Override
    public void setWifiP2pEnabled(boolean wifiP2pEnabled) {
        super.setWifiP2pEnabled(wifiP2pEnabled);
        if (wifiP2pEnabled) {
            discoverPeers();
        }
    }

    @Override
    public void onThisDeviceChanged(WifiP2pDevice device) {
        TextView nameText = findViewById(R.id.peer_name);
        nameText.setText(getString(R.string.peer_name, device.deviceName));
        TextView addressText = findViewById(R.id.peer_address);
        addressText.setText(getString(R.string.peer_address, device.deviceAddress));
    }

    @Override
    public void onDiscoverPeersSuccess() {
        Toast.makeText(this, "Discovery initiation complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDiscoverPeersFailure(int reasonCode) {
        Toast.makeText(this, "Failed to initiate discover peers " + reasonCode, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPeersChanged(List<WifiP2pDevice> devices) {
        recyclerAdapter.notifyDataSetChanged();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private final List<WifiP2pDevice> peers;

        private MyAdapter(List<WifiP2pDevice> peers) {
            this.peers = peers;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.peer_element, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final WifiP2pDevice device = peers.get(position);
            // TODO set views
            holder.position = position;
            holder.nameText.setText(getString(R.string.peer_name, device.deviceName));
            holder.addressText.setText(getString(R.string.peer_address, device.deviceAddress));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(WifiDirectConnectionActivity.this)
                            .setTitle(getString(R.string.connect_to, device.deviceName))
                            .setMessage(getString(R.string.connect_to_question, device.deviceName))
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(WifiDirectConnectionActivity.this, "TODO", Toast.LENGTH_SHORT).show();
                                    // TODO connect on socket
                                }
                            })
                            .setNeutralButton(R.string.no, null)
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return peers.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private final TextView nameText;
            private final TextView addressText;
            int position;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.element_peer_name);
                addressText = itemView.findViewById(R.id.element_peer_address);
            }
        }
    }


}
