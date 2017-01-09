package sk.hackcraft.als.utils;

import java.io.IOException;

public interface ProcessesKiller {

    void killAll() throws IOException;

    void killAll(int timeout) throws IOException;
}
