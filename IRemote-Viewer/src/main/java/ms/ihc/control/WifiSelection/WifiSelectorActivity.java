package ms.ihc.control.WifiSelection;


import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ms.ihc.control.Utils.NetworkUtil;
import ms.ihc.control.activities.BaseActivity;
import ms.ihc.control.viewer.R;

public class WifiSelectorActivity extends BaseActivity {

    private FrameLayout progressLayout;
    private TextView connection_status;
    private List<WifiConfiguration> wifiConfigurationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_selector);

        progressLayout = (FrameLayout) findViewById(R.id.progressLayout);
        connection_status = (TextView) findViewById(R.id.connection_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.select_wifi);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ListView wifiListView = (ListView) findViewById(R.id.wifi_listview);

        wifiConfigurationList = NetworkUtil.getKnowWifiNetworks(getApplicationContext());

        List<Map<String,String>> list = new ArrayList<>();
        for (WifiConfiguration wifiConfiguration : wifiConfigurationList) {
            Map<String, String> map = new HashMap<>();
            map.put("wifi_ssid", wifiConfiguration.SSID.replace("\"",""));
            list.add(map);
        }

        String[] from = {"wifi_ssid"};
        int[] to = {R.id.wifi_ssid};

        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiConfiguration wifiConfiguration = wifiConfigurationList.get(position);
                NetworkUtil.setPreferredWifi(wifiConfiguration.SSID.replace("\"",""), getApplicationContext());
                finish();
            }
        });
        wifiListView.setTextFilterEnabled(true);

        SimpleAdapter locationAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.wifi_list_item, from, to);
        wifiListView.setAdapter(locationAdapter);
    }

}
