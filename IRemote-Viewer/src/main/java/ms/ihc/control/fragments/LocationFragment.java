package ms.ihc.control.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import ms.ihc.control.resource.ResourceActivity;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.IHCHome;
import ms.ihc.control.viewer.IHCLocation;
import ms.ihc.control.viewer.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class LocationFragment extends BaseFragment implements OnItemClickListener{

	private final static String TAG = LocationFragment.class.getName();

	private ArrayList<HashMap<String,String>> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.generic_listview,container,false);
		ListView locationListView = (ListView) view.findViewById(R.id.listview);

		ConnectionManager connectionManager = getApplicationContext().getIHCConnectionManager();
		if(connectionManager == null)
			getActivity().finish();
		else
		{
			IHCHome home = getApplicationContext().getIHCHome();
			
			if(home != null && list == null)
			{
				list = new ArrayList<>();
				for (IHCLocation location : home.getLocations()) {
					HashMap<String, String> map = new HashMap<>();
					map.put("location", location.getName());
					map.put("resources", getResources().getString(R.string.resources) + String.valueOf(location.getResources().size()));
					list.add(map);
				}
			}
			String[] from = {"location", "resources"};
			int[] to = {R.id.location,R.id.resources};

	
			locationListView.setOnItemClickListener(this);
			locationListView.setTextFilterEnabled(true);

			SimpleAdapter locationAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.location_list_item, from,to );
			locationListView.setAdapter(locationAdapter);
			//Crashlytics.getInstance().core.setUserIdentifier();
		}


		return view;
	}
	
	// Click on ListView item
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			HashMap<String,String> map = list.get(position);
			String selectedResource = map.get("location");

			Intent intent = new Intent(getApplicationContext(), ResourceActivity.class);
			intent.putExtra("location", selectedResource);
			startActivity(intent);
	}
	
    // Called when app is resumed from background
    @Override
    public void onResume() {
		super.onResume();
    }

	// Called when app is put into background
	@Override
	public void onPause() {
		super.onPause();
	}

    // TODO: Not used, but test impact on application
	/*@Override
	public void onBackPressed() {  
		((ApplicationContext) getApplication()).setIsAppRestarted(false);
		super.onBackPressed();
	}*/


	/*public void onConnectionAccepted() {
		Log.v("LocationActivity", "onConnectionAccepted");
		runtimeTask = new enableRuntimeValueNotificationsTask();
		runtimeTask.execute();
	}

	public void onRuntimeVaulesChanged() {
			new refreshResourceViewTask().execute();
	}*/



}
