package ms.ihc.control.viewer;

import java.io.FileInputStream;
import java.io.IOException;
import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;
import ms.ihc.control.fragments.AlertDialogFragment;
import ms.ihc.control.fragments.LocationFragment;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

@Deprecated
public class IHCControl extends FragmentActivity {

	private LicenseCheckerCallback mLicenseCheckerCallback;   
	private LicenseChecker mChecker;
	
	private static final String PREFS_NAME = "IHCSettings";
	private IhcManager ihcManager = null;
	private ProgressDialog dialog;
	
	private SharedPreferences settings = null;
	private Boolean forceReload = false;
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvXLkhAGbLsxdP9DMAmAmg14LbqSirf8zlfAV4NL1qlVkNXxqFy3OvNlhZQSiSXV/Ir2JDJy7gK1nBa2yoeQuf0jyo6iGdY/m68Vt1yrQU8ouqOcpOt8626X/XeconYf+aLg55pEXWba5Uhy8To2dd3tjMDMtW6e/EtvOyRPJPNfhK3DS5sk2o8lOCQntgVOnByUgi8iZj6Bm8VdBra2DWXyXqbvhE7m6EXmFUlezWRsjnD+XswIQyHA5DLZ0ZjZ1bwetMMFIb0Ru3dZ1cmtGIRvFoTyvAgX1XJnSFrhBo/9z0/eHTtNReIWQgnJvq+8e79G6rINdL4Xa+kKjhLQIFwIDAQAB";
	// Generate 20 random bytes, and put them here.    
	private static final byte[] SALT = new byte[] { -45, 35, 39, -18, -123, -117, 74, -24, 77, 123, -100, -42, 74, -122, -14, -102, -11, 0, -6, 22};

	private static final Boolean simulationMode = false;

	private CompoundButton reloadProjectCheckBox;
	private CompoundButton wanCheckBox;
    private EditText wanIpEditText;
    private EditText lanIpEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
    	
    	public void allow() 
    	{            
    		if (isFinishing()) 
    		{                
    			// Don't update UI if Activity is finishing.                
    			return;           
    		}
    		SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = saveSettings.edit();
			editor.putBoolean("isLicensed", true);
			editor.commit();
    	}        
    	
    	public void dontAllow() {          
    		if (isFinishing()) {            
    			// Don't update UI if Activity is finishing.        
    			return;        
  			}
            final AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(R.string.unlicensed_dialog_title);
			dialogFragment.show(getSupportFragmentManager(), "dialog");
    	}

