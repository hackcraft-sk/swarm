package sk.hackcraft.datacom;

public interface MessagesReceiver<I extends Identifiable> extends Runnable {

    void stop();
}
