package ms.ihc.control.viewer;

import java.io.BufferedInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.devices.wireless.ResourceFactory;
import ms.ihc.control.gzip.GZIPInputStream;

import org.ksoap2.*;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.android.vending.licensing.util.Base64;


public class SoapImpl{

	private String URI;
	private static final String NAMESPACE = "utcs";
	private String SESSIONID = "";
	private String login;
	private String password;
	private String ctrlIp;
	private Boolean wanOnly;
	private String msg;
	public Boolean isInTouchMode = false;
	private Boolean isConnected = false;
	private ControllerConnection controllerConnection;
	
	// Define our custom Listener interface
	public interface ControllerConnection {
		public abstract void onConnectionAccepted();
		public abstract void onRuntimeVaulesChanged();
	}

	// Allows the user to set an Listener and react to the event
	public void setControlerConnectionListener(ControllerConnection listener) {
		controllerConnection = listener;
	}
	
	public String getLoginMessage()
	{
		return msg;
	}

	
	public Boolean Authenticate(String username, String Password, String ip, Boolean WanOnly) {
		
		this.login = username;
		this.password = Password;
		this.ctrlIp = ip;
		this.wanOnly = WanOnly;
		
		String SOAP_ACTION = "authenticate";
		String METHOD_NAME = "authenticate1";
		msg = "Unknown error";
		boolean loginWasSuccessful = false;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("username", username);
		request.addProperty("password", Password);
		if(WanOnly)
			request.addProperty("application", "sceneview");
		else
			request.addProperty("application", "treeview");

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);

