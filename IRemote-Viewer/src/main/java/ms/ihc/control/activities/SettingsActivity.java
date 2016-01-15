package ms.ihc.control.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;

import ms.ihc.control.Utils.SharedPreferencesHelper;
import ms.ihc.control.Utils.SnackbarHelper;
import ms.ihc.control.WifiSelection.WifiSelectorActivity;
import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;


public class SettingsActivity extends BaseActivity {
    private final String TAG = SettingsActivity.class.getName();

    private static final String PREFS_NAME = "IHCSettings";
    private SharedPreferences sharedPreferences = null;
    private Button saveSettingsButton;
    private Button reloadProjectButton;
    private Button wifiSelectorButton;

    private RelativeLayout settingsLayout;
    private FrameLayout progressLayout;
    private TextView connection_status;
    private TextView selected_wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        progressLayout = (FrameLayout) findViewById(R.id.progressLayout);
        settingsLayout = (RelativeLayout) findViewById(R.id.settingsLayout);
        connection_status = (TextView) findViewById(R.id.connection_status);
        reloadProjectButton = (Button) findViewById(R.id.reloadProjectButton);
        wifiSelectorButton = (Button) findViewById(R.id.wifibutton);
        selected_wifi = (TextView) findViewById(R.id.selected_wifi);

        // TODO replace getSharedPereferences!!
        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);


        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(R.string.settings);
        } catch (NullPointerException e) {
            Log.e(TAG, "NPE");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        SnackbarHelper.createSnack(SettingsActivity.this, String.format("%s", getString(R.string.missing_host)));
                        return;
                    }
                } else {
                    SnackbarHelper.createSnack(SettingsActivity.this, String.format("%s", getString(R.string.missing_login_credentials)));
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
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard();

        String wanip = ((EditText) findViewById(R.id.wan_ip)).getText().toString();
        String lanip = ((EditText) findViewById(R.id.lan_ip)).getText().toString();
        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            if (!wanip.isEmpty() || !lanip.isEmpty()) {
            } else {
                SnackbarHelper.createSnack(SettingsActivity.this, String.format("%s", getString(R.string.missing_host)));
                return;
            }
        } else {
            SnackbarHelper.createSnack(SettingsActivity.this, String.format("%s", getString(R.string.missing_login_credentials)));
            return;
        }

        SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = saveSettings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("lan_ip", lanip);
        editor.putString("wan_ip", wanip);
        editor.apply();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(SettingsActivity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            Log.i(TAG, "hideSoftKeyboard: keyboard not visible");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: RESULT_OK");

            } else {
                Log.i(TAG, "onActivityResult: FAILED");
            }
        }

    }
}
