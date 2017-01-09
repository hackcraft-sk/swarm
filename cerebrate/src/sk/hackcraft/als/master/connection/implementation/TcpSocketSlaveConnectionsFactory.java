package sk.hackcraft.als.master.connection.implementation;

import com.google.gson.Gson;
import sk.hackcraft.als.master.connection.definition.SlaveConnection;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TcpSocketSlaveConnectionsFactory extends AbstractComponent implements SlaveConnectionsFactory {

    private final int port;

    public TcpSocketSlaveConnectionsFactory(int port) {
        this.port = port;
    }

    @Override
    public List<SlaveConnection> waitForConnections(int number) throws InterruptedException, IOException {
        Set<SlaveConnection> connections = new HashSet<>();

        Gson gson = new Gson();
        while (connections.size() < number) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                Socket socket = serverSocket.accept();

                TcpSocketSlaveConnection slaveConnection = new TcpSocketSlaveConnection(socket, gson);
                connections.add(slaveConnection);

                Set<SlaveConnection> invalidConnections = new HashSet<>();
                for (SlaveConnection connection : connections) {
                    try {
                        connection.ping();
                    } catch (Exception e) {
                        invalidConnections.add(connection);
                    }
                }

                for (SlaveConnection connection : invalidConnections) {
                    connections.remove(connection);
                }
            }
        }

        return new ArrayList<>(connections);
    }
}