		this.URI = "https://%IP%/ws/";
		this.URI = this.URI.replace("%IP%", ip);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ "AuthenticationService");

		try {
			
			androidHttpTransport.call(SOAP_ACTION, envelope);
			this.SESSIONID = androidHttpTransport.sessionCookie;
			SoapObject soapResponse = (SoapObject) envelope.bodyIn;
			loginWasSuccessful = (Boolean) soapResponse.getProperty("loginWasSuccessful");
			if(!loginWasSuccessful)
			{
				if((Boolean) soapResponse.getProperty("loginFailedDueToConnectionRestrictions"))
					msg = "loginFailedDueToConnectionRestrictions";
				else if ((Boolean) soapResponse.getProperty("loginFailedDueToInsufficientUserRights"))
					msg = "loginFailedDueToInsufficientUserRights";
				else if ((Boolean) soapResponse.getProperty("loginFailedDueToAccountInvalid"))
					msg = "loginFailedDueToAccountInvalid";
			}
			isConnected = true;

		} 
		catch (SocketTimeoutException socketTimeout)
		{
			msg = "SocketTimeout";
		}
		catch (Exception e) {
			 msg = e.getMessage();
		}
		finally
		{
			androidHttpTransport.reset();
		}

		return loginWasSuccessful;
	}
	
	public Boolean reAuthenticate()
	{
		 if(this.Authenticate(this.login, this.password, this.ctrlIp, this.wanOnly))
		 {
			 controllerConnection.onConnectionAccepted();
			 return true;
		 }
		 else
			 return false;
	}

	public Boolean ping() {
		String SOAP_ACTION = "ping";
		String SERVICE = "AuthenticationService";
		Boolean resultsRequestSOAP = false;

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			resultsRequestSOAP = (Boolean) envelope.bodyIn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			androidHttpTransport.reset();
		}

		return resultsRequestSOAP;

	}
	
	public String getState() {
		String SOAP_ACTION = "getState";
		String SERVICE = "ControllerService";
		String response = "";

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
			response = resultsRequestSOAP.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			androidHttpTransport.reset();
		}

		return response;

	}
	
	public Boolean isIHCProjectAvailable(String username, String Password, String ip) {
		String SOAP_ACTION = "isIHCProjectAvailable";
		String SERVICE = "ControllerService";
		Boolean response = false;

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			response = (Boolean) envelope.bodyIn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			androidHttpTransport.reset();
		}

		return response;

	}

	
	private int getProjectNumberOfSegments()
	{
		String SOAP_ACTION = "getIHCProjectNumberOfSegments";
		String SERVICE = "ControllerService";
		int numberOfSegements = 0;

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI + SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			numberOfSegements = (Integer) envelope.bodyIn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			androidHttpTransport.reset();
		}
		return numberOfSegements;
	}
	
	private Map<String, Integer> getProjectInfo()
	{
		String SOAP_ACTION = "getProjectInfo";
		String SERVICE = "ControllerService";
		HashMap<String, Integer> projectInfo = new HashMap<String, Integer>();

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			// Retrieve body in response
			SoapObject soapResponse = (SoapObject) envelope.bodyIn;
			projectInfo.put("projectMajorRevision", (Integer)soapResponse.getProperty("projectMajorRevision"));
			projectInfo.put("projectMinorRevision", (Integer)soapResponse.getProperty("projectMinorRevision"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			androidHttpTransport.reset();
		}

		return projectInfo;
	}
	
	private int getSegmentSize()
	{
		String SOAP_ACTION = "getIHCProjectSegmentationSize";
		String SERVICE = "ControllerService";
		int segmentSize = 0;

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			// Retrieve body in response
			segmentSize =(Integer) envelope.bodyIn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			androidHttpTransport.reset();
		}
		
		return segmentSize;
	}
	
	
	private ByteArrayOutputStream getProjectSegment(int segments, int arraySize, int majorRevision, int minorRevision )
	{
		ByteArrayOutputStream decodedStream = new ByteArrayOutputStream(arraySize);
		
		String SOAP_ACTION = "getIHCProjectSegment";
		String SERVICE = "ControllerService";
		HttpTransportSE androidHttpTransport = null;
		
		for(int i = 0; i<segments; i++)
		{
			SoapObject request = new SoapObject(NAMESPACE, "");
			request.addProperty("getIHCProjectSegment1", i);
			request.addProperty("getIHCProjectSegment2", majorRevision);
			request.addProperty("getIHCProjectSegment3", minorRevision);
			
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setAddAdornments(false);
			envelope.setOutputSoapObject(request);
			
			if(androidHttpTransport == null)
			{
				androidHttpTransport = new HttpTransportSE(this.URI+ SERVICE);
				androidHttpTransport.debug = false;
				androidHttpTransport.sessionCookie = this.SESSIONID;
			}
	
			try {
				androidHttpTransport.call(SOAP_ACTION, envelope);
				//String requestDump = androidHttpTransport.requestDump;
				//String responseDumo = androidHttpTransport.responseDump;
				SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
			
				// Get Base64Binary data and decode to byte array 
				// getData
				SoapPrimitive base64 = (SoapPrimitive) resultsRequestSOAP.getProperty("data");
				
				decodedStream.write(Base64.decode(base64.toString()));
				
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		if(androidHttpTransport != null)
			androidHttpTransport.reset();
		
		return decodedStream;
	}
	
	public IHCHome getIHCProject(boolean isSimulationMode, InputStream instream) {
		IHCHome home = new IHCHome();
		if(isSimulationMode)
		{
			try {
				home = parse(instream, "group", home);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else
		{

			int segments = getProjectNumberOfSegments();	
			int segmentSize = getSegmentSize();
			int arraySize = segments * segmentSize;
			ByteArrayOutputStream decoded = new ByteArrayOutputStream(arraySize);
			Map<String, Integer> projectInfo = getProjectInfo();

			int majorRevision = projectInfo.get("projectMajorRevision");
			int minorRevision = projectInfo.get("projectMinorRevision");

			decoded = getProjectSegment(segments, arraySize, majorRevision, minorRevision);

			try {
				// Convert byte array into stream
				InputStream is = new ByteArrayInputStream(decoded.toByteArray());
				/*InputStream gzipStream = new GZIPInputStream(is);
				Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
				BufferedReader buffered = new BufferedReader(decoder);*/
				BufferedInputStream buffered = new BufferedInputStream(is);
				GZIPInputStream zip = new GZIPInputStream(buffered);
				home = parse(zip, "group", home);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(XmlPullParserException e)
			{
				e.printStackTrace();
			}
		}
		
		return home;

	}
	
	public String setResourceBooleanValue(SoapObject request, String type, KvmSerializable valueType) {
		Log.v("setResourceBooleanValue", Long.toString(System.currentTimeMillis()));
		String SOAP_ACTION = "setResourceValue";
		String SERVICE = "ResourceInteractionService";
		Boolean response;
		if(this.isConnected)
		{
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	
			envelope.addMapping("d", type, valueType.getClass());
			
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
					+ SERVICE);
			androidHttpTransport.sessionCookie = this.SESSIONID;
			androidHttpTransport.debug = false;
			
			
			try {
				// Send Soap request
				Log.v("setResourceBooleanValue", "Setting value - START");
				androidHttpTransport.call(SOAP_ACTION, envelope);
				Log.v("setResourceBooleanValue", "Setting value - END");
			/*	String requestDump = androidHttpTransport.requestDump;
				String responseDumo = androidHttpTransport.responseDump;*/
				// Retrieve body in response
				response = (Boolean) envelope.bodyIn;
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				androidHttpTransport.reset();
			}
		}

		return "OK";

	}
	
	public String setResourceBooleanValues(SoapObject request, String type, KvmSerializable valueType)
	{
		String SOAP_ACTION = "setResourceValues";
		String SERVICE = "ResourceInteractionService";
		Boolean response;

		if(this.isConnected)
		{
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	
			envelope.addMapping("d", type, valueType.getClass());
			
			envelope.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
					+ SERVICE);
			androidHttpTransport.sessionCookie = this.SESSIONID;
			androidHttpTransport.debug = false;
			
			
			try {
				// Send Soap request
				androidHttpTransport.call(SOAP_ACTION, envelope);
				//String requestDump = androidHttpTransport.requestDump;
				//String responseDumo = androidHttpTransport.responseDump;
				// Retrieve body in response
				response = (Boolean) envelope.bodyIn;
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				androidHttpTransport.reset();
			}
		}

		return "OK";
	}
	
	public SoapObject getResourceValue(int resourceID)
	{
		String SOAP_ACTION = "getRuntimeValues";
		String SERVICE = "ResourceInteractionService";
		String METHOD_NAME = "getRuntimeValues1";
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("arrayItem", resourceID);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			return (SoapObject) envelope.bodyIn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			androidHttpTransport.reset();
		}

		return null;
	}
	
	public boolean waitForResourceValueChanges(HashMap<Integer, IHCResource> resourceIds)
	{
		String SOAP_ACTION = "waitForResourceValueChanges";
		String SERVICE = "ResourceInteractionService";
		
		SoapObject request = new SoapObject(NAMESPACE, "");
		request.addProperty("waitForResourceValueChanges1", 10);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;
		androidHttpTransport.debug = false;
		Log.v("waitForResourceValueChanges", "START");

		if(this.isConnected)
		{
			try {
				androidHttpTransport.call(SOAP_ACTION, envelope);
				if(envelope.bodyIn.getClass().getName().equals(SoapFault.class.getName()))
				{
					this.isConnected = false;
					if(reAuthenticate())
					{
						this.isConnected = true;
					}
					Log.v("waitForResourceValueChanges", "reauthenticate");
					return false;
				}
				else
				{
					SoapObject soapResponse = (SoapObject) envelope.bodyIn;
					int properties = soapResponse.getPropertyCount();
		
					for(int i=0; i<properties; i++)
					{
						Object val = null;
						SoapObject obj = (SoapObject)soapResponse.getProperty(i);
						if(obj == null)
						{
							Log.v("waitForResourceValueChanges", "obj is null");
							return false;
						}
						int resourceID = (Integer)obj.getProperty("resourceID");
						SoapObject value = (SoapObject) obj.getProperty("value");
						String type = (String) value.getAttribute("type");
						if(type.contains("WSFloatingPointValue"))
							val = value.getProperty("floatingPointValue");
						else
							val = value.getProperty(0);
						resourceIds.get(resourceID).setResourceValue(resourceID, val);
						Log.v("SetResourceValue", String.valueOf(resourceID));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				androidHttpTransport.reset();
			}
		}
		else
		{
			Log.v("waitForResourceValueChanges", "No connection");
			return false;
		}
		
		// Inform eventlistners about RuntimeValues has changed.
		controllerConnection.onRuntimeVaulesChanged();
		return true;
	}
	
	
	public Boolean enableRuntimeValueNotifications(HashMap<Integer, IHCResource> resourceIds)
	{
		String SOAP_ACTION = "enableRuntimeValueNotifications";
		String SERVICE = "ResourceInteractionService";
		String METHOD_NAME = "enableRuntimeValueNotifications1";
		Boolean bSuccess = false;
		
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		
		for (Iterator<Integer> iterator = resourceIds.keySet().iterator(); iterator.hasNext();) {
			int resourceId = (Integer) iterator.next();	
				request.addProperty("arrayItem",resourceId);
		}


		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(this.URI
				+ SERVICE);
		androidHttpTransport.sessionCookie = this.SESSIONID;
		androidHttpTransport.debug = false;

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			bSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			androidHttpTransport.reset();
		}

		return bSuccess;
	}

	public IHCHome parse(InputStream inputStream, String ITEM, IHCHome home) throws XmlPullParserException, IOException {
		IHCLocation location = null;
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
		factory.setNamespaceAware(true);       
		XmlPullParser xpp = factory.newPullParser();    
		xpp.setInput(inputStream,null); 
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) 
		{
			 if(eventType == XmlPullParser.START_TAG) 
			 {
				 if(xpp.getName().equals("group"))
				 {
					 location = new IHCLocation();
					 home.locations.add(location);
					 location.Name = xpp.getAttributeValue(null, "name");
					 System.out.println(location.Name);
				 }
	     		 // Wireless or Standard IHC resources
				 else if(xpp.getName().equals("product_airlink"))
				 {
					 try
					 {
						 IHCResource ihcResource = ResourceFactory.createResource(xpp.getAttributeValue(null,"product_identifier").replace("_0x", ""), this);	
						 ihcResource.setName(xpp.getAttributeValue(null,"name"));
						 if(xpp.getAttributeValue(null,"position") != null)
							 ihcResource.setPosition(xpp.getAttributeValue(null,"position"));
						 else
							 ihcResource.setPosition("");
							
						 ihcResource.setState(false);	
						 ihcResource.setupResources(xpp, xpp.getName());
						 location.resources.add(ihcResource);
					 }
					 catch(Exception ihc)
					 {
						//ihc.printStackTrace();
					 }	
				 }
				 else if(xpp.getName().equals("product_dataline"))
				 {
					 try
					 {
						 IHCResource ihcResource = ResourceFactory.createResource(xpp.getAttributeValue(null,"product_identifier").replace("_0x", ""), this);	
						 ihcResource.setName(xpp.getAttributeValue(null,"name"));
						 if(xpp.getAttributeValue(null,"position") != null)
							 ihcResource.setPosition(xpp.getAttributeValue(null,"position"));
						 else
							 ihcResource.setPosition("");
							
						 ihcResource.setState(false);	
						 ihcResource.setupResources(xpp, xpp.getName());
						 location.resources.add(ihcResource);
					 }
					 catch(Exception ihc)
					 {
						//ihc.printStackTrace();
					 }
						 
				 }
			 }
			 try
			 {
				 eventType = xpp.next(); 
			 }
			 catch(XmlPullParserException pullException )
			 {
				 pullException.printStackTrace();
			 }
			 
		 }

		return home;
	}



}
