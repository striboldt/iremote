package ms.ihc.control.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.viewer.SoapImpl.ControllerConnection;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LocationActivity extends Activity implements OnItemClickListener, ControllerConnection{

	private ListView locationListView;
	private Handler mHandler = new Handler();
	private SoapImpl soapImp = null;
	private IHCHome home = null;
	public static HashMap<Integer,IHCResource> resourceMap = new HashMap<Integer, IHCResource>();
	private enableRuntimeValueNotificationsTask runtimeTask = null;
	public static ResourceAdapter resourceAdapter;
	private SimpleAdapter locationAdapter;
	ArrayList<HashMap<String,String>> list;
	private ApplicationContext appContext;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (ApplicationContext) getApplication();
		appContext.setIsWaitingForValueChanges(false);
		soapImp = appContext.getIhcConnector();
		if(soapImp == null)
			this.finish();
		else
		{
			soapImp.setControlerConnectionListener(this);
			home = appContext.getIHCHome();
			
			if(list == null)
			{
				list = new ArrayList<HashMap<String,String>>();
				for (IHCLocation location : home.locations) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("location", location.Name);
					map.put("resources", getResources().getString(R.string.resources) + String.valueOf(location.resources.size()));
					list.add(map);
				}
			}
			String[] from = {"location", "resources"};
			int[] to = {R.id.location,R.id.resources};
	
			setContentView(R.layout.locations);
			locationListView = (ListView) findViewById(R.id.locationlist);
	
			locationListView.setOnItemClickListener(this);
	
			locationListView.setTextFilterEnabled(true);
			if(runtimeTask == null)
			{
				runtimeTask = new enableRuntimeValueNotificationsTask();
				runtimeTask.execute();
			}
			
			locationAdapter = new SimpleAdapter( this, list, R.layout.location_list_item, from,to );
			locationListView.setAdapter(locationAdapter);
		}
	}
	
	/** Called when the Menu button is pushed */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.locationsmenu, menu);
		return true;
	}
	
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
	}
	
	
	// Click on ListView item
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			HashMap<String,String> map = list.get(position);
			String selectedResource = map.get("location");
			
			resourceAdapter = new ResourceAdapter(getApplicationContext(),soapImp);

			Iterator<IHCLocation> iLocations = home.locations.iterator();
			while (iLocations.hasNext()) {
				IHCLocation location = iLocations.next();
				if (location.Name.equals(selectedResource)) {
					Iterator<IHCResource> iResources = location.resources.iterator();
					while (iResources.hasNext()) {
						IHCResource ihcResource = iResources.next();
						ihcResource.setLocation("");
						resourceAdapter.addItem(ihcResource);
					}
				}
			}
			
			Intent intent = new Intent(this,ResourceActivity.class);
			intent.putExtra("location", selectedResource);
			startActivity(intent);
	}
	
	private class enableRuntimeValueNotificationsTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			resourceMap = new HashMap<Integer, IHCResource>();
			Iterator<IHCLocation> iLocations = home.locations.iterator();
			while (iLocations.hasNext()) {
				IHCLocation location = iLocations.next();
					Iterator<IHCResource> iResources = location.resources.iterator();
					while (iResources.hasNext()) {
						IHCResource resource = iResources.next();
						resourceMap = resource.getResourceIds(resourceMap);
					}
			}
			soapImp.enableRuntimeValueNotifications(resourceMap);
			return true;
		}
	}
	
	
	// Called when app is resumed from background
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.postDelayed(waitForResourceValuesChange, 100);
	}

	// Called when app is put into background
	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(waitForResourceValuesChange);
	}
	
	@Override 
	public void onBackPressed() {  
		((ApplicationContext) getApplication()).setIsAppRestarted(false);
		super.onBackPressed();
	}
	
	private Runnable waitForResourceValuesChange = new Runnable() {
		public void run() {
			if (!appContext.getIsWaitingForValueChanges()) {
				new waitForResourceValueChangesTask().execute();
			}	
			mHandler.postDelayed(this, 1000);
		}
	};


	private class waitForResourceValueChangesTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			appContext.setIsWaitingForValueChanges(true);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Log.v("waitForResourceValueChangesTask-LocationActivity", "Waiting for valuechanges");
			return soapImp.waitForResourceValueChanges(LocationActivity.resourceMap);
		}

		@Override
		protected void onPostExecute(Boolean refreshListView) {
			appContext.setIsWaitingForValueChanges(false);			
			Log.v("waitForResourceValueChangesTask-LocationActivity", "Done");
		}
	}
	
	private class refreshResourceViewTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (LocationActivity.resourceAdapter != null && !soapImp.isInTouchMode) {
				Log.v("refresjResourceViewTask-ResourceActivity", "Refreshing view...");
				LocationActivity.resourceAdapter.notifyDataSetChanged();
			}
		}
	}


	public void onConnectionAccepted() {
		Log.v("LocationActivity", "onConnectionAccepted");
		runtimeTask = new enableRuntimeValueNotificationsTask();
		runtimeTask.execute();
	}

	public void onRuntimeVaulesChanged() {
			new refreshResourceViewTask().execute();
	}

}
