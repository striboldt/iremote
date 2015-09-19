package ms.ihc.control.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import ms.ihc.control.Resource.ResourceActivity;
import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.IHCHome;
import ms.ihc.control.viewer.IHCLocation;
import ms.ihc.control.viewer.R;
import ms.ihc.control.Resource.ResourceAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class LocationFragment extends Fragment implements OnItemClickListener{

	private final static String TAG = LocationFragment.class.getName();
	private ListView locationListView;
	private Handler mHandler = new Handler();
	private ConnectionManager connectionManager = null;
	private IHCHome home = null;
	public static SparseArray<IHCResource> resourceMap = new SparseArray<IHCResource>();
	private enableRuntimeValueNotificationsTask runtimeTask = null;
	public static ResourceAdapter resourceAdapter;
	private SimpleAdapter locationAdapter;
	ArrayList<HashMap<String,String>> list;
	private ApplicationContext appContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (ApplicationContext) getActivity().getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.location_listview,container,false);

		connectionManager = appContext.getIHCConnectionManager();
		if(connectionManager == null)
			getActivity().finish();
		else
		{
			home = appContext.getIHCHome();
			
			if(list == null)
			{
				list = new ArrayList<HashMap<String,String>>();
				for (IHCLocation location : home.getLocations()) {
					HashMap<String, String> map = new HashMap<String, String>();
                    // TODO: Refactor locations to reflect new fragment design
					map.put("location", location.getName());
					map.put("resources", getResources().getString(R.string.resources) + String.valueOf(location.getResources().size()));
					list.add(map);
				}
			}
			String[] from = {"location", "resources"};
			int[] to = {R.id.location,R.id.resources};
	
			
			locationListView = (ListView) view.findViewById(R.id.locationlist);
	
			locationListView.setOnItemClickListener(this);
	
			locationListView.setTextFilterEnabled(true);
			if(runtimeTask == null)
			{
				runtimeTask = new enableRuntimeValueNotificationsTask();
				runtimeTask.execute();
			}
			
			locationAdapter = new SimpleAdapter(appContext, list, R.layout.location_list_item, from,to );
			locationListView.setAdapter(locationAdapter);
		}
		
		return view;
	}


	
	
	// Click on ListView item
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			HashMap<String,String> map = list.get(position);
			String selectedResource = map.get("location");
			
			resourceAdapter = new ResourceAdapter(appContext, connectionManager);

			Iterator<IHCLocation> iLocations = home.getLocations().iterator();
			while (iLocations.hasNext()) {
				IHCLocation location = iLocations.next();
				if (location.getName().equals(selectedResource)) {
					Iterator<IHCResource> iResources = location.getResources().iterator();
					while (iResources.hasNext()) {
						IHCResource ihcResource = iResources.next();
						ihcResource.setLocation("");
						resourceAdapter.addItem(ihcResource);
					}
				}
			}
			
			Intent intent = new Intent(appContext, ResourceActivity.class);
			intent.putExtra("location", selectedResource);
			startActivity(intent);
	}
	
	private class enableRuntimeValueNotificationsTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			resourceMap = new SparseArray<>();
			Iterator<IHCLocation> iLocations = home.getLocations().iterator();
			while (iLocations.hasNext()) {
				IHCLocation location = iLocations.next();
					Iterator<IHCResource> iResources = location.getResources().iterator();
					while (iResources.hasNext()) {
						IHCResource resource = iResources.next();
						resourceMap = resource.getResourceIds(resourceMap);
					}
			}
			connectionManager.enableRuntimeValueNotifications(resourceMap);
			return true;
		}
	}
	
	
    // Called when app is resumed from background
    @Override
    public void onResume() {
		super.onResume();
       // mHandler.postDelayed(waitForResourceValuesChange, 100);
    }

	// Called when app is put into background
	@Override
	public void onPause() {
		super.onPause();
		//mHandler.removeCallbacks(waitForResourceValuesChange);
	}

    // TODO: Not used, but test impact on application
	/*@Override
	public void onBackPressed() {  
		((ApplicationContext) getApplication()).setIsAppRestarted(false);
		super.onBackPressed();
	}*/
	
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
			Log.v(TAG, "Waiting for valuechanges");
			return connectionManager.waitForResourceValueChanges(LocationFragment.resourceMap);
		}

		@Override
		protected void onPostExecute(Boolean refreshListView) {
			appContext.setIsWaitingForValueChanges(false);			
			Log.v(TAG, "Done");
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
			if (LocationFragment.resourceAdapter != null && !connectionManager.isInTouchMode) {
				Log.v(TAG, "Refreshing view...");
				LocationFragment.resourceAdapter.notifyDataSetChanged();
			}
		}
	}


	/*public void onConnectionAccepted() {
		Log.v("LocationActivity", "onConnectionAccepted");
		runtimeTask = new enableRuntimeValueNotificationsTask();
		runtimeTask.execute();
	}

	public void onRuntimeVaulesChanged() {
			new refreshResourceViewTask().execute();
	}*/



}
