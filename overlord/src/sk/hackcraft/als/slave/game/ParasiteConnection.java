package sk.hackcraft.als.slave.game;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.PlayerColor;
import sk.hackcraft.als.utils.components.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Interface representing connection to Parasite module
 */
public interface ParasiteConnection extends Component {

    void close() throws IOException;

    PlayerColor waitForMatchStart() throws IOException;

    Set<Achievement> waitForMatchEnd() throws IOException;
}
