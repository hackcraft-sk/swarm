package sk.hackcraft.datacom.socket;

import sk.hackcraft.datacom.Message;
import sk.hackcraft.datacom.MessagesFactory;

public class SocketMessageFactory implements MessagesFactory {

    @Override
    public Message createMessage(byte[] content) {
        return new SocketMessage(null, content);
    }
}
