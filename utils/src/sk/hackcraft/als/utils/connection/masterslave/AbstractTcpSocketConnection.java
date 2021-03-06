package sk.hackcraft.als.utils.connection.masterslave;

import com.google.gson.Gson;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.*;
import java.net.Socket;

public class AbstractTcpSocketConnection extends AbstractComponent {

    protected final Gson gson;
    protected final Socket socket;
    protected final DataInput dataInput;
    protected final DataOutput dataOutput;

    protected AbstractTcpSocketConnection(Socket socket, Gson gson) throws IOException {
        this.gson = gson;
        this.socket = socket;

        InputStream is = socket.getInputStream();
        dataInput = new DataInputStream(is);

        OutputStream os = socket.getOutputStream();
        dataOutput = new DataOutputStream(os);
    }

    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    protected void writeHeader(MessageId messageId) throws IOException {
        int rawId = messageId.getRawId();
        dataOutput.writeInt(rawId);
    }

    protected void writeHeaderAndContent(MessageId messageId, Object data) throws IOException {
        writeHeader(messageId);
        String json = gson.toJson(data);
        dataOutput.writeUTF(json);
    }

    protected MessageId readHeader() throws IOException {
        int rawId = dataInput.readInt();
        return MessageId.fromRawId(rawId);
    }

    protected void readAndExpectHeader(MessageId expectedMessageId) throws IOException {
        int rawId = dataInput.readInt();
        MessageId id = MessageId.fromRawId(rawId);

        if (id != expectedMessageId) {
            throw new IllegalStateException("Unexpected " + id + ", " + expectedMessageId + " expected.");
        }
    }

    protected <T> T readContent(Class<T> dataType) throws IOException {
        String json = dataInput.readUTF();
        return gson.fromJson(json, dataType);
    }

    protected <T> T readHeaderAndContentWithHeaderCheck(MessageId expectedMessageId, Class<T> dataType) throws IOException {
        readAndExpectHeader(expectedMessageId);

        String json = dataInput.readUTF();
        return gson.fromJson(json, dataType);
    }
}
