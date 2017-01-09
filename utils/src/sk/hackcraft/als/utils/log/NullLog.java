package sk.hackcraft.als.utils.log;

public class NullLog implements BareLog {

    private static final BareLog INSTANCE = new NullLog();

    public static BareLog getInstance() {
        return INSTANCE;
    }

    private NullLog() {

    }

    @Override
    public void m(String message) {

    }

    @Override
    public void m(String format, Object... params) {

    }

    @Override
    public void e(String message) {

    }

    @Override
    public void e(String format, Object... params) {

    }

    @Override
    public void e(String message, Throwable throwable) {

    }
}
