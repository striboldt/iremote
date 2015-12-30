package ms.ihc.control.Resource;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ms.ihc.control.activities.BaseActivity;
import ms.ihc.control.fragments.ResourceListFragment;
import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;


public class ResourceActivity extends BaseActivity {

    private static final String TAG = ResourceActivity.class.getName();
    ResourceListFragment resourceListFragment;
    private FrameLayout progressLayout;
    private TextView connection_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resources);

        progressLayout = (FrameLayout) findViewById(R.id.progressLayout);
        connection_status = (TextView) findViewById(R.id.connection_status);

        // Set a toolbar to replace the action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        resourceListFragment = (ResourceListFragment) getFragmentManager().findFragmentById(R.id.resourcefragment);


        String location = getIntent().getStringExtra("location");

        try {
            getSupportActionBar().setTitle(location);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } catch (NullPointerException e){
            Log.e(TAG, "NPE");
        }

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

    @Override
    protected void onMessage(ConnectionManager.IHC_EVENTS event, String Extra) {
        super.onMessage(event,Extra);
        if(event == ConnectionManager.IHC_EVENTS.RESOURCE_VALUE_CHANGED) {
            resourceListFragment.onResourceChanged();
        }
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
