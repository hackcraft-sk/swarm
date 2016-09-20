package sk.hackcraft.jwebcomm.http;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class FormRequestFactory {
    private final URL serviceUrl;
    private final SSLSocketFactory sslSocketFactory;

    public FormRequestFactory(String url, KeyStore keyStore) throws IOException {
        this.serviceUrl = new URL(url);

        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tmf.getTrustManagers(), null);
            this.sslSocketFactory = ctx.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Required algorithm not present.", e);
        } catch (KeyManagementException e) {
            throw new SecurityException(e);
        } catch (KeyStoreException e) {
            throw new SecurityException(e);
        }
    }

    public FormRequest createRequest(String actionUrl, FormRequest.Method method) throws IOException {
        URL requestUrl = new URL(serviceUrl.toString() + actionUrl);

        return new FormRequest(sslSocketFactory, requestUrl, method);
    }

    public FormRequest createPostRequest(String actionUrl) throws IOException {
        return createRequest(actionUrl, FormRequest.Method.POST);
    }

    public FormRequest createGetRequest(String actionUrl) throws IOException {
        return createRequest(actionUrl, FormRequest.Method.GET);
    }
}
