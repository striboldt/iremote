package ms.ihc.control.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import ms.ihc.control.viewer.ConnectionManager;

/**
 * Created by mortenstriboldt on 02/02/14.
 */
public class BaseFragmentActivity extends AppCompatActivity {
    private static final String TAG = BaseFragmentActivity.class.getSimpleName();


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra(ConnectionManager.IHCMESSAGE.class.getName());
            if(message == null){
                message = "Ukendt fejl";
            }

            if(intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.RESOURCE_VALUE_CHANGED.toString())) {
                Log.d(TAG,"RESOURCE_VALUE_CHANGED ");
            }
            else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.CONNECTED.toString())) {
                Log.d(TAG,"CONNECTED ");
                onMessage(ConnectionManager.IHC_EVENTS.CONNECTED, message);
            }
            else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.DISCONNECTED.toString())) {

                if(intent.hasExtra(ConnectionManager.IHCMESSAGE.class.getName())){
                    Log.d(TAG, "DISCONNECTED. Message: " + message );
                } else {
                    Log.d(TAG, "DISCONNECTED.");
                }
                onMessage(ConnectionManager.IHC_EVENTS.DISCONNECTED, message);

            }
            else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.PROJECT_LOADED.toString())) {

                if(intent.hasExtra(ConnectionManager.IHCMESSAGE.class.getName())){
                    Log.d(TAG, "Project Loaded. Message: " + message );
                } else {
                    Log.d(TAG, "Project Loaded.");
                }
                onMessage(ConnectionManager.IHC_EVENTS.PROJECT_LOADED, message);

            } else {
                Log.e(TAG, "Unhandled action in broadcastReceiver");
                throw new UnsupportedOperationException("Unhandled action in broadcastReceiver");
                //onMessage(ConnectionManager.IHC_EVENTS.PROJECT_LOADED, message);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ConnectionManager.IHC_EVENTS.RESOURCE_VALUE_CHANGED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ConnectionManager.IHC_EVENTS.CONNECTED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ConnectionManager.IHC_EVENTS.DISCONNECTED.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ConnectionManager.IHC_EVENTS.PROJECT_LOADED.toString()));
    }

    protected void onMessage(ConnectionManager.IHC_EVENTS event, String Extra){}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }
}
