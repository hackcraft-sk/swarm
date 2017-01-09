package sk.hackcraft.als.utils;

public class SimpleActionRepeatSleep implements ActionRepeatSleep {

    private long sleepDuration;

    private int failsCount;

    public SimpleActionRepeatSleep(long sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    @Override
    public void sleep() throws InterruptedException {
        long timeout = sleepDuration * failsCount;
        Thread.sleep(timeout);
    }

    @Override
    public void onActionSuccess() {
        failsCount = 0;
    }

    @Override
    public void onActionFailed() {
        failsCount++;
    }
}
