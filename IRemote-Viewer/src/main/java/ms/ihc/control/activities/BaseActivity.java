package ms.ihc.control.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ms.ihc.control.Utils.NetworkUtil;
import ms.ihc.control.Utils.SharedPreferencesHelper;
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
    private SharedPreferences sharedPreferences = null;
    private static final String PREFS_NAME = "IHCSettings";
    private static int preferredHost;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == null) {
                Log.e(TAG, "Unhandled action in broadcastReceiver");
                throw new UnsupportedOperationException("Unhandled action in broadcastReceiver");
            }
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

                if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.RESOURCE_VALUE_CHANGED.toString())) {
                    Log.d(TAG, "RESOURCE_VALUE_CHANGED ");
                    onMessage(ConnectionManager.IHC_EVENTS.RESOURCE_VALUE_CHANGED, message);
                } else if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.CONNECTED.toString())) {
                    Log.d(TAG, "CONNECTED ");
                    onMessage(ConnectionManager.IHC_EVENTS.CONNECTED, message);
                } else if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.RECONNECTED.toString())) {
                    Log.d(TAG, "RECONNECTED ");
                    onMessage(ConnectionManager.IHC_EVENTS.RECONNECTED, message);
                } else if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.RECONNECTION_FAILED.toString())) {
                    Log.d(TAG, "RECONNECTION FAILED ");
                    onMessage(ConnectionManager.IHC_EVENTS.RECONNECTION_FAILED, message);
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

                } else if (intent.getAction().equalsIgnoreCase(ConnectionManager.IHC_EVENTS.CONNECTION_FAILED.toString())) {
                    String errorMessage = "";
                    if (intent.hasExtra(ConnectionManager.IHCMESSAGE.class.getName())) {
                        Log.d(TAG, "General Login Message: " + message);
                        if (message.equalsIgnoreCase(ConnectionManager.IHCMESSAGE.INVALID_URI.toString())) {
                            errorMessage = getString(R.string.error_wrong_uri);
                        } else if (message.equalsIgnoreCase(ConnectionManager.IHCMESSAGE.NO_ROUTE_TO_HOST.toString())) {
                            errorMessage = getString(R.string.no_route_to_host);
                        } else if (message.equalsIgnoreCase(ConnectionManager.IHCMESSAGE.CERTIFICATE_NOT_TRUSTED.toString())) {
                            errorMessage = getString(R.string.ssl_connection_error);
                        }
                    } else {
                        Log.d(TAG, "General Login Message.");
                    }
                    onMessage(ConnectionManager.IHC_EVENTS.CONNECTION_FAILED, errorMessage);
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
        if (event == ConnectionManager.IHC_EVENTS.CONNECTED) {
            if (((ApplicationContext) getApplicationContext()).dataFileExists()) {
                this.sharedPreferences.edit().putBoolean("hasValidLogin", true).apply();
                enableRuntimeValueNotifications();
                startLocationActivityAsNewTask();
            } else {
                setProgressVisibility(true, getString(R.string.loading_project_msg));
                ((ApplicationContext) getApplicationContext()).getIHCConnectionManager().loadIHCProject(false, null);
            }
        }
        else if(event == ConnectionManager.IHC_EVENTS.RECONNECTED) {
            enableRuntimeValueNotifications();
            setProgressVisibility(false, null);
        }
        else if (event == ConnectionManager.IHC_EVENTS.PROJECT_LOADED) {
            this.sharedPreferences.edit().putBoolean("hasValidLogin", true).apply();
            enableRuntimeValueNotifications();
            startLocationActivityAsNewTask();
        }
        else if (event == ConnectionManager.IHC_EVENTS.CONNECTION_FAILED || event == ConnectionManager.IHC_EVENTS.DISCONNECTED) {
            if(!(this instanceof LoginActivity)){
                if(!attemptReconnect()){
                    startLoginActivityAsNewTask();
                }
            } else {
                SnackbarHelper.createSnack(BaseActivity.this, String.format("%s %s", getString(R.string.login_failed_msg), Extra));
                setProgressVisibility(false, null);
            }
        }
        else if(event == ConnectionManager.IHC_EVENTS.RECONNECTION_FAILED) {
            startLoginActivityAsNewTask();
        }
        else if (event == ConnectionManager.IHC_EVENTS.NETWORK_CHANGE) {
            if (!(this instanceof LoginActivity) && Extra == "0") {
                startLoginActivityAsNewTask();
            } else {
                if (Extra != "0") {
                    setProgressVisibility(false, "");
                } else {
                    setProgressVisibility(true, getString(R.string.error_no_network));
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferredHost = NetworkUtil.getPreferredHost(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
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
            if(getAppContext().getIHCHome()!= null) {
                getAppContext().getIHCConnectionManager().enableRuntimeValueNotifications(getAppContext().getIHCHome().getAllResourceIDs());
            }
            return true;
        }
    }

    protected ApplicationContext getAppContext() {
        return ((ApplicationContext) getApplication());
    }

    protected void setProgressVisibility(boolean visible, String text) {
    }

    protected int getPreferredHost(){
        return preferredHost;
    }

    private boolean attemptReconnect(){
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        if (sharedPreferences.getBoolean("hasValidLogin", false) && getPreferredHost() >= 0) {
            String ip = "";
            switch (getPreferredHost()){
                case NetworkUtil.LAN:
                    if(sharedPreferencesHelper.hasValidLanIp()){
                        ip = sharedPreferencesHelper.getLanIp();
                    }
                    break;
                case NetworkUtil.WAN:
                    if(sharedPreferencesHelper.hasValidWanIp()){
                        ip = sharedPreferencesHelper.getWanIp();
                    }
                    break;
            }
            setProgressVisibility(true, getString(R.string.reconnecting));
            ((ApplicationContext) getApplicationContext()).getIHCConnectionManager().reconnect(ip, getPreferredHost() == NetworkUtil.WAN);
            return true;
        } else {
            return false;
        }

    }

    private void startLocationActivityAsNewTask(){
        Intent locationIntent = new Intent(this, LocationActivity.class);
        locationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(locationIntent);
    }

    private void startLoginActivityAsNewTask(){
        Intent locationIntent = new Intent(this, LoginActivity.class);
        locationIntent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        startActivity(locationIntent);
    }

}
