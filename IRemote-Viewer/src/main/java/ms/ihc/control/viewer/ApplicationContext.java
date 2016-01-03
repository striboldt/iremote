package ms.ihc.control.viewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ms.ihc.control.Utils.SnackbarHelper;

public class ApplicationContext extends Application {

    private ConnectionManager connectionManager;
    private IHCHome home;
    private Boolean waitingForValueChanges = false;
    private static final String ihcFilename = "iremote.data";

    public ApplicationContext() {
        Log.v("ApplicationContext", "Instantiating app context");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

    public ConnectionManager getIHCConnectionManager() {
        if (connectionManager == null) {
            connectionManager = new ConnectionManager(this);
        }
        return connectionManager;
    }

    public void setIHCHome(IHCHome home) {
        this.home = home;
        writeDataFile(ihcFilename);
    }

    @Nullable
    public IHCHome getIHCHome() {
        return home;
    }

    public Boolean getIsWaitingForValueChanges() {
        return this.waitingForValueChanges;
    }

    public void setIsWaitingForValueChanges(Boolean isWaiting) {
        this.waitingForValueChanges = isWaiting;
    }

    public void deleteDataFile(){
        File ihcFile = new File(ihcFilename);
        ihcFile.delete();
    }

    public Boolean writeDataFile(String filename) {
        Boolean success = true;
        FileOutputStream f_out = null;
        ObjectOutputStream obj_out = null;

        try {
            // Use a FileOutputStream to send data to a file
            // called myobject.data.
            f_out = openFileOutput(filename, Context.MODE_PRIVATE);
            // Use an ObjectOutputStream to send object data to the
            // FileOutputStream for writing to disk.
            obj_out = new ObjectOutputStream(f_out);

            // Pass our object to the ObjectOutputStream's
            // writeObject() method to cause it to be written out
            // to disk.
            obj_out.writeObject(home);
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_LONG;
            Toast toast;
            toast = Toast.makeText(context, "Error persisting project: " + e.getMessage(), duration);
            toast.show();
        } finally {
            try {
                if (f_out != null)
                    f_out.close();
                if (obj_out != null)
                    obj_out.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return success;
    }

    public Boolean dataFileExists() {
        Boolean exists = true;
        FileInputStream f_in = null;
        ObjectInputStream obj_in = null;
        try {
            // Read from disk using FileInputStream.
            f_in = openFileInput(ihcFilename);

            // Read object using ObjectInputStream.
            obj_in = new ObjectInputStream(f_in);

            // Read an object.
            home = (IHCHome) obj_in.readObject();

        } catch (Exception e) {
            exists = false;
            //e.printStackTrace();
        } finally {
            try {
                if (obj_in != null)
                    obj_in.close();
                if (f_in != null)
                    f_in.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return exists;

    }

}