		public void applicationError(ApplicationErrorCode errorCode) {	
		}   
    }
 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(null));
			
		// Reads iremote.data into memory if it exists
		if(!((ApplicationContext) getApplication()).dataFileExists("iremote.data"))
			forceReload = true;

		setContentView(R.layout.settings);
		settings = getSharedPreferences(PREFS_NAME, 0);
        findViewById(R.id.username);
		((EditText) findViewById(R.id.username)).setText(settings.getString("username", ""));
		((EditText) findViewById(R.id.password)).setText(settings.getString("password", ""));
		((EditText) findViewById(R.id.lan_ip)).setText(settings.getString("lan_ip", ""));
		((EditText) findViewById(R.id.wan_ip)).setText(settings.getString("wan_ip", ""));
		
		if (Build.VERSION.SDK_INT >= 14) {
			this.wanCheckBox = (Switch) findViewById(R.id.WanOnly);
			this.reloadProjectCheckBox = (Switch) findViewById(R.id.reloadProject);
		}
		else {
			this.wanCheckBox = (CheckBox) findViewById(R.id.WanOnly);
			this.reloadProjectCheckBox = (CheckBox) findViewById(R.id.reloadProject);
		}
		
		this.wanCheckBox.setChecked(settings.getBoolean("wanonly", false));
		
		if(!settings.getBoolean("isLicensed", false))
		{
			final String deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
			// Construct the LicenseCheckerCallback. The library calls this when done.       
			mLicenseCheckerCallback = new MyLicenseCheckerCallback();
			
			// Construct the LicenseChecker with a Policy.
			mChecker = new LicenseChecker(this, new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY);
			mChecker.checkAccess(mLicenseCheckerCallback);
		}
	}

	/** Called when the Menu button is pushed */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settingsmenu, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle item selection
		switch (item.getItemId()) {
		case R.id.locations:

            String ip;
            String wanip = "";
            String lanip = "";

            this.wanIpEditText = (EditText) findViewById(R.id.wan_ip);

            if(this.wanIpEditText.getText() != null)
                wanip =  this.wanIpEditText.getText().toString();


            this.lanIpEditText = (EditText) findViewById(R.id.lan_ip);
            if(this.lanIpEditText.getText() != null)
                lanip = this.lanIpEditText.getText().toString();

			if (this.wanCheckBox.isChecked()) {
				ip = wanip;
			} else {
				ip = lanip;
			}

	
            final String username = ((EditText) findViewById(R.id.username)).getText().toString();
            final String password = ((EditText) findViewById(R.id.password)).getText().toString();

            final SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
            final SharedPreferences.Editor editor = saveSettings.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("lan_ip", lanip);
            editor.putString("wan_ip", wanip);
            editor.putString("active_ip", ip);
            editor.putBoolean("wanonly", this.wanCheckBox.isChecked());
            editor.putBoolean("reloadProject",this.reloadProjectCheckBox.isChecked());
            editor.commit();

            new LoginTask().execute(username, password, ip);
			return true;

		case R.id.about:
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.about);
			dialog.setTitle(getResources().getString(R.string.about));
			String version = "";
			int icon = 0;
			try {
				version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
				icon = getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.icon;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			
			final TextView email = (TextView) dialog.findViewById(R.id.email);
            final TextView author = (TextView) dialog.findViewById(R.id.author);
            final TextView text = (TextView) dialog.findViewById(R.id.text);
            final TextView versionView = (TextView) dialog.findViewById(R.id.version);
			
			email.setText(getResources().getString(R.string.email));
			author.setText(getResources().getString(R.string.author));
			text.setText(getResources().getString(R.string.aboutText));
			versionView.setText("Version: " + version);

            final ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(icon);
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}


	
	// Called when app is put into background
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	// Called when app is resumed from background
	@Override
	protected void onResume() {
		super.onResume();
		
		if(settings.getBoolean("autologin", false) && ((ApplicationContext) getApplication()).getIsAppRestarted())
		{
            String ip;
            String wanip = "";
            String lanip = "";
            String username = "";
            String password = "";

            this.wanIpEditText = (EditText) findViewById(R.id.wan_ip);

            if(this.wanIpEditText.getText() != null)
                wanip =  wanIpEditText.getText().toString();


            this.lanIpEditText = (EditText) findViewById(R.id.lan_ip);
            if(this.lanIpEditText.getText() != null)
                lanip = lanIpEditText.getText().toString();

            if (this.wanCheckBox.isChecked()) {
                ip = wanip;
            } else {
                ip = lanip;
            }


            this.usernameEditText = (EditText) findViewById(R.id.username);
            if(this.usernameEditText.getText() != null)
			    username = this.usernameEditText.getText().toString();


            this.passwordEditText = (EditText) findViewById(R.id.password);
            if(this.passwordEditText.getText() != null)
			    password = this.passwordEditText.getText().toString();

			final SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
			final SharedPreferences.Editor editor = saveSettings.edit();
			editor.putString("username", username);
			editor.putString("password", password);
			editor.putString("lan_ip", lanip);
			editor.putString("wan_ip", wanip);
			editor.putString("active_ip", ip);
			editor.putBoolean("wanonly", this.wanCheckBox.isChecked());
			editor.commit();

			new LoginTask().execute(username, password, ip);
		}
		((ApplicationContext) getApplication()).setIsAppRestarted(true);

	}

	private class LoginTask extends AsyncTask<String, Void, Boolean> {

		private String loginMessage = "";
		
		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = ihcManager.authenticate(params[0], params[1],
                    params[2], settings.getBoolean("wanonly", false));
			if (!result)
				loginMessage = ihcManager.getLoginMessage();
			return result;
		}
		
		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(IHCControl.this, "IHC", getResources().getString(R.string.login_msg), true ,true);
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				new LoadIHCProjectTask().execute(settings.getBoolean("reloadProject", true));
			} else {
				dialog.cancel();
				int duration = Toast.LENGTH_LONG;

                if (isFinishing())
                    return;

                final Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_failed_msg) + loginMessage, duration);
				toast.show();
			}
		}
	}

	private class LoadIHCProjectTask extends AsyncTask<Boolean, Void, Boolean> {
	
		
		protected void onPreExecute()
		{
			dialog.setMessage(getResources().getString(R.string.loading_project_msg));
		}
		
		@Override
		protected Boolean doInBackground(Boolean... params) {
			// open the file for reading
            FileInputStream streamIn = null;

            if(simulationMode) {
				try {     
					streamIn = new FileInputStream("/sdcard/onshuis.vis");
					 //streamIn = new FileInputStream("/sdcard/julio.xml");
				}
				catch (IOException e) 
				{     
					e.printStackTrace();
					//You'll need to add proper error handling here
				}
            }

			if(params[0] || forceReload)
			{
				((ApplicationContext) getApplication()).setIHCHome(ihcManager.getIHCProject(simulationMode, streamIn));
				if(((ApplicationContext) getApplication()).writeDataFile("iremote.data"))
					forceReload = false;
				
				final SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
				final SharedPreferences.Editor editor = saveSettings.edit();
				editor.putBoolean("reloadProject",false);
				editor.commit();
			}
			return true;
		}

		protected void onPostExecute(Boolean b) {
			dialog.cancel();
			final Intent intent = new Intent(getApplicationContext(), LocationFragment.class);
			startActivity(intent);
		}
	}
	
	 @Override    
	 protected void onDestroy() {        
		 super.onDestroy();
	 }
	 
	 
	 





}
