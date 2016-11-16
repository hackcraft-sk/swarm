package sk.hackcraft.als.utils.components;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class SSLSocketFactoryCreator {

    public static SSLSocketFactory create(String certificatePath) throws SocketFactoryCreationException {
        InputStream cert = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = new FileInputStream(certificatePath);
            Certificate ca = cf.generateCertificate(cert);

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new SocketFactoryCreationException(e);
        } finally {
            if (cert != null) {
                try {
                    cert.close();
                } catch (IOException e) {
                    System.out.println("Can't close certificate stream.");
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    public static class SocketFactoryCreationException extends Exception {
        public SocketFactoryCreationException(Exception cause) {
            super("Can't create SSL socket factory.", cause);
        }
    }

}
