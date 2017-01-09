package sk.hackcraft.datacom;

import java.io.IOException;

public interface Message {

    void send() throws IOException;

    void send(Listener listener) throws IOException;

    byte[] sendAndWait() throws IOException;

    interface Listener {

        void onResponseReceived(byte response[]);

        void onError(IOException e);
    }
}