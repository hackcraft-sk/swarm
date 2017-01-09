package sk.hackcraft.als.master.connection.implementation;

import sk.hackcraft.als.master.connection.definition.SlaveConnection;
import sk.hackcraft.als.utils.components.Component;

import java.io.IOException;
import java.util.List;

public interface SlaveConnectionsFactory extends Component {

    List<SlaveConnection> waitForConnections(int number) throws InterruptedException, IOException;
}
