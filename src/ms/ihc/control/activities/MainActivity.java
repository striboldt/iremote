package ms.ihc.control.activities;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;
import ms.ihc.control.viewer.CustomExceptionHandler;
import ms.ihc.control.viewer.R;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;



public class MainActivity extends Activity {
	private LicenseCheckerCallback mLicenseCheckerCallback;   
	private LicenseChecker mChecker;
	
	private static final String PREFS_NAME = "IHCSettings";
	private SharedPreferences settings = null;
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvXLkhAGbLsxdP9DMAmAmg14LbqSirf8zlfAV4NL1qlVkNXxqFy3OvNlhZQSiSXV/Ir2JDJy7gK1nBa2yoeQuf0jyo6iGdY/m68Vt1yrQU8ouqOcpOt8626X/XeconYf+aLg55pEXWba5Uhy8To2dd3tjMDMtW6e/EtvOyRPJPNfhK3DS5sk2o8lOCQntgVOnByUgi8iZj6Bm8VdBra2DWXyXqbvhE7m6EXmFUlezWRsjnD+XswIQyHA5DLZ0ZjZ1bwetMMFIb0Ru3dZ1cmtGIRvFoTyvAgX1XJnSFrhBo/9z0/eHTtNReIWQgnJvq+8e79G6rINdL4Xa+kKjhLQIFwIDAQAB";
	// Generate 20 random bytes, and put them here.    
	private static final byte[] SALT = new byte[] { -45, 35, 39, -18, -123, -117, 74, -24, 77, 123, -100, -42, 74, -122, -14, -102, -11, 0, -6, 22};
	Button next;
	
	
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
    		showDialog(0);
			
    	}

		public void applicationError(ApplicationErrorCode errorCode) {	
		}   
    }
    
    protected Dialog onCreateDialog(int id) {
        // We have only one dialog.
        return new AlertDialog.Builder(this)
                .setTitle(R.string.unlicensed_dialog_title)
                .setMessage(R.string.unlicensed_dialog_body)
                .setPositiveButton(R.string.buy_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData( Uri.parse(
                                "market://details?id=" + getPackageName()));
                        startActivity(marketIntent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
 
                .setCancelable(false)
                .setOnKeyListener(new DialogInterface.OnKeyListener(){
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
                        finish();
                        return true;
					}
                })
                .create();
 
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(null));
		
		if(!settings.getBoolean("isLicensed", false))
		{
			String deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);  
			// Construct the LicenseCheckerCallback. The library calls this when done.       
			mLicenseCheckerCallback = new MyLicenseCheckerCallback();
			
			// Construct the LicenseChecker with a Policy.
			mChecker = new LicenseChecker(this, new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY);
			mChecker.checkAccess(mLicenseCheckerCallback);
		}
		
		if(settings.getBoolean("hasValidLogin", false)){
			setContentView(R.layout.main);
		}
		else
			setContentView(R.layout.settings);	

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
		Context context = this;

		// Handle item selection
	/*	switch (item.getItemId()) {
		case R.menu.locations:
				String ip;

				String wanip = ((EditText) findViewById(R.ihc.wan_ip)).getText().toString();
				String lanip = ((EditText) findViewById(R.ihc.lan_ip)).getText().toString();
				if (wanCheckBox.isChecked()) {
					ip = wanip;
				} else {
					ip = lanip;
				}
	
				String username = ((EditText) findViewById(R.ihc.username)).getText().toString();
				String password = ((EditText) findViewById(R.ihc.password)).getText().toString();
	
				SharedPreferences saveSettings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = saveSettings.edit();
				editor.putString("username", username);
				editor.putString("password", password);
				editor.putString("lan_ip", lanip);
				editor.putString("wan_ip", wanip);
				editor.putString("active_ip", ip);
				editor.putBoolean("autologin", this.autoLoginBox.isChecked());
				editor.putBoolean("wanonly", this.wanCheckBox.isChecked());
				editor.putBoolean("reloadProject",this.reloadProjectCheckBox.isChecked());
				editor.commit();
	
				new LoginTask().execute(username, password, ip);
			return true;

		case R.menu.about:
			Dialog dialog = new Dialog(context);
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
			
			
			TextView email = (TextView) dialog.findViewById(R.id.email);
			TextView author = (TextView) dialog.findViewById(R.id.author);
			TextView text = (TextView) dialog.findViewById(R.id.text);
			TextView versionView = (TextView) dialog.findViewById(R.id.version);
			
			email.setText(getResources().getString(R.string.email));
			author.setText(getResources().getString(R.string.author));
			text.setText(getResources().getString(R.string.aboutText));
			versionView.setText("Version: " + version);
			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(icon);
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);*/
		return true;

		}
	}
