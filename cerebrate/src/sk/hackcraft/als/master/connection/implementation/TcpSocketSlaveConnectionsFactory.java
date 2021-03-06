package sk.hackcraft.als.master.connection.implementation;

import com.google.gson.Gson;
import sk.hackcraft.als.master.connection.definition.SlaveConnection;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TcpSocketSlaveConnectionsFactory extends AbstractComponent implements SlaveConnectionsFactory {

    private final int port;
    private final long serverSocketTimeout;

    public TcpSocketSlaveConnectionsFactory(int port, long serverSocketTimeout) {
        this.port = port;
        this.serverSocketTimeout = serverSocketTimeout;
    }

    @Override
    public List<SlaveConnection> waitForConnections(int number) throws InterruptedException, IOException {
        Set<SlaveConnection> connections = new HashSet<>();

        Gson gson = new Gson();
        while (connections.size() < number) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                serverSocket.setSoTimeout((int)serverSocketTimeout);

                TcpSocketSlaveConnection slaveConnection;
                try {
                    Socket socket = serverSocket.accept();

                    socket.setKeepAlive(true);

                    slaveConnection = new TcpSocketSlaveConnection(socket, gson);
                    connections.add(slaveConnection);

                    int id = slaveConnection.readSlaveId();
                    log.m("Slave #%d acquired.", id);
                } catch (SocketTimeoutException e) {
                    log.m("Wait for slave timeout.");
                }

                Set<SlaveConnection> invalidConnections = new HashSet<>();
                for (SlaveConnection connection : connections) {
                    try {
                        int slaveId = connection.getSlaveId();
                        log.m("Pinged slave %d.", slaveId);
                        connection.ping();
                    } catch (Exception e) {
                        invalidConnections.add(connection);
                    }
                }

                for (SlaveConnection connection : invalidConnections) {
                    int lostSlaveId = connection.getSlaveId();
                    log.m("Connection to slave %d lost.", lostSlaveId);
                    connections.remove(connection);
                }
            }
        }

        return new ArrayList<>(connections);
    }
}
