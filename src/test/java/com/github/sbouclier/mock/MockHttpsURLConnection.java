package com.github.sbouclier.mock;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;

/**
 * HttpsURLConnection mock
 *
 * @author Stéphane Bouclier
 */
public class MockHttpsURLConnection extends HttpsURLConnection {
    public MockHttpsURLConnection(URL url) {
        super(url);
    }

    @Override
    public String getCipherSuite() {
        return null;
    }

    @Override
    public Certificate[] getLocalCertificates() {
        return new Certificate[0];
    }

    @Override
    public Certificate[] getServerCertificates()  {
        return new Certificate[0];
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect()  {

    }

    @Override
    public OutputStream getOutputStream()  {
        return new OutputStream() {
            @Override
            public void write(int b) {

            }
        };
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream("read inputstream".getBytes(StandardCharsets.UTF_8));
    }
}
