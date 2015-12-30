/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * Copyright (c) 2006, James Seigel, Calgary, AB., Canada
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
 * IN THE SOFTWARE. */

package ms.ihc.control.ksoap2.transport;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import javax.net.ssl.*;
import org.apache.http.conn.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.concurrent.TimeUnit;


/**
 * Connection for J2SE environments.
 */
public class OkHttpWrapper {

    private static OkHttpWrapper instance;
    private Request request;
    private OkHttpClient connection;

    public static final MediaType TEXT = MediaType.parse("text/xml; charset=utf-8");

    protected OkHttpWrapper() {
        // Exists only to defeat instantiation.
    }

    public static OkHttpWrapper getInstance(){
        if(instance == null) {
            instance = new OkHttpWrapper();
        }
        return instance;
    }

    private static SSLContext getSSLContext(KeyStore trustedKeystore) {

        SSLContext sslContext = null;
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(trustedKeystore, null);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustedKeystore);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    public void setConnection(KeyStore trustedKeystore){
        if(connection == null) {
            connection = new OkHttpClient();
            connection.setSslSocketFactory(getSSLContext(trustedKeystore).getSocketFactory());
            connection.setHostnameVerifier(new AllowAllHostnameVerifier());
            connection.setConnectTimeout(12000, TimeUnit.MILLISECONDS);
            connection.setFollowRedirects(false);
        }
    }

    public void setUrl(String url) {
        request = new Request.Builder().url(url).build();
    }

    public void addHeader(String key, String value) {
        request = request.newBuilder().addHeader(key, value).build();
    }

    public void setRequestBody(byte[] requestBody) {
        RequestBody body = RequestBody.create(TEXT, requestBody);
        request = request.newBuilder().post(body).build();
    }

    public Response call() throws IOException {
        Response response = connection.newCall(request).execute();
        return response;
    }

}
