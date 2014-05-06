package ms.ihc.control.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ms.ihc.control.viewer.IhcManager;

/**
 * Created by mortenstriboldt on 02/02/14.
 */
public class BaseFragmentActivity extends FragmentActivity {
    private static final String TAG = BaseFragmentActivity.class.getPackage().getName();


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null && intent.getAction().equalsIgnoreCase(IhcManager.IHCEVENTS.RESOURCE_VALUE_CHANGED.toString())) {
                Log.d(TAG,"RESOURCE_VALUE_CHANGED ");
            }
            else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(IhcManager.IHCEVENTS.CONNECTED.toString())) {
                Log.d(TAG,"CONNECTED ");
            }
            else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(IhcManager.IHCEVENTS.DISCONNECTED.toString())) {
                Log.d(TAG,"DISCONNECTED ");
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(IhcManager.IHCEVENTS.RESOURCE_VALUE_CHANGED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(IhcManager.IHCEVENTS.CONNECTED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(IhcManager.IHCEVENTS.DISCONNECTED.toString()));
    }

}
