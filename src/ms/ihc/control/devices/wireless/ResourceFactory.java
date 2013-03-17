package ms.ihc.control.devices.wireless;

import android.util.Log;
import ms.ihc.control.viewer.SoapImpl;

public class ResourceFactory {
	
	public static IHCResource createResource(String deviceId, SoapImpl soapImpl) throws IllegalAccessException, InstantiationException, ClassNotFoundException, Exception
	{ 
		IHCResource resource = null;
		try{
			resource =(IHCResource) Class.forName(IHCResource.resourceTypes.get(Integer.parseInt(deviceId,16)).getName()).newInstance();
			resource.setDeviceId(Integer.parseInt(deviceId,16));
		}
		catch(Exception e)
		{
			Log.v("ResourceFactory", "Resource id: " + deviceId + " not found");
			throw new Exception("Resource id: " + deviceId + " not found");
		}
		return resource;
	}

}
