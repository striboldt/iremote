package ms.ihc.control.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;

import java.util.List;

import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.R;

/**
 * Created by mortenstriboldt on 04/12/15.
 */
public class NetworkUtil {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0, NETWORK_STAUS_WIFI = 1, NETWORK_STATUS_MOBILE = 2;

    public static final int LAN = 0;
    public static final int WAN = 1;
    public static final int NO_CONNECTION = -1;
    public static final int LAN_NOT_AVAILABLE_FROM_MOBILE = -2;
    private static String PREFERRED_WIFI = "";

    public NetworkUtil() {
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        int status = 0;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = NETWORK_STAUS_WIFI;
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = NETWORK_STATUS_MOBILE;
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = NETWORK_STATUS_NOT_CONNECTED;
        }
        return status;
    }

    private static String getWifiSSIDName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public static int getPreferredHost(Activity activity) {

        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(activity);
        int status = getConnectivityStatusString(activity);
        if (status == NETWORK_STATUS_NOT_CONNECTED) {
            return NO_CONNECTION;
        } else if (status == NETWORK_STATUS_MOBILE && sharedPreferencesHelper.hasValidWanIp()) {
            // We can only connect to public IP (WAN side)
            return WAN;
        } else if (status == NETWORK_STAUS_WIFI && getWifiSSIDName(activity) == PREFERRED_WIFI && sharedPreferencesHelper.hasValidLanIp()) {
            // We are on the preferred WiFi network which should have access to LAN IP
            return LAN;
        } else if (status == NETWORK_STAUS_WIFI && sharedPreferencesHelper.hasValidWanIp()) {
            // We are not located on the same LAN as the controller, therefore connect to WAN IP
            return WAN;
        } else if (status == NETWORK_STAUS_WIFI && sharedPreferencesHelper.hasValidLanIp()) {
            // Attempt to connect to LAN even though we don't know if possible
            return LAN;
        } else if (status == NETWORK_STATUS_MOBILE && sharedPreferencesHelper.hasValidLanIp()) {
            SnackbarHelper.createSnack(activity,String.format("%s %s", activity.getString(R.string.login_failed_msg), activity.getString(R.string.lan_not_available)));
            return LAN_NOT_AVAILABLE_FROM_MOBILE;
        } else {
            return LAN_NOT_AVAILABLE_FROM_MOBILE;
        }
    }

    public static List<WifiConfiguration> getKnowWifiNetworks(Context context){
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm.getConfiguredNetworks();
    }

    public static void setPreferredWifi(String preferredWifi, Context context) {
        ((ApplicationContext)context.getApplicationContext()).getSharedPreferencesHelper().setSelectedWiFi(preferredWifi);
        PREFERRED_WIFI = preferredWifi;
    }
}
