package ms.ihc.control.viewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.os.Environment;
import android.util.Log;

public class CustomExceptionHandler implements UncaughtExceptionHandler {
	private UncaughtExceptionHandler defaultUEH;
	private File file;
	private String uncaughtFile = "IRemote.stacktrace";
	private FileWriter bos;
	
	/*
	 *   
	 * if any of the parameters is null, the respective
     * functionality * will not be used
     */

	public CustomExceptionHandler(String filename) {
		File root = Environment.getExternalStorageDirectory();
		if(filename == null)
		{
			this.file = new File(root, this.uncaughtFile);
			this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		}
		else
		{
			this.file = new File(root, filename);
		}
		
		try
		{
			bos = new FileWriter(this.file);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void uncaughtException(Thread t, Throwable e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
	    writeToFile(stacktrace);

		defaultUEH.uncaughtException(t, e);
	}

	public void writeToFile(String stacktrace) {
		try {
			if(!file.exists()) {
				Log.w("Exception", "File Doesn't Exists!"); 
				file.createNewFile();              
			} 
			bos.write(stacktrace + "\n ");
			bos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
