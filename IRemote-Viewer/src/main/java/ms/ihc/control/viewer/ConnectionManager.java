package ms.ihc.control.viewer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import ms.ihc.control.ksoap2.serialization.*;
import ms.ihc.control.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;

import com.crashlytics.android.Crashlytics;
import com.google.android.vending.licensing.util.Base64;
import javax.net.ssl.SSLHandshakeException;
import ms.ihc.control.devices.wireless.IHCResource;
import ms.ihc.control.devices.ResourceFactory;


public class ConnectionManager {

    private static final String TAG = ConnectionManager.class.getCanonicalName();

    public enum IHC_EVENTS {
        RESOURCE_VALUE_CHANGED("ms.ihc.control.viewer.resource_value_changed"),
        CONNECTED("ms.ihc.control.viewer.connected"),
        DISCONNECTED("ms.ihc.control.viewer.disconnected"),
        LOADING_PROJECT("ms.ihc.control.viewer.loading_project"),
        PROJECT_LOADED("ms.ihc.control.viewer.project_loaded"),
        CONNECTION_FAILED("ms.ihc.control.viewer.general_message"),
        NETWORK_CHANGE("ms.ihc.control.viewer.network_change"),
        RECONNECTED("ms.ihc.control.viewer.reconnected"),
        RECONNECTION_FAILED("ms.ihc.control.viewer.reconnection_failed");

        private String pretty;

        private IHC_EVENTS(String pretty) {
            this.pretty = pretty;
        }

        @Override
        public String toString() {
            // you can localise this string somehow here
            return pretty;
        }
    }

    public enum IHCMESSAGE {
        CONNECTION_RESTRICTIONS("loginFailedDueToConnectionRestrictions"),
        INSUFFICIENT_RIGHTS("loginFailedDueToInsufficientUserRights"),
        SOCKET_TIMEOUT("socketTimeout"),
        INVALID_ACCOUNT("loginFailedDueToAccountInvalid"),
        LOAD_PROJECT_FAILED("failToLoadProject"),
        CERTIFICATE_NOT_TRUSTED("certNotTrusted"),
        INVALID_URI("wrongURI"), NO_ROUTE_TO_HOST("no_route_to_host");

        private String pretty;

        private IHCMESSAGE(String pretty) {
            this.pretty = pretty;
        }

        @Override
        public String toString() {
            // you can localise this string somehow here
            return pretty;
        }
    }

    private static final String NAMESPACE = "utcs";
    public Boolean isInTouchMode = false;
    private java.net.URI URI;
    private String login;
    private String password;
    private Boolean wan;
    private Boolean isConnected = false;
    private ApplicationContext context;
    private KeyStore trustedKeystore = null;
    private boolean sslDebug = false;
    private HttpTransportSE androidHttpTransport = null;
    private boolean isReconnecting;


    public ConnectionManager(ApplicationContext applicationContext) {
        this.context = applicationContext;

        // No longer nessesary
        // Load LK certificate into keystore from resources
 /*       try {
            // loading CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = context.getResources().openRawResource(R.raw.lk);
            InputStream cert2 = context.getResources().openRawResource(R.raw.lk2);
            Certificate ca, ca2;
            try {
                ca = cf.generateCertificate(cert);
                ca2 = cf.generateCertificate(cert2);
                Log.i(TAG, "ca=" + ((X509Certificate) ca).getSubjectDN());
                Log.i(TAG, "ca2=" + ((X509Certificate) ca2).getSubjectDN());
            } finally {
                cert.close();
                cert2.close();
            }

            // creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            trustedKeystore = KeyStore.getInstance(keyStoreType);
            trustedKeystore.load(null, null);
            trustedKeystore.setCertificateEntry("ca", ca);
            trustedKeystore.setCertificateEntry("ca2", ca2);
        } catch (CertificateException e) {
            Crashlytics.getInstance().core.logException(e);
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Crashlytics.getInstance().core.logException(e);
            e.printStackTrace();
        } catch (IOException e) {
            Crashlytics.getInstance().core.logException(e);
            e.printStackTrace();
        } catch (KeyStoreException e) {
            Crashlytics.getInstance().core.logException(e);
            e.printStackTrace();
        }*/
    }

    private class AuthenticationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

          /*  try {
                InetAddress address = InetAddress.getByName(ip);
                URI = new URI(String.format("https://%s/ws/", address.getHostAddress()));
            } catch (Exception e) {
                sendBroadcast(IHC_EVENTS.CONNECTION_FAILED,  handleException(e));
            }*/

