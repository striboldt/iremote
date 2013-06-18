package ms.ihc.control.viewer;

import java.util.Iterator;

import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.fragments.LocationFragment;

import ms.ihc.control.viewer.SoapImpl.ControllerConnection;
import android.app.Activity;
import android.os.AsyncTask;
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


public class FavouritesActivity extends Activity implements OnClickListener, ControllerConnection {

	private ListView favouritesListView;
	private TableRow favouritesTableRow;
	private TextView favouritesTextView;
	private SoapImpl soapImp = null;
	private ResourceAdapter resourceAdapter;
	private ApplicationContext appContext;
	
	Handler mHandler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resources);
		appContext = (ApplicationContext) getApplication();
		soapImp = appContext.getIhcConnector();
		if(soapImp == null)
			this.finish();
		else
		{
			soapImp.setControlerConnectionListener(this);
			favouritesListView = (ListView) findViewById(R.id.resourcelist);
			favouritesTableRow = (TableRow) findViewById(R.id.HeaderTableRow);
			favouritesTextView = (TextView) findViewById(R.id.locationtext1);
	
			resourceAdapter = new ResourceAdapter(getApplicationContext(),soapImp);
	
			Iterator<IHCLocation> iLocations = appContext.getIHCHome().locations.iterator();
			while (iLocations.hasNext()) {
				IHCLocation location = iLocations.next();
				Iterator<IHCResource> iResources = location.resources.iterator();
				while (iResources.hasNext()) {
					IHCResource resource = iResources.next();
					resource.setLocation(location.Name);	
					if(resource.isFavourite())
						resourceAdapter.addItem(resource);
					
				}
			}
			
			favouritesTextView.setText("< Back");	
			registerForContextMenu(favouritesListView);
			favouritesListView.setAdapter(resourceAdapter);
			favouritesListView.setOnCreateContextMenuListener(this);
			favouritesTableRow.setOnClickListener(this);
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
			Log.v("waitForResourceValueChangesTask", "Waiting for valuechanges");
			return soapImp.waitForResourceValueChanges(LocationFragment.resourceMap);
		}

		@Override
		protected void onPostExecute(Boolean refreshListView) {
			if(refreshListView)
				new refreshResourceViewTask().execute();
			appContext.setIsWaitingForValueChanges(false);
			Log.v("waitForResourceValueChangesTask", "Done");
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
			if (resourceAdapter != null && !soapImp.isInTouchMode) {
				Log.v("refresjResourceViewTask", "Refreshing view...");
				resourceAdapter.notifyDataSetChanged();
			}
		}
	}

	public void onConnectionAccepted() {
		// TODO Auto-generated method stub
		
	}

	public void onRuntimeVaulesChanged() {
		new refreshResourceViewTask().execute();
	}
	
	@Override 
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(!soapImp.isInTouchMode)
		{
			super.onCreateContextMenu(menu, v, menuInfo);  
			menu.setHeaderTitle(R.string.edit_resource);
			String[] menuItems = getApplicationContext().getResources().getStringArray(R.array.ContextMenuRemoveFav);
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
			Iterator<IHCResource> iResources = location.resources.iterator();
			while (iResources.hasNext()) { 
				IHCResource ihcResource = iResources.next();
				if(ihcResource.equals((IHCResource) resourceAdapter.getItem(info.position)))
				{					
					switch (menuItemIndex) 
					{
					case 0: 		
							// remove favourite
							ihcResource.removeAsFavourite();								
							// Save changes to iremote resource file.
							appContext.writeDataFile("iremote.data");
							resourceAdapter.removeItem(info.position);
							resourceAdapter.notifyDataSetChanged();
						break;				
					default:
						break;
					}
						
					return true;
				}
			}
		}
	  return true;
	}
}