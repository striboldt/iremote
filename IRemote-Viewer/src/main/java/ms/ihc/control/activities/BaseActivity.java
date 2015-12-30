package ms.ihc.control.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import ms.ihc.control.Utils.NetworkUtil;
import ms.ihc.control.Utils.SnackbarHelper;
import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;

/**
 * Created by mortenstriboldt on 02/02/14.
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private final String WIFI_INTENT_ACTION = "android.net.wifi.WIFI_STATE_CHANGED";
    private final String CONNECTIVITY_CHANGE_INTENT_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private static int status;
    private FrameLayout progressLayout;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Network related messages
            if (intent.getAction().equals(CONNECTIVITY_CHANGE_INTENT_ACTION) || intent.getAction().equals(WIFI_INTENT_ACTION)) {
                if (NetworkUtil.getConnectivityStatusString(getApplication()) != status) {
                    status = NetworkUtil.getConnectivityStatusString(getApplication());
                    if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                        SnackbarHelper.createSnack(BaseActivity.this, String.format("%s", getString(R.string.error_no_network)));
                    }
                    onMessage(ConnectionManager.IHC_EVENTS.NETWORK_CHANGE, String.valueOf(status));
                }
            } else {
                String message = intent.getStringExtra(ConnectionManager.IHCMESSAGE.class.getName());
                if (message == null) {
                    message = "Ukendt fejl";
                }

                if (intent.getAction() == null) {
                    Log.e(TAG, "Unhandled action in broadcastReceiver");
                    throw new UnsupportedOperationException("Unhandled action in broadcastReceiver");
                }

                if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.RESOURCE_VALUE_CHANGED.toString())) {
                    Log.d(TAG, "RESOURCE_VALUE_CHANGED ");
                    onMessage(ConnectionManager.IHC_EVENTS.RESOURCE_VALUE_CHANGED, message);
                } else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.CONNECTED.toString())) {
                    Log.d(TAG, "CONNECTED ");
                    onMessage(ConnectionManager.IHC_EVENTS.CONNECTED, message);
                } else if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.DISCONNECTED.toString())) {

                    if (intent.hasExtra(ConnectionManager.IHCMESSAGE.class.getName())) {
                        Log.d(TAG, "DISCONNECTED. Message: " + message);
                    } else {
                        Log.d(TAG, "DISCONNECTED.");
                    }
                    onMessage(ConnectionManager.IHC_EVENTS.DISCONNECTED, message);
                    SnackbarHelper.createSnack(BaseActivity.this, String.format("%s %s", getString(R.string.login_failed_msg), message));

                } else if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.PROJECT_LOADED.toString())) {

                    if (intent.hasExtra(ConnectionManager.IHCMESSAGE.class.getName())) {
                        Log.d(TAG, "Project Loaded. Message: " + message);
                    } else {
                        Log.d(TAG, "Project Loaded.");
                    }
                    onMessage(ConnectionManager.IHC_EVENTS.PROJECT_LOADED, message);

                } else if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.GENERAL_LOGIN_MESSAGE.toString())) {
                    String errorMessage = "";
                    if (intent.hasExtra(ConnectionManager.IHCMESSAGE.class.getName())) {
                        Log.d(TAG, "General Login Message: " + message);
                        if(message.equalsIgnoreCase(ConnectionManager.IHCMESSAGE.INVALID_URI.toString())){
                            errorMessage = getString(R.string.error_wrong_uri);
                        } else if (message.equalsIgnoreCase(ConnectionManager.IHCMESSAGE.NO_ROUTE_TO_HOST.toString())){
                            errorMessage = getString(R.string.no_route_to_host);
                        }
                    } else {
                        Log.d(TAG, "General Login Message.");
                    }
                    SnackbarHelper.createSnack(BaseActivity.this, String.format("%s %s", getString(R.string.login_failed_msg), errorMessage));
                    onMessage(ConnectionManager.IHC_EVENTS.GENERAL_LOGIN_MESSAGE, message);
                } else {
                    Log.e(TAG, "Unhandled action in broadcastReceiver");
                    throw new UnsupportedOperationException("Unhandled action in broadcastReceiver");
                    //onMessage(ConnectionManager.IHC_EVENTS.PROJECT_LOADED, message);
                }

            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (ConnectionManager.IHC_EVENTS ihc_event : ConnectionManager.IHC_EVENTS.values()) {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(ihc_event.toString()));
        }

        status = NetworkUtil.getConnectivityStatusString(getApplicationContext());
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            onMessage(ConnectionManager.IHC_EVENTS.NETWORK_CHANGE, String.valueOf(status));
        }

        IntentFilter filters = new IntentFilter();
        filters.addAction(WIFI_INTENT_ACTION);
        filters.addAction(CONNECTIVITY_CHANGE_INTENT_ACTION);
        registerReceiver(broadcastReceiver, new IntentFilter(filters));

    }

    protected void onMessage(ConnectionManager.IHC_EVENTS event, String Extra) {
        if (event == ConnectionManager.IHC_EVENTS.NETWORK_CHANGE && Extra == "0" && !(this instanceof SettingsActivity)) {
            Intent locationIntent = new Intent(this, SettingsActivity.class);
            locationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(locationIntent);
        } else if(event == ConnectionManager.IHC_EVENTS.GENERAL_LOGIN_MESSAGE && !(this instanceof SettingsActivity)){
            // Attempt relogin
            showSpinner(true);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressLayout = (FrameLayout)findViewById(R.id.progressLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void enableRuntimeValueNotifications() {

        enableRuntimeValueNotificationsTask runtimeTask = new enableRuntimeValueNotificationsTask();
        runtimeTask.execute();
    }

    private class enableRuntimeValueNotificationsTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            getAppContext().getIHCConnectionManager().enableRuntimeValueNotifications(getAppContext().getIHCHome().getAllResourceIDs());
            return true;
        }
    }

    private ApplicationContext getAppContext() {
        return ((ApplicationContext) getApplicationContext());
    }

    private void showSpinner(boolean showSpinner){
            if(showSpinner){
                progressLayout.setVisibility(View.VISIBLE);
            } else {
                progressLayout.setVisibility(View.GONE);
            }
    }


}
