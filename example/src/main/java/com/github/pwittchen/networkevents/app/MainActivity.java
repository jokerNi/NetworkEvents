/*
 * Copyright (C) 2015 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pwittchen.networkevents.app;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.NetworkHelper;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.github.pwittchen.networkevents.library.event.WifiSignalStrengthChanged;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Exemplary activity showing how to use NetworkEvents library.
 * Take a closer look on onConnectivityChanged and onWifiSignalStrengthChanged methods
 * as well as @Subscribe annotations, initialization of Bus and NetworkEvents classes.
 */
public class MainActivity extends Activity {
    private Bus bus;
    private NetworkEvents networkEvents;

    private TextView connectivityStatus;
    private ListView accessPoints;

    @Subscribe
    public void onConnectivityChanged(ConnectivityChanged event) {
        connectivityStatus.setText(event.getConnectivityStatus().toString());
    }

    @Subscribe
    public void onWifiSignalStrengthChanged(WifiSignalStrengthChanged event) {
        List<String> wifiScanResults = new ArrayList<>();

        for (ScanResult scanResult : NetworkHelper.getWifiScanResults(this)) {
            wifiScanResults.add(scanResult.SSID);
        }

        accessPoints.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiScanResults));
        Toast.makeText(this, getString(R.string.wifi_signal_strength_changed), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectivityStatus = (TextView) findViewById(R.id.connectivity_status);
        accessPoints = (ListView) findViewById(R.id.access_points);
        bus = new Bus();
        networkEvents = new NetworkEvents(this, bus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        networkEvents.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
        networkEvents.unregister();
    }
}
