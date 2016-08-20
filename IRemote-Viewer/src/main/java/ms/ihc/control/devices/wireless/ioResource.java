package ms.ihc.control.devices.wireless;

import ms.ihc.control.viewer.ConnectionManager;
import ms.ihc.control.viewer.R;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;

@SuppressLint("NewApi")
public abstract class ioResource implements OnLongClickListener, java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1240051520566963622L;
	private boolean longClickDetected = false;
	public abstract void inputClicked(int inputID, ConnectionManager ihcCtrl);
	public abstract void setInputClicked(boolean OnOff, int inputID, ConnectionManager ihcCtrl);
	public boolean isFavourite = false;
	
	
	public void onClick(View v) {
		Log.v("OnClick", "button clicked");
		
		Button btn = (Button)v;
		int button=0;
		switch(btn.getId())
		{
			case R.id.Button01:
				button = 1;
				break;
			case R.id.Button02:
				button = 2;
				break;
			case R.id.Button03:
				button = 3;
				break;
			case R.id.Button04:
				button = 4;
				break;
			case R.id.Button05:
				button = 5;
				break;
			case R.id.Button06:
				button = 6;
				break;
			case R.id.Button07:
				button = 7;
				break;
			case R.id.Button08:
				button = 8;
				break;
			case R.id.Button09:
				button = 9;
				break;
			case R.id.Button10:
				button = 10;
				break;
			case R.id.Button11:
				button = 11;
				break;
			case R.id.Button12:
				button = 12;
				break;
			default:
				break;
					
		}	
		
		ConnectionManager ihcCtrl = (ConnectionManager)btn.getTag();
		
		if(longClickDetected)
		{
			longClickDetected = false;
			ihcCtrl.isInTouchMode = false;
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
				new ClickBgEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, button,ihcCtrl,false);
			}
			else
				new ClickBgEvent().execute(button,ihcCtrl,false);
			Log.v("OnClick", "longClickDected - setting value to false");
		}
		else
		{
			Log.v("OnClick", "Normal click");
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
				new ClickBgEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, button,ihcCtrl);
			}
			else
				new ClickBgEvent().execute(button,ihcCtrl);
		}

	}
	
	public boolean onLongClick(View v) {
		this.longClickDetected = true;
		Log.v("LongClick", "Long click event");
		
		Button btn = (Button)v;
		int button=0;
		switch(btn.getId())
		{
		case R.id.Button01:
			button = 1;
			break;
		case R.id.Button02:
			button = 2;
			break;
		case R.id.Button03:
			button = 3;
			break;
		case R.id.Button04:
			button = 4;
			break;
		case R.id.Button05:
			button = 5;
			break;
		case R.id.Button06:
			button = 6;
			break;
		case R.id.Button07:
			button = 7;
			break;
		case R.id.Button08:
			button = 8;
			break;
		case R.id.Button09:
			button = 9;
			break;
		case R.id.Button10:
			button = 10;
			break;
		case R.id.Button11:
			button = 11;
			break;
		case R.id.Button12:
			button = 12;
			break;
					
		}	
		
		ConnectionManager ihcCtrl = (ConnectionManager)btn.getTag();
		ihcCtrl.isInTouchMode = true;
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
			new ClickBgEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,button,ihcCtrl,true);
		}
		else
			new ClickBgEvent().execute(button,ihcCtrl,true);
		
		return false;
	}
	
	
	private class ClickBgEvent extends AsyncTask<Object, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Object... params) {
			Log.v("ioResource.ClickBgEvent", "Running AsyncTask");
			int btnNumber = (Integer)params[0];
			ConnectionManager ihcCtrl = (ConnectionManager) params[1];
			if(params.length > 2)
			{	
				setInputClicked((Boolean)params[2], btnNumber, ihcCtrl);
			}
			else
			{
				Log.v("ioResource.ClickBgEvent", "Before inputClicked");
				inputClicked(btnNumber, ihcCtrl);
			}
			return true;
		}

		protected void onPostExecute(Boolean result) {
		}
	}
	
	public class SetDimmerEvent extends AsyncTask<Object, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Object... params) {
			ConnectionManager ihcCtrl = (ConnectionManager) params[0];
			setDimmerValue(ihcCtrl);
			return true;
		}

		protected void onPostExecute(Boolean result) {
		}
	}

	public abstract void setDimmerValue(ConnectionManager ihcCtrl);

	

		
	
}
