package ms.ihc.control.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.IHCHome;
import ms.ihc.control.viewer.R;


public class LocationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(R.string.locations);
       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
       // getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }




    /** Called when the Menu button is pushed */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.locationsmenu, menu);
        return super.onCreateOptionsMenu(menu);
	}

    /*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = this;
		Intent intent = null;
		// Handle item selection
		switch (item.getItemId()) {
		case R.menu.settings:
			((ApplicationContext) getApplication()).setIsAppRestarted(false);
			this.finish();
			return true;

		case R.menu.active_resources:
			intent = new Intent(this,ActiveResourcesActivity.class);
			startActivity(intent);
			return true;

		case R.menu.favourites:
			intent = new Intent(this,FavouritesActivity.class);
			startActivity(intent);
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
			return super.onOptionsItemSelected(item);

		}
	}*/

    @Override
    protected void onMessage(ConnectionManager.IHC_EVENTS event, String Extra) {
        super.onMessage(event, Extra);
    }
}