            String SOAP_ACTION = "authenticate";
            String SERVICE = "AuthenticationService";

            String METHOD_NAME = "authenticate1";
            IHCMESSAGE ihcmessage = null;

            boolean loginWasSuccessful = false;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("username", login);
            request.addProperty("password", password);

            if (wan)
                request.addProperty("application", "sceneview");
            else
                request.addProperty("application", "treeview");

            Log.i(TAG, "SoapSerializationEnvelope");
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(ms.ihc.control.ksoap2.serialization.SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);

            if (androidHttpTransport == null) {
                androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
            } else {
                androidHttpTransport.setUrl(URI + SERVICE);
            }
            androidHttpTransport.debug = sslDebug;
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject soapResponse = (SoapObject) envelope.bodyIn;
                loginWasSuccessful = (Boolean) soapResponse.getProperty("loginWasSuccessful");
                if (!loginWasSuccessful) {
                    if ((Boolean) soapResponse.getProperty(IHCMESSAGE.CONNECTION_RESTRICTIONS.toString()))
                        ihcmessage = IHCMESSAGE.CONNECTION_RESTRICTIONS;
                    else if ((Boolean) soapResponse.getProperty(IHCMESSAGE.INSUFFICIENT_RIGHTS.toString()))
                        ihcmessage = IHCMESSAGE.INSUFFICIENT_RIGHTS;
                    else if ((Boolean) soapResponse.getProperty(IHCMESSAGE.INVALID_ACCOUNT.toString()))
                        ihcmessage = IHCMESSAGE.INVALID_ACCOUNT;
                }
                isConnected = true;
                Log.i(TAG, "isConnected");

            } catch (Exception e) {
                ihcmessage = handleException(e);
            }

            if (loginWasSuccessful)
                if(isReconnecting){
                    sendBroadcast(IHC_EVENTS.RECONNECTED, null);
                } else {
                    sendBroadcast(IHC_EVENTS.CONNECTED, null);
                }
            else {
                if(isReconnecting){
                    sendBroadcast(IHC_EVENTS.RECONNECTION_FAILED, null);
                } else {
                    sendBroadcast(IHC_EVENTS.CONNECTION_FAILED, ihcmessage);
                }
            }
            return null;
        }
    }

    private IHCMESSAGE handleException(Exception e) {
        IHCMESSAGE ihcmessage = null;
        Crashlytics.getInstance().core.logException(e);
        Log.w(TAG, "handleException: ", e);
        if (e instanceof SocketTimeoutException) {
            ihcmessage = IHCMESSAGE.SOCKET_TIMEOUT;
        } else if (e instanceof SSLHandshakeException) {
            ihcmessage = IHCMESSAGE.CERTIFICATE_NOT_TRUSTED;
        } else if (e instanceof URISyntaxException || e instanceof UnknownHostException) {
            ihcmessage = IHCMESSAGE.INVALID_URI;
        } else if (e instanceof ConnectException) {
            ihcmessage = IHCMESSAGE.NO_ROUTE_TO_HOST;
            isConnected = false;
        } else {
            Log.e(TAG, "handleException: Unhandled Exception", e);
        }
        return ihcmessage;
    }

    public void connect(final String username, String pwd, final String IP, final boolean WAN, boolean reconnect) {
        login = username;
        password = pwd;
        wan = WAN;
        isReconnecting = reconnect;
        try {
            URI = new URI(String.format("https://%s/ws/", IP));
            new AuthenticationTask().execute();
        } catch (Exception e) {
            sendBroadcast(IHC_EVENTS.CONNECTION_FAILED,  handleException(e));
        }
    }

    // TODO: Setup algorithm for connection retry and timeout. And notify upstream Activities in case of failure
    public void reconnect(final String ip, final Boolean WAN) {
        this.connect(login, password, ip, WAN, true);
    }

    public Boolean ping() {
        String SOAP_ACTION = "ping";
        String SERVICE = "AuthenticationService";
        Boolean resultsRequestSOAP = false;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                ms.ihc.control.ksoap2.serialization.SoapEnvelope.VER11);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            resultsRequestSOAP = (Boolean) envelope.bodyIn;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return resultsRequestSOAP;

    }

    public String getState() {
        String SOAP_ACTION = "getState";
        String SERVICE = "ControllerService";
        String response = "";

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(ms.ihc.control.ksoap2.serialization.SoapEnvelope.VER11);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
            response = resultsRequestSOAP.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return response;

    }

    public Boolean isIHCProjectAvailable(String username, String Password, String ip) {
        final String SOAP_ACTION = "isIHCProjectAvailable";
        final String SERVICE = "ControllerService";

        Boolean response = false;

        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(ms.ihc.control.ksoap2.serialization.SoapEnvelope.VER11);

        final HttpTransportSE androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            response = (Boolean) envelope.bodyIn;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return response;

    }

    private class getProjectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            IHCHome home = new IHCHome();
            int segments = getProjectNumberOfSegments();

            int segmentSize = getSegmentSize();

            int arraySize = segments * segmentSize;
            Map<String, Integer> projectInfo = getProjectInfo();

            int majorRevision = projectInfo.get("projectMajorRevision");
            int minorRevision = projectInfo.get("projectMinorRevision");

            ByteArrayOutputStream decoded = getProjectSegment(segments, arraySize, majorRevision, minorRevision);

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
                Log.e(TAG, e.getMessage());
                Crashlytics.getInstance().core.logException(e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "XmlPullParserException: " + e.getMessage());
                Crashlytics.getInstance().core.logException(e);
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage());
                Crashlytics.getInstance().core.logException(e);
            }

            ConnectionManager.this.context.setIHCHome(home);

            if (home != null)
                sendBroadcast(IHC_EVENTS.PROJECT_LOADED, null);
            else
                sendBroadcast(IHC_EVENTS.DISCONNECTED, IHCMESSAGE.LOAD_PROJECT_FAILED);
            return null;
        }
    }

    private int getProjectNumberOfSegments() {
        String SOAP_ACTION = "getIHCProjectNumberOfSegments";
        String SERVICE = "ControllerService";
        int numberOfSegements = 0;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(ms.ihc.control.ksoap2.serialization.SoapEnvelope.VER11);
        if (androidHttpTransport != null) {
            androidHttpTransport.setUrl(URI + SERVICE);
        } else {
            androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
        }

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            if (envelope.bodyIn instanceof ms.ihc.control.ksoap2.serialization.SoapFault) {
                ms.ihc.control.ksoap2.serialization.SoapFault soapFault = (ms.ihc.control.ksoap2.serialization.SoapFault) envelope.bodyIn;
                Log.e(TAG, soapFault.faultstring);
            } else {
                numberOfSegements = (Integer) envelope.bodyIn;
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to get Number of segments in project");
            handleException(e);
        }

        return numberOfSegements;
    }

    private Map<String, Integer> getProjectInfo() {
        String SOAP_ACTION = "getProjectInfo";
        String SERVICE = "ControllerService";
        HashMap<String, Integer> projectInfo = new HashMap<>();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(ms.ihc.control.ksoap2.serialization.SoapEnvelope.VER11);

        if (androidHttpTransport != null) {
            androidHttpTransport.setUrl(URI + SERVICE);
        } else {
            androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
        }

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Retrieve body in response
            SoapObject soapResponse = (SoapObject) envelope.bodyIn;
            projectInfo.put("projectMajorRevision", (Integer) soapResponse.getProperty("projectMajorRevision"));
            projectInfo.put("projectMinorRevision", (Integer) soapResponse.getProperty("projectMinorRevision"));
            Crashlytics.getInstance().core.setUserIdentifier((String) soapResponse.getProperty("customerName"));
            Log.i(TAG, "getProjectInfo: customer: " + soapResponse.getProperty("customerName"));
        } catch (Exception e) {
            handleException(e);
        }

        return projectInfo;
    }

    private int getSegmentSize() {
        String SOAP_ACTION = "getIHCProjectSegmentationSize";
        String SERVICE = "ControllerService";
        int segmentSize = 0;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        if (androidHttpTransport != null) {
            androidHttpTransport.setUrl(URI + SERVICE);
        } else {
            androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
        }

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Retrieve body in response
            segmentSize = (Integer) envelope.bodyIn;
        } catch (Exception e) {
            handleException(e);
        }

        return segmentSize;
    }

    private ByteArrayOutputStream getProjectSegment(int segments, int arraySize, int majorRevision, int minorRevision) {
        ByteArrayOutputStream decodedStream = new ByteArrayOutputStream(arraySize);

        String SOAP_ACTION = "getIHCProjectSegment";
        String SERVICE = "ControllerService";

        for (int i = 0; i < segments; i++) {
            SoapObject request = new SoapObject(NAMESPACE, "");
            request.addProperty("getIHCProjectSegment1", i);
            request.addProperty("getIHCProjectSegment2", majorRevision);
            request.addProperty("getIHCProjectSegment3", minorRevision);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setAddAdornments(false);
            envelope.setOutputSoapObject(request);

            if (androidHttpTransport != null) {
                androidHttpTransport.setUrl(URI + SERVICE);
            } else {
                androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
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
                Crashlytics.getInstance().core.logException(e);
                Log.e(TAG, e.getMessage());
            }
        }

        if (androidHttpTransport != null)
            androidHttpTransport.reset();

        return decodedStream;
    }

    public void loadIHCProject(boolean isSimulationMode, InputStream instream) {
        IHCHome home = new IHCHome();
        if (isSimulationMode) {
            //TODO Needs to be refactored to new structure
            try {
                home = parse(instream, "group", home);
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
            }

        } else {
            new getProjectTask().execute();
        }
    }

    public String setResourceBooleanValue(SoapObject request, String type, KvmSerializable valueType) {
        Log.v("setResourceBooleanValue", Long.toString(System.currentTimeMillis()));
        String SOAP_ACTION = "setResourceValue";
        String SERVICE = "ResourceInteractionService";
        Boolean response;
        if (isConnected) {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.addMapping("d", type, valueType.getClass());

            envelope.setOutputSoapObject(request);
            if (androidHttpTransport != null) {
                androidHttpTransport.setUrl(URI + SERVICE);
            } else {
                androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
            }

            try {
                // Send Soap request
                Log.v("setResourceBooleanValue", "Setting value - START");
                androidHttpTransport.call(SOAP_ACTION, envelope);
                Log.v("setResourceBooleanValue", "Setting value - END");
                // Retrieve body in response
                response = (Boolean) envelope.bodyIn;

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Crashlytics.getInstance().core.logException(e);
            }
        }

        return "OK";

    }

    public String setResourceBooleanValues(SoapObject request, String type, KvmSerializable valueType) {
        String SOAP_ACTION = "setResourceValues";
        String SERVICE = "ResourceInteractionService";
        Boolean response;

        if (this.isConnected) {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(ms.ihc.control.ksoap2.serialization.SoapEnvelope.VER11);

            envelope.addMapping("d", type, valueType.getClass());

            envelope.setOutputSoapObject(request);
            if (androidHttpTransport != null) {
                androidHttpTransport.setUrl(URI + SERVICE);
            } else {
                androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
            }

            try {
                // Send Soap request
                androidHttpTransport.call(SOAP_ACTION, envelope);
                // Retrieve body in response
                response = (Boolean) envelope.bodyIn;

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Crashlytics.getInstance().core.logException(e);
            }
        }

        return "OK";
    }

    public SoapObject getResourceValue(int resourceID) {
        String SOAP_ACTION = "getRuntimeValues";
        String SERVICE = "ResourceInteractionService";
        String METHOD_NAME = "getRuntimeValues1";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("arrayItem", resourceID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(ms.ihc.control.ksoap2.serialization.SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        if (androidHttpTransport != null) {
            androidHttpTransport.setUrl(URI + SERVICE);
        } else {
            androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
        }


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            return (SoapObject) envelope.bodyIn;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Crashlytics.getInstance().core.logException(e);
        }

        return null;
    }

    public boolean waitForResourceValueChanges(SparseArray<IHCResource> resourceIds) {
        String SOAP_ACTION = "waitForResourceValueChanges";
        String SERVICE = "ResourceInteractionService";

        SoapObject request = new SoapObject(NAMESPACE, "");
        request.addProperty("waitForResourceValueChanges1", 4);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        if (androidHttpTransport != null) {
            androidHttpTransport.setUrl(URI + SERVICE);
        } else {
            androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
        }

        if (isConnected) {
            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                if (envelope.bodyIn instanceof SoapFault) {
                    Log.v(TAG, "waitForResourceValueChanges - reauthenticate");
                    return false;
                } else {
                    SoapObject soapResponse = (SoapObject) envelope.bodyIn;
                    if (soapResponse == null) {
                        return false;
                    }

                    int properties = soapResponse.getPropertyCount();

                    for (int i = 0; i < properties; i++) {
                        Object val;
                        SoapObject obj = (SoapObject) soapResponse.getProperty(i);
                        if (obj == null) {
                            Log.v(TAG, "waitForResourceValueChanges - No resource changes detected");
                            return false;
                        }
                        int resourceID = (Integer) obj.getProperty("resourceID");
                        SoapObject value = (SoapObject) obj.getProperty("value");
                        String type = (String) value.getAttribute("type");

                        if (type.contains("WSFloatingPointValue"))
                            val = value.getProperty("floatingPointValue");
                        else
                            val = value.getProperty(0);

                        resourceIds.get(resourceID).setResourceValue(resourceID, val);
                        Log.v("SetResourceValue", String.valueOf(resourceID));
                    }
                    sendBroadcast(IHC_EVENTS.RESOURCE_VALUE_CHANGED, null);
                }
            } catch (Exception e) {
                IHCMESSAGE ihcmessage = handleException(e);
                if( !(e instanceof InterruptedIOException))
                    sendBroadcast(IHC_EVENTS.CONNECTION_FAILED, ihcmessage);

            }
        } else {
            Log.v(TAG, "waitForResourceValueChanges - No connection");
            return false;
        }

        return true;
    }

    public Boolean enableRuntimeValueNotifications(SparseArray<IHCResource> resourceIds) {
        String SOAP_ACTION = "enableRuntimeValueNotifications";
        String SERVICE = "ResourceInteractionService";
        String METHOD_NAME = "enableRuntimeValueNotifications1";
        Boolean bSuccess = false;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        for (int i = 0; i < resourceIds.size(); i++) {
            request.addProperty("arrayItem", resourceIds.keyAt(i));
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        if (androidHttpTransport != null) {
            androidHttpTransport.setUrl(URI + SERVICE);
        } else {
            androidHttpTransport = new HttpTransportSE(URI + SERVICE, trustedKeystore);
        }

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            bSuccess = true;
        } catch (Exception e) {
            handleException(e);
        }

        return bSuccess;
    }

    public IHCHome parse(InputStream inputStream, String ITEM, IHCHome home) throws XmlPullParserException, IOException {
        IHCLocation location = null;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(inputStream, null);
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("group")) {
                    location = new IHCLocation();
                    home.locations.add(location);
                    location.setName(xpp.getAttributeValue(null, "name"));
                    System.out.println(location.getName());
                }
                // Wireless or Standard IHC resources
                else if (xpp.getName().equals("product_airlink")) {
                    try {
                        IHCResource ihcResource = ResourceFactory.createResource(xpp.getAttributeValue(null, "product_identifier").replace("_0x", ""), this);
                        ihcResource.setName(xpp.getAttributeValue(null, "name"));
                        if (xpp.getAttributeValue(null, "position") != null)
                            ihcResource.setPosition(xpp.getAttributeValue(null, "position"));
                        else
                            ihcResource.setPosition("");

                        ihcResource.setState(false);
                        ihcResource.setupResources(xpp, xpp.getName());
                        location.getResources().add(ihcResource);
                    } catch (Exception e) {
                        Crashlytics.getInstance().core.logException(e);
                        Log.e(TAG, e.getMessage());
                    }
                } else if (xpp.getName().equals("product_dataline")) {
                    try {
                        IHCResource ihcResource = ResourceFactory.createResource(xpp.getAttributeValue(null, "product_identifier").replace("_0x", ""), this);
                        ihcResource.setName(xpp.getAttributeValue(null, "name"));
                        if (xpp.getAttributeValue(null, "position") != null)
                            ihcResource.setPosition(xpp.getAttributeValue(null, "position"));
                        else
                            ihcResource.setPosition("");

                        ihcResource.setState(false);
                        ihcResource.setupResources(xpp, xpp.getName());
                        location.getResources().add(ihcResource);
                    } catch (Exception e) {
                        Crashlytics.getInstance().core.logException(e);
                        Log.e(TAG, e.getMessage());
                    }

                }
            }
            try {
                eventType = xpp.next();
            } catch (XmlPullParserException pullException) {
                Log.e(TAG, pullException.getMessage());
            }

        }

        return home;
    }


    private void sendBroadcast(IHC_EVENTS action, IHCMESSAGE message) {
        // Inform broadcastreceivers about RuntimeValues has changed.
        Intent intent = new Intent(action.toString());
        if (message != null)
            intent.putExtra(IHCMESSAGE.class.getName(), message.toString());

        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }


}
