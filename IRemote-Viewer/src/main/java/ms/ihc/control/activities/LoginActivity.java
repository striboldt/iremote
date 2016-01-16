package ms.ihc.control.activities;


import com.crashlytics.android.Crashlytics;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

import ms.ihc.control.Utils.NetworkUtil;
import ms.ihc.control.Utils.SharedPreferencesHelper;
import ms.ihc.control.Utils.SnackbarHelper;
import ms.ihc.control.WifiSelection.WifiSelectorActivity;
import ms.ihc.control.fragments.AlertDialogFragment;
import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.BuildConfig;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class LoginActivity extends BaseActivity {
    private final String TAG = LoginActivity.class.getName();
    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;

    private static final String PREFS_NAME = "IHCSettings";
    private SharedPreferences sharedPreferences = null;
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvXLkhAGbLsxdP9DMAmAmg14LbqSirf8zlfAV4NL1qlVkNXxqFy3OvNlhZQSiSXV/Ir2JDJy7gK1nBa2yoeQuf0jyo6iGdY/m68Vt1yrQU8ouqOcpOt8626X/XeconYf+aLg55pEXWba5Uhy8To2dd3tjMDMtW6e/EtvOyRPJPNfhK3DS5sk2o8lOCQntgVOnByUgi8iZj6Bm8VdBra2DWXyXqbvhE7m6EXmFUlezWRsjnD+XswIQyHA5DLZ0ZjZ1bwetMMFIb0Ru3dZ1cmtGIRvFoTyvAgX1XJnSFrhBo/9z0/eHTtNReIWQgnJvq+8e79G6rINdL4Xa+kKjhLQIFwIDAQAB";
    // Generate 20 random bytes, and put them here.
    private static final byte[] SALT = new byte[]{-45, 35, 39, -18, -123, -117, 74, -24, 77, 123, -100, -42, 74, -122, -14, -102, -11, 0, -6, 22};

    private RelativeLayout settingsLayout;
    private FrameLayout progressLayout;
    private TextView connection_status;
    private TextView selected_wifi;


    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {


        @Override
        public void allow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            final SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
            final SharedPreferences.Editor editor = saveSettings.edit();
            editor.putBoolean("isLicensed", true);
            editor.apply();
        }

        @Override
        public void dontAllow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            final AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(R.string.unlicensed_dialog_title);
            dialogFragment.show(getSupportFragmentManager(), "dialog");
        }

        @Override
        public void applicationError(int errorCode) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        Log.i(TAG, "Preferred host: " + getPreferredHost());
        Crashlytics.log("Preferred host: " + getPreferredHost());

        progressLayout = (FrameLayout) findViewById(R.id.progressLayout);
        settingsLayout = (RelativeLayout) findViewById(R.id.settingsLayout);
        TextView versionText = (TextView) findViewById(R.id.version_data);
        versionText.setText("IRemote " + BuildConfig.VERSION_NAME + " Build: " + BuildConfig.VERSION_CODE);

        connection_status = (TextView) findViewById(R.id.connection_status);
        Button loginButton = (Button) findViewById(R.id.loginbutton);
        Button reloadProjectButton = (Button) findViewById(R.id.reloadProjectButton);
        Button wifiSelectorButton = (Button) findViewById(R.id.wifibutton);
        selected_wifi = (TextView) findViewById(R.id.selected_wifi);
        setProgressVisibility(true, "");

        // TODO replace getSharedPereferences!!
        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferencesHelper sharedPreferencesHelper = getAppContext().getSharedPreferencesHelper();

        if (!sharedPreferences.getBoolean("isLicensed", true)) {
            final String deviceId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
            // Construct the LicenseCheckerCallback. The library calls this when done.
            mLicenseCheckerCallback = new MyLicenseCheckerCallback();

            // Construct the LicenseChecker with a Policy.
            mChecker = new LicenseChecker(this, new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY);
            mChecker.checkAccess(mLicenseCheckerCallback);
        }


        // Auto login if login is valid
        if (sharedPreferences.getBoolean("hasValidLogin", false) && getPreferredHost() >= 0) {
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");
            String ip = "";
            switch (getPreferredHost()) {
                case NetworkUtil.LAN:
                    if (sharedPreferencesHelper.hasValidLanIp()) {
                        ip = sharedPreferencesHelper.getLanIp();
                    }
                    break;
                case NetworkUtil.WAN:
                    if (sharedPreferencesHelper.hasValidWanIp()) {
                        ip = sharedPreferencesHelper.getWanIp();
                    }
                    break;
            }
            setProgressVisibility(true, getString(R.string.login_msg));
            getAppContext().getIHCConnectionManager().connect(username, password, ip, getPreferredHost() == NetworkUtil.WAN, false);
        } else {
            setProgressVisibility(false, "");
        }

        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (sharedPreferencesHelper.hasValidLanIp()) {
            EditText lan_ip = (EditText) findViewById(R.id.lan_ip);
            lan_ip.setText(sharedPreferencesHelper.getLanIp());
        }

        if (sharedPreferencesHelper.hasValidWanIp()) {
            EditText wan_ip = (EditText) findViewById(R.id.wan_ip);
            wan_ip.setText(sharedPreferencesHelper.getWanIp());
        }

        EditText username = (EditText) findViewById(R.id.username);
        username.setText(this.sharedPreferences.getString("username", ""));

        EditText password = (EditText) findViewById(R.id.password);
        password.setText(this.sharedPreferences.getString("password", ""));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                String ip;

                String wanip = ((EditText) findViewById(R.id.wan_ip)).getText().toString();
                String lanip = ((EditText) findViewById(R.id.lan_ip)).getText().toString();
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    if (!wanip.isEmpty() || !lanip.isEmpty()) {
                    } else {
                        SnackbarHelper.createSnack(LoginActivity.this, String.format("%s %s", getString(R.string.login_failed_msg), getString(R.string.missing_host)));
                        return;
                    }
                } else {
                    SnackbarHelper.createSnack(LoginActivity.this, String.format("%s %s", getString(R.string.login_failed_msg), getString(R.string.missing_login_credentials)));
                    return;
                }

                if (!lanip.isEmpty()) {
                    ip = lanip;
                } else {
                    ip = wanip;
                }

                SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = saveSettings.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putString("lan_ip", lanip);
                editor.putString("wan_ip", wanip);
                editor.apply();

                ((ApplicationContext) getApplicationContext()).getIHCConnectionManager().connect(username, password, ip, false, false);
                setProgressVisibility(true, getString(R.string.login_msg));
            }
        });

        reloadProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                getAppContext().deleteDataFile();
                String ip;

                String wanip = ((EditText) findViewById(R.id.wan_ip)).getText().toString();
                String lanip = ((EditText) findViewById(R.id.lan_ip)).getText().toString();
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    if (!wanip.isEmpty() || !lanip.isEmpty()) {
                    } else {
                        SnackbarHelper.createSnack(LoginActivity.this, String.format("%s %s", getString(R.string.login_failed_msg), getString(R.string.missing_host)));
                        return;
                    }
                } else {
                    SnackbarHelper.createSnack(LoginActivity.this, String.format("%s %s", getString(R.string.login_failed_msg), getString(R.string.missing_login_credentials)));
                    return;
                }

                if (!lanip.isEmpty()) {
                    ip = lanip;
                } else {
                    ip = wanip;
                }

                SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = saveSettings.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putString("lan_ip", lanip);
                editor.putString("wan_ip", wanip);
                editor.apply();

                ((ApplicationContext) getApplicationContext()).getIHCConnectionManager().connect(username, password, ip, false, false);
            }
        });

        wifiSelectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WifiSelectorActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!getAppContext().getSharedPreferencesHelper().getSelectedWiFi().isEmpty()){
            selected_wifi.setText(getAppContext().getSharedPreferencesHelper().getSelectedWiFi());
        }
    }

    @Override
    protected void onMessage(ConnectionManager.IHC_EVENTS event, String message) {
        super.onMessage(event, message);
    }

    // Don't show the options menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void setProgressVisibility(boolean visible, String text) {
        super.setProgressVisibility(visible, text);
        if (visible) {
            progressLayout.setVisibility(View.VISIBLE);
            settingsLayout.setVisibility(View.GONE);

        } else {
            progressLayout.setVisibility(View.GONE);
            settingsLayout.setVisibility(View.VISIBLE);
        }
        connection_status.setText(text);
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            Log.i(TAG, "hideSoftKeyboard: keyboard not visible");
        }
    }

}
