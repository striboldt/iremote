package ms.ihc.control.ksoap2.transport;

import com.squareup.okhttp.Response;

import java.io.*;
import java.security.KeyStore;
import org.xmlpull.v1.*;
import ms.ihc.control.ksoap2.serialization.SoapEnvelope;

/**
 * A J2SE based HttpTransport layer.
 */
public class HttpTransportSE extends Transport {

    private OkHttpWrapper connection;
    private KeyStore trustedKeystore;

    /**
     * Creates instance of HttpTransportSE with set url
     *
     * @param url the destination to POST SOAP data
     */
    public HttpTransportSE(String url, KeyStore trustedKeystore) {
        super(url);
        System.setProperty("http.keepAlive", "true");
        System.setProperty("http.maxConnections", "5");
        this.trustedKeystore = trustedKeystore;
    }

    /**
     * set the desired soapAction header field
     *
     * @param soapAction the desired soapAction
     * @param envelope   the envelope containing the information for the soap call.
     */
    public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        long rtt_start = System.currentTimeMillis();

        connection = OkHttpWrapper.getInstance();
        connection.setConnection(trustedKeystore);

        if (soapAction == null)
            soapAction = "\"\"";
        byte[] requestData = createRequestData(envelope);
        requestDump = debug ? new String(requestData) : null;
        responseDump = null;

        connection.setUrl(url);
        if (this.sessionCookie != null) {
            connection.addHeader("Cookie", this.sessionCookie);
        }

        connection.addHeader("SOAPAction", soapAction);
        connection.setRequestBody(requestData);

        Response response = connection.call();
        if (response != null && response.isSuccessful()) {
            this.sessionCookie = response.header("set-cookie");
            parseResponse(envelope, response.body().byteStream());
        } else {
            throw new IOException("Unexpected code " + response);
        }

        long rtt = System.currentTimeMillis() - rtt_start;
        System.out.println(soapAction + " network rtt: " + rtt);

    }

}