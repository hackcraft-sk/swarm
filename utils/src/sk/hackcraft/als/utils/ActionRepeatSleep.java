package sk.hackcraft.als.utils;

public interface ActionRepeatSleep {

    void sleep() throws InterruptedException;

    void onActionSuccess();

    void onActionFailed();
}
