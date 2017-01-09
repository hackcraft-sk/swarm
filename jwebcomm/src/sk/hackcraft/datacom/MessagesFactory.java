package sk.hackcraft.datacom;

public interface MessagesFactory {

    Message createMessage(byte content[]);
}
