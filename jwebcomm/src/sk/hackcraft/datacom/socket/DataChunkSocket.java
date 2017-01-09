package sk.hackcraft.datacom.socket;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataChunkSocket {

    private static final Logger logger = Logger.getLogger(DataChunkSocket.class.getName());
    private final String address;
    private final int port;

    private Socket socket;
    private DataInput input;
    private DataOutput output;

    public DataChunkSocket(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(address, port);

        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.INFO, "Can't properly close socket.", e);
        }
    }

    public void send(byte content[]) throws IOException {
        output.writeInt(content.length);
        output.write(content);
    }

    public byte[] receive() throws IOException {
        int length = input.readInt();
        byte content[] = new byte[length];

        input.readFully(content);

        return content;
    }
}
