package ms.ihc.control.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import ms.ihc.control.viewer.BuildConfig;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;


public class LocationActivity extends BaseActivity {

    private FrameLayout progressLayout;
    private TextView connection_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations);

        progressLayout = (FrameLayout) findViewById(R.id.progressLayout);
        connection_status = (TextView) findViewById(R.id.connection_status);
		TextView versionText = (TextView) findViewById(R.id.version_data);
		versionText.setText("IRemote " + BuildConfig.VERSION_NAME + " Build: " + BuildConfig.VERSION_CODE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.locations);
       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
       // getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Called when the Menu button is pushed */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.locationsmenu, menu);
        return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = this;
		Intent intent;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
            intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
			return true;

		/*case R.menu.active_resources:
			intent = new Intent(this,ActiveResourcesActivity.class);
			startActivity(intent);
			return true;

		case R.menu.favourites:
			intent = new Intent(this,FavouritesActivity.class);
			startActivity(intent);
			return true;*/

		case R.id.action_about:
			Dialog dialog = new Dialog(context);
			dialog.getWindow().setBackgroundDrawableResource(R.color.statusBarColor);
			dialog.setContentView(R.layout.about);
			dialog.setTitle(getResources().getString(R.string.about));
			String version = "";
			int icon = 0;
			try {
				version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
				icon = getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.icon;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}


			TextView email = (TextView) dialog.findViewById(R.id.email);
			TextView author = (TextView) dialog.findViewById(R.id.author);
			TextView text = (TextView) dialog.findViewById(R.id.text);
			TextView versionView = (TextView) dialog.findViewById(R.id.version);

			email.setText(getResources().getString(R.string.email));
			author.setText(getResources().getString(R.string.author));
			text.setText(getResources().getString(R.string.aboutText));
			versionView.setText("Version: " + BuildConfig.VERSION_NAME + " Build: " + BuildConfig.VERSION_CODE);
			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(icon);
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

    @Override
    protected void onMessage(ConnectionManager.IHC_EVENTS event, String Extra) {
        super.onMessage(event, Extra);
    }

    @Override
    protected void setProgressVisibility(boolean visible, String text){
        super.setProgressVisibility(visible,text);
        if(visible){
            progressLayout.setVisibility(View.VISIBLE);
        } else {
            progressLayout.setVisibility(View.GONE);
        }
        connection_status.setText(text);
    }
}
