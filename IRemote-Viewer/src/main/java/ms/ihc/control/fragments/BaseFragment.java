package ms.ihc.control.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.viewer.ApplicationContext;

/**
 * Created by mortenstriboldt on 13/12/15.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getName();
    private ApplicationContext appContext;
    private Handler mHandler = new Handler();
    private SparseArray<IHCResource> resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = (ApplicationContext) getActivity().getApplicationContext();
        try {
            resources = appContext.getIHCHome().getAllResourceIDs();
        } catch (NullPointerException e){
            Log.i(TAG, "onCreate: IHCRemote not yet initialized");
        }
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
            if( resources != null) {
                Log.v(TAG, "Waiting for value changes");
                return appContext.getIHCConnectionManager().waitForResourceValueChanges(resources);
            } else {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean refreshListView) {
            appContext.setIsWaitingForValueChanges(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(waitForResourceValuesChange);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(waitForResourceValuesChange);
    }

    protected ApplicationContext getApplicationContext(){
        return appContext;
    }
}
