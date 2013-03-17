package ms.ihc.control.viewer;

import java.util.Iterator;

import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.valueTypes.DeviceType;
import ms.ihc.control.viewer.SoapImpl.ControllerConnection;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;


public class ActiveResourcesActivity extends Activity implements OnClickListener, ControllerConnection {

	private ListView resourceListView;
	private TableRow resourceTableRow;
	private TextView resourceTextView;
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
			resourceListView = (ListView) findViewById(R.id.resourcelist);
			resourceTableRow = (TableRow) findViewById(R.id.HeaderTableRow);
			resourceTextView = (TextView) findViewById(R.id.locationtext1);
	
			resourceAdapter = new ResourceAdapter(getApplicationContext(),soapImp);
	
			Iterator<IHCLocation> iLocations = appContext.getIHCHome().locations.iterator();
			while (iLocations.hasNext()) {
				IHCLocation location = iLocations.next();
				Iterator<IHCResource> iResources = location.resources.iterator();
				while (iResources.hasNext()) {
					IHCResource resource = iResources.next();
					resource.setLocation(location.Name);
					if(resource.type() == DeviceType.INPUT_OUTPUT || resource.type() == DeviceType.OUTPUT)
					{
						if(resource.getState())
							resourceAdapter.addItem(resource);
					}
				}
			}
			
			resourceTextView.setText("< Back");
			resourceListView.setAdapter(resourceAdapter);
			resourceTableRow.setOnClickListener(this);
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
			return soapImp.waitForResourceValueChanges(LocationActivity.resourceMap);
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

}