package org.ksoap2.transport;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * HttpsServiceConnectionSE is a service connection that uses a https url connection and requires explicit setting of
 * host, port and file.
 *
 * The explicit setting is necessary since pure url passing and letting the Java URL class parse the string does not
 * work properly on Android.
 *
 * Links for reference:
 * @see "http://stackoverflow.com/questions/2820284/ssl-on-android-strange-issue"
 * @see "http://stackoverflow.com/questions/2899079/custom-ssl-handling-stopped-working-on-android-2-2-froyo"
 * @see "http://code.google.com/p/android/issues/detail?id=2690"
 * @see "http://code.google.com/p/android/issues/detail?id=2764"
 *
 * @author Manfred Moser <manfred@simpligility.com>
 */
public class HttpsServiceConnectionSE implements ServiceConnection {

	private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];

		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}
	} };
	
    private HttpsURLConnection connection;

    public HttpsServiceConnectionSE(String host, int port, String file, int timeout) throws IOException {
        connection = (HttpsURLConnection) new URL(HttpsTransportSE.PROTOCOL, host, port, file).openConnection();
        updateConnectionParameters(timeout);
    }

    private void updateConnectionParameters(int timeout) {
    	
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.getMessage();
		}
		
        
        connection.setConnectTimeout(timeout); // 20 seconds like in iphone app
        connection.setReadTimeout(timeout); // even if we connect fine we want to time out if we cant read anything..
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        //((HttpsURLConnection) connection).setHostnameVerifier(new AllowAllHostnameVerifier());

        // Allowing any hostname  through with https:// so basically disabling host checking when https is used.
        //Attention! This is not secure against man in the middle attacks.
        // supposed to be a workaround for https problems.
        // @see "http://android.amberfog.com/?p=45"
        connection.setHostnameVerifier(new AllowAllHostnameVerifier());

        // when the url is created with protocol, host, port and file this workaround is not necessary
    }

    public void connect() throws IOException {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void setRequestProperty(String key, String value) {
        connection.setRequestProperty(key, value);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        connection.setRequestMethod(requestMethod);
    }

    public OutputStream openOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return connection.getInputStream();
    }

    public InputStream getErrorStream() {
        return connection.getErrorStream();
    }

	public String getHearderField(String headerKey) {
		// TODO Auto-generated method stub
		return connection.getHeaderField(headerKey);
	}
}
