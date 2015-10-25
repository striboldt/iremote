package ms.ihc.control.Resource;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.Iterator;

import ms.ihc.control.activities.BaseFragmentActivity;
import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.fragments.LocationFragment;
import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.IHCLocation;
import ms.ihc.control.viewer.R;


public class ResourceActivity extends BaseFragmentActivity {

    private static final String TAG = ResourceActivity.class.getName();
    private ResourceAdapter resourceAdapter;
    private ListView resourceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resources);

        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String location = getIntent().getStringExtra("location");
        ApplicationContext applicationContext = (ApplicationContext)getApplicationContext();

        resourceListView = (ListView) findViewById(R.id.resourcelist);
        resourceAdapter = new ResourceAdapter(applicationContext);

        Log.v(TAG,"Loading resources");
        Iterator<IHCLocation> iLocations = applicationContext.getIHCHome().getLocations().iterator();
        while (iLocations.hasNext()) {
            IHCLocation ihcLocation = iLocations.next();
            if (ihcLocation.getName().equals(location)) {
                Iterator<IHCResource> iResources = ihcLocation.getResources().iterator();
                while (iResources.hasNext()) {
                    IHCResource ihcResource = iResources.next();
                    ihcResource.setLocation("");
                    resourceAdapter.addItem(ihcResource);
                }
            }
        }

        registerForContextMenu(resourceListView);
        resourceListView.setAdapter(resourceAdapter);
        resourceListView.setOnCreateContextMenuListener(this);


        getSupportActionBar().setTitle(location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            //TODO: What if in touch mode
            if (resourceAdapter != null) {
                Log.v(TAG, "Refreshing view...");
                resourceAdapter.notifyDataSetChanged();
            }
        }
    }
}
