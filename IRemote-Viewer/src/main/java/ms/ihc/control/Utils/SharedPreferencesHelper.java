package ms.ihc.control.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mortenstriboldt on 04/12/15.
 */
public class SharedPreferencesHelper {

    private static SharedPreferencesHelper instance;
    private SharedPreferences sharedPreferences = null;
    private final String PREFS_NAME = "IHCSettings";

    public SharedPreferencesHelper(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public static SharedPreferencesHelper getInstance(Context context){
        if(instance == null){
            instance = new SharedPreferencesHelper(context);
        }
        return instance;
    }

    public boolean hasValidLanIp(){
        return !sharedPreferences.getString("lan_ip", "").isEmpty();
    }

    public boolean hasValidWanIp(){
        return !sharedPreferences.getString("wan_ip", "").isEmpty();
    }

    public String getLanIp() {
        return sharedPreferences.getString("lan_ip", "");
    }

    public String getWanIp() {
        return sharedPreferences.getString("wan_ip", "");
    }
}
