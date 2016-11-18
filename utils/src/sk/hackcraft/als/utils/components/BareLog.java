package sk.hackcraft.als.utils.components;

public interface BareLog {
    void m(String message);
    void m(String format, Object... params);

    void e(String message);
    void e(String format, Object... params);
    void e(String message, Throwable throwable);
}
