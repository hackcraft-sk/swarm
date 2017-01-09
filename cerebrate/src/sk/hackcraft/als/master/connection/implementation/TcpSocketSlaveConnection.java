package sk.hackcraft.als.master.connection.implementation;

import com.google.gson.Gson;
import sk.hackcraft.als.master.connection.definition.SlaveConnection;
import sk.hackcraft.als.utils.SlaveMatchSpecification;
import sk.hackcraft.als.utils.components.AbstractComponent;
import sk.hackcraft.als.utils.connection.masterslave.AbstractTcpSocketConnection;
import sk.hackcraft.als.utils.connection.masterslave.MessageId;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.*;
import java.net.Socket;

public class TcpSocketSlaveConnection extends AbstractTcpSocketConnection implements SlaveConnection {

    public TcpSocketSlaveConnection(Socket socket, Gson gson) throws IOException {
        super(socket, gson);
    }

    @Override
    public void close() throws IOException {
        if (!socket.isClosed()) {
            write(MessageId.CLOSE);
        }
        socket.close();
    }

    @Override
    public void ping() throws IOException {
        write(MessageId.PING);
        readAndExpect(MessageId.PING);
    }

    @Override
    public SlaveSetupReport setupSlave(SlaveMatchSpecification slaveMatchSpecification) throws IOException {
        write(MessageId.SLAVE_SETUP, slaveMatchSpecification);
        return readContent(MessageId.SLAVE_SETUP_REPORT, SlaveSetupReport.class);
    }

    @Override
    public void runMatch() throws IOException {
        write(MessageId.RUN_MATCH);
    }

    @Override
    public SlaveMatchReport waitForResult() throws IOException {
        return readContent(MessageId.SLAVE_MATCH_REPORT, SlaveMatchReport.class);
    }
}
