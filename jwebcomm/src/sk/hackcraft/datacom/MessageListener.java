package sk.hackcraft.datacom;

public interface MessageListener {

    void onMessageReceived(byte content[]);
}
