package ms.ihc.control.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ms.ihc.control.viewer.ConnectionManager;

/**
 * Created by mortenstriboldt on 02/02/14.
 */
public class BaseFragmentActivity extends AppCompatActivity {
    private static final String TAG = BaseFragmentActivity.class.getSimpleName();


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHCEVENTS.RESOURCE_VALUE_CHANGED.toString())) {
                Log.d(TAG,"RESOURCE_VALUE_CHANGED ");
            }
            else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHCEVENTS.CONNECTED.toString())) {
                Log.d(TAG,"CONNECTED ");
                onMessage(ConnectionManager.IHCEVENTS.CONNECTED);
            }
            else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHCEVENTS.DISCONNECTED.toString())) {

                if(intent.hasExtra(ConnectionManager.IHCMESSAGE.class.getName())){
                    String message = intent.getStringExtra(ConnectionManager.IHCMESSAGE.class.getName());
                    Log.d(TAG, "DISCONNECTED. Message: " + message );
                } else {
                    Log.d(TAG, "DISCONNECTED.");
                }

            }
            else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHCEVENTS.PROJECT_LOADED.toString())) {

                if(intent.hasExtra(ConnectionManager.IHCMESSAGE.class.getName())){
                    String message = intent.getStringExtra(ConnectionManager.IHCMESSAGE.class.getName());
                    Log.d(TAG, "Project Loaded. Message: " + message );
                } else {
                    Log.d(TAG, "Project Loaded.");
                }
                onMessage(ConnectionManager.IHCEVENTS.PROJECT_LOADED);

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
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ConnectionManager.IHCEVENTS.RESOURCE_VALUE_CHANGED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ConnectionManager.IHCEVENTS.CONNECTED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ConnectionManager.IHCEVENTS.DISCONNECTED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ConnectionManager.IHCEVENTS.PROJECT_LOADED.toString()));
    }

    protected void onMessage(ConnectionManager.IHCEVENTS event){}

}
