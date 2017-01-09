package sk.hackcraft.als.utils.files;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5ChecksumCreator implements ChecksumCreator {

    private final InputStream input;

    public MD5ChecksumCreator(InputStream input) {
        this.input = input;
    }

    @Override
    public String create() throws IOException {
        try {
            byte[] digest = createDigest(input);
            String checksum = "";

            for (byte chunk : digest) {
                checksum += Integer.toString((chunk & 0xff) + 0x100, 16).substring(1);
            }

            return checksum;
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("Can't create MD5 masterslave digest instance.");
        }
    }

    private static byte[] createDigest(InputStream input) throws IOException, NoSuchAlgorithmException {
        MessageDigest complete = MessageDigest.getInstance("MD5");

        byte[] buffer = new byte[1024];
        int numRead;

        do {
            numRead = input.read(buffer);

            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        }
        while (numRead != -1);

        return complete.digest();
    }
}
