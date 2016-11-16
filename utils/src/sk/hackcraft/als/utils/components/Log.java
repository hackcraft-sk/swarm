package sk.hackcraft.als.utils.components;

public interface Log {
    void m(String format, Object... params);

    void e(String format, Object... params);
}
