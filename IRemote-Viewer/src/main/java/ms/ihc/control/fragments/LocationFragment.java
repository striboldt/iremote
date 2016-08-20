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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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
	private IHCHome home;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			// Restore last state
			IHCHome restoredIHChome = (IHCHome) savedInstanceState.getSerializable("home");
			getApplicationContext().setIHCHome(restoredIHChome);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("home", home);
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

			home = getApplicationContext().getIHCHome();
			
			if(home != null)
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
			if(list != null){
				SimpleAdapter locationAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.location_list_item, from,to );
				locationListView.setAdapter(locationAdapter);
			}
		}


		return view;
	}
	
	// Click on ListView item
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			HashMap<String,String> map = list.get(position);
			String selectedResource = map.get("location");

			Intent intent = new Intent(getApplicationContext(), ResourceActivity.class);
			intent.putExtra("location", selectedResource);

		ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
				// the context of the activity
				getActivity(),

				// For each shared element, add to this method a new Pair item,
				// which contains the reference of the view we are transitioning *from*,
				// and the value of the transitionName attribute
				new Pair<>(v.findViewById(R.id.location), getString(R.string.transition_location_name))
		);
		ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
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
