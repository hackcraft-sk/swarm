package sk.hackcraft.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreLoader {

    public KeyStore load(String filePath, String password) throws IOException {
        InputStream is = new FileInputStream(filePath);
        BufferedInputStream bis = new BufferedInputStream(is);

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(bis, password.toCharArray());
            is.close();
            return keyStore;
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Can't get keystore instance.", e);
        } catch (CertificateException e) {
            throw new IllegalStateException("Can't load keystore.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can't check keystore integrity.", e);
        }
    }
}
