package ms.ihc.control.viewer;

import java.util.Iterator;

import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.fragments.LocationFragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
@Deprecated
@SuppressLint("NewApi")
public class ResourceActivity extends Activity implements OnClickListener{

	private ListView resourceListView;
	//private TableRow resourceTableRow;
	private ConnectionManager soapImp = null;
	private ApplicationContext appContext;
	//private TextView resourceTextView1;
//	private TextView resourceTextView2;
	private String selectedLocation = null;
	
	Handler mHandler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resources);
		appContext = (ApplicationContext) getApplication();
		//soapImp = appContext.getIhcManager();
		
		if(soapImp == null)
			this.finish();
		else
		{
		//	soapImp.setControlerConnectionListener(this);
			Intent i = getIntent();
			selectedLocation = i.getStringExtra("location");
	
			resourceListView = (ListView) findViewById(R.id.resourcelist);
		/*	resourceTableRow = (TableRow) findViewById(R.id.HeaderTableRow);
			resourceTextView1 = (TextView) findViewById(R.id.locationtext1);
			resourceTextView2 = (TextView) findViewById(R.id.locationtext2);
	
			resourceTextView1.setText("< Location: ");
			resourceTextView2.setText(selectedLocation);*/
			registerForContextMenu(resourceListView);
			resourceListView.setAdapter(LocationFragment.resourceAdapter);  
			resourceListView.setOnCreateContextMenuListener(this);
			
		//	resourceTableRow.setOnClickListener(this);
		}
	}

	public void onClick(View v) {
		this.finish();
	}

	// Called when app is resumed from background
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.postDelayed(refreshListViewTask, 100);
	}

	// Called when app is put into background
	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(refreshListViewTask);
	}

	private Runnable refreshListViewTask = new Runnable() {
		public void run() {
			if (!appContext.getIsWaitingForValueChanges()) {
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
					new waitForResourceValueChangesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
				else
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
			Log.v("waitForResourceValueChangesTask-ResourceActivity", "Waiting for valuechanges");
			return soapImp.waitForResourceValueChanges(LocationFragment.resourceMap);
		}

		@Override
		protected void onPostExecute(Boolean refreshListView) {
			appContext.setIsWaitingForValueChanges(false);		
			Log.v("waitForResourceValueChangesTask-ResourceActivity", "Done");
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
			if (LocationFragment.resourceAdapter != null) {
				Log.v("refresjResourceViewTask-ResourceActivity", "Refreshing view...");
				LocationFragment.resourceAdapter.notifyDataSetChanged();
			}
		}
	}
	
	
	@Override 
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(!soapImp.isInTouchMode)
		{
			super.onCreateContextMenu(menu, v, menuInfo);  
			menu.setHeaderTitle(R.string.edit_resource);
			String[] menuItems = getApplicationContext().getResources().getStringArray(R.array.ContextMenu);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
		Iterator<IHCLocation> iLocations = appContext.getIHCHome().locations.iterator();
		while (iLocations.hasNext()) 
		{
			IHCLocation location = iLocations.next();
			if (location.getName().equals(selectedLocation)) {
				Iterator<IHCResource> iResources = location.getResources().iterator();
				while (iResources.hasNext()) { 
					IHCResource ihcResource = iResources.next();
					if(ihcResource.equals((IHCResource) LocationFragment.resourceAdapter.getItem(info.position)))
					{					
						switch (menuItemIndex) 
						{
						case 0: // Add or delete favourite			
							if(ihcResource.isFavourite())
							{
								// remove favourite
								ihcResource.removeAsFavourite();				
							}
							else
							{
								// add favourite
								ihcResource.setAsFavourite();				
							}
							// Save changes to iremote resource file.
							appContext.writeDataFile("iremote.data");
							LocationFragment.resourceAdapter.notifyDataSetChanged();
							break;
						case 1: // Delete Resource
							  iResources.remove();					
							  LocationFragment.resourceAdapter.removeItem(info.position);
							  appContext.writeDataFile("iremote.data");
							break;
							
						default:
							break;
						}
						
						return true;
					}
				}
			}
		}
	  return true;
	}

}
