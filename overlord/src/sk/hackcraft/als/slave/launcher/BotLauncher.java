package sk.hackcraft.als.slave.launcher;

import java.io.IOException;

public interface BotLauncher {

    void prepare() throws IOException;

    void dispose() throws IOException;
}
