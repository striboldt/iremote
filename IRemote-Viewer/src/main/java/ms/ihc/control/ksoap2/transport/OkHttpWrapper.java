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

import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import javax.net.ssl.*;
import org.apache.http.conn.ssl.*;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
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

    public static OkHttpWrapper getInstance() {
        if (instance == null) {
            instance = new OkHttpWrapper();
        }
        return instance;
    }

    private SSLContext getSSLContext(KeyStore trustedKeystore) {

        SSLContext sslContext = null;
        try {
         //   KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
         //   keyManagerFactory.init(trustedKeystore, null);
            TrustManager tm[] = { new PubKeyManager() };
        //    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        //    trustManagerFactory.init(trustedKeystore);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tm, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
      /*  } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();*/
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    public void setConnection(KeyStore trustedKeystore) {

        if (connection == null) {
            connection = new OkHttpClient();
            connection.setSslSocketFactory(getSSLContext(trustedKeystore).getSocketFactory());
            connection.setHostnameVerifier(new AllowAllHostnameVerifier());
            connection.setConnectTimeout(10, TimeUnit.SECONDS);
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

    @Nullable
    public Response call() throws IOException {
        Response response;
        try {
            response = connection.newCall(request).execute();
        } catch (Exception e) {
            Crashlytics.getInstance().core.logException(e);
            throw e;
        }
        return response;
    }


    public final class PubKeyManager implements X509TrustManager
    {
        private String TAG = PubKeyManager.class.getName();

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            Log.i(TAG, "checkClientTrusted: ");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
            if (chain == null) {
                throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
            }

            if (!(chain.length > 0)) {
                throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
            }

            if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {
                throw new CertificateException("checkServerTrusted: AuthType is not RSA");
            }

            // Perform customary SSL/TLS checks
           /* try {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                tmf.init((KeyStore) null);

                for (TrustManager trustManager : tmf.getTrustManagers()) {
                    ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                }
            } catch (Exception e) {
                throw new CertificateException(e);
            }*/

            // Hack ahead: BigInteger and toString(). We know a DER encoded Public Key begins
            // with 0x30 (ASN.1 SEQUENCE and CONSTRUCTED), so there is no leading 0x00 to drop.
            RSAPublicKey pubkey = (RSAPublicKey) chain[0].getPublicKey();
            String encoded = new BigInteger(1 /* positive */, pubkey.getEncoded()).toString(16);
            Log.i(TAG, "checkServerTrusted: public key: " + encoded);

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
