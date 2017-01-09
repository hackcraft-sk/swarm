package sk.hackcraft.datacom.socket;

import sk.hackcraft.datacom.Identifiable;
import sk.hackcraft.datacom.MessageListener;
import sk.hackcraft.datacom.MessagesReceiver;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketMessagesReceiver<I> implements MessagesReceiver<Identifiable> {

    private static final Logger logger = Logger.getLogger(SocketMessagesReceiver.class.getName());

    private DataChunkSocket dataChunkSocket;

    private volatile boolean run;

    private Map<Integer, MessageListener> messageListeners;

    public SocketMessagesReceiver(DataChunkSocket dataChunkSocket) {
        this.dataChunkSocket = dataChunkSocket;
    }

    @Override
    public void run() {
        run = true;

        while (run) {
            try {
                processMessage();
            } catch (IOException e) {
                logger.log(Level.INFO, "Can't process message.", e);

                tryReconnect();
            }
        }
    }

    private void processMessage() throws IOException {
        byte content[] = dataChunkSocket.receive();

        DataInput dataInput = new DataInputStream(new ByteArrayInputStream(content));

        int type = dataInput.readInt();

        MessageListener listener = messageListeners.get(type);
        listener.onMessageReceived(content);
    }

    private void tryReconnect() {
        try {
            dataChunkSocket.connect();
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Can't create connection.", ioe);
        }
    }

    @Override
    public void stop() {
        run = false;
    }
}
