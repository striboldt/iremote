/**
 *  Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. 
 *
 * Contributor(s): John D. Beatty, Dave Dash, F. Hunter, Alexander Krebs, 
 *                 Lars Mehrmann, Sean McDaniel, Thomas Strang, Renaud Tognelli 
 * */
package org.ksoap2.transport;

import java.io.*;
import java.security.KeyStore;

import org.apache.commons.logging.Log;
import org.ksoap2.*;
import org.xmlpull.v1.*;

/**
 * A J2SE based HttpTransport layer.
 */
public class HttpTransportSE extends Transport {

	private ServiceConnection connection;
    private KeyStore trustedKeystore;

    /**
     * Creates instance of HttpTransportSE with set url
     * 
     * @param url
     *            the destination to POST SOAP data
     */
    public HttpTransportSE(String url, KeyStore trustedKeystore) {
        super(url);
        this.trustedKeystore = trustedKeystore;

    }


    /**
     * set the desired soapAction header field
     * 
     * @param soapAction
     *            the desired soapAction
     * @param envelope
     *            the envelope containing the information for the soap call.
     */
    public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        long rtt_start = System.currentTimeMillis();

        if (soapAction == null)
            soapAction = "\"\"";
        byte[] requestData = createRequestData(envelope);
        requestDump = debug ? new String(requestData) : null;
        responseDump = null;

        this.connection = new ServiceConnectionSE(url,trustedKeystore);

        if(this.sessionCookie != null)
        	connection.setRequestProperty("Cookie", this.sessionCookie);
        
        connection.setRequestProperty("User-Agent", "kSOAP/2.0");
        connection.setRequestProperty("SOAPAction", soapAction);      
        connection.setRequestProperty("Content-Type", "text/xml");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Content-Length", "" + requestData.length);
        connection.setRequestMethod("POST");
        
        BufferedOutputStream os = new BufferedOutputStream(connection.openOutputStream(),8192);
        os.write(requestData, 0, requestData.length);
        os.flush();
        os.close();
        BufferedInputStream is;

        try {
            is = new BufferedInputStream(connection.openInputStream(),8192);
            	this.sessionCookie = connection.getHearderField("set-cookie");
        } catch (IOException e) {
            is = new BufferedInputStream(connection.getErrorStream(),8192);
        }

        if (debug) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
            while (true) {
                int rd = is.read(buf, 0, 256);
                if (rd == -1)
                    break;
                bos.write(buf, 0, rd);
            }
            bos.flush();
            buf = bos.toByteArray();
            responseDump = new String(buf);
            is.close();
            is = new BufferedInputStream(new ByteArrayInputStream(buf), 8192);
        }
        parseResponse(envelope, is);
        connection.disconnect();
        long rtt = System.currentTimeMillis() - rtt_start;
        System.out.println( soapAction +" network rtt: " + rtt );

    }

}