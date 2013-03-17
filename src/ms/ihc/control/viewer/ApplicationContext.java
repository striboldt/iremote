package ms.ihc.control.viewer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ApplicationContext extends Application{
	
	private SoapImpl ihcConnector;
	private IHCHome home;
	private Boolean isAppRestarted;
	private Boolean waitingForValueChanges;
	
	public ApplicationContext()
	{
		this.isAppRestarted = true;
		Log.v("ApplicationContext", "Instantiating app context");
	}

	public void setIhcConnector(SoapImpl ihcConnector) {
		this.ihcConnector = ihcConnector;
	}

	public SoapImpl getIhcConnector() {
		return ihcConnector;
	}

	public void setIHCHome(IHCHome home) {
		this.home = home;
	}

	public IHCHome getIHCHome() {
		return home;
	}
	
	public Boolean getIsAppRestarted()
	{
		return this.isAppRestarted;
	}

	public void setIsAppRestarted(Boolean isAppRestarted)
	{
		this.isAppRestarted = isAppRestarted;
	}
	
	public Boolean getIsWaitingForValueChanges()
	{
		return this.waitingForValueChanges;
	}
	
	public void setIsWaitingForValueChanges(Boolean isWaiting)
	{
		this.waitingForValueChanges = isWaiting;
	}
	
	 public Boolean writeDataFile(String filename)
	 {
		 Boolean success = true;
			FileOutputStream f_out = null;
			ObjectOutputStream obj_out = null;
			
			try{
				// Use a FileOutputStream to send data to a file
				// called myobject.data.
				f_out = openFileOutput(filename, Context.MODE_PRIVATE); 
				// Use an ObjectOutputStream to send object data to the
				// FileOutputStream for writing to disk.
				obj_out = new ObjectOutputStream (f_out);
	
				// Pass our object to the ObjectOutputStream's
				// writeObject() method to cause it to be written out
				// to disk.
				obj_out.writeObject(home);
			}
			catch (Exception e)
			{
				success = false;
				e.printStackTrace();
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_LONG;
				Toast toast;			
				toast = Toast.makeText(context, "Error persisting project: " + e.getMessage(), duration);
				toast.show();
			}
			finally
			{
				try
				{
					if(f_out != null)
						f_out.close();
					if(obj_out != null)
						obj_out.close();
				}
				catch(IOException io)
				{
					io.printStackTrace();
				}
			}
			return success;
	 }
	 
	 public Boolean dataFileExists(String filename)
	 {
		 Boolean exists = true;
		 FileInputStream f_in = null;
		 ObjectInputStream obj_in = null;
			try
			{
				// Read from disk using FileInputStream.
				f_in = openFileInput(filename);

				// Read object using ObjectInputStream.
				obj_in = new ObjectInputStream (f_in);

				// Read an object.
				home = (IHCHome) obj_in.readObject();
				
			}
			catch(Exception e)
			{
				exists = false;
				//e.printStackTrace();
			}
			finally
			{
				try
				{
					if(obj_in != null)
						obj_in.close();
					if(f_in != null)
						f_in.close();
				}
				catch(IOException io)
				{
					io.printStackTrace();
				}
			}
			return exists;
		 
	 }
	

}