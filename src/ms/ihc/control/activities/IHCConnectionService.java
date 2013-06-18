package ms.ihc.control.activities;

import ms.ihc.control.viewer.ApplicationContext;
import ms.ihc.control.viewer.SoapImpl;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class IHCConnectionService extends Service {

	// This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
	private SoapImpl soapImp;


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	IHCConnectionService getService() {
            return IHCConnectionService.this;
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	  
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("IHCConnectionService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		soapImp = new SoapImpl();
	}


}
