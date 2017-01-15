package sk.hackcraft.als.master.connection.implementation;

import com.google.gson.Gson;
import sk.hackcraft.als.master.connection.definition.SlaveConnection;
import sk.hackcraft.als.utils.SlaveMatchSpecification;
import sk.hackcraft.als.utils.connection.masterslave.AbstractTcpSocketConnection;
import sk.hackcraft.als.utils.connection.masterslave.MessageId;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.*;
import java.net.Socket;

public class TcpSocketSlaveConnection extends AbstractTcpSocketConnection implements SlaveConnection {

    private int slaveId = -1;

    public TcpSocketSlaveConnection(Socket socket, Gson gson) throws IOException {
        super(socket, gson);
    }

    @Override
    public int readSlaveId() throws IOException {
        this.slaveId = readHeaderAndContentWithHeaderCheck(MessageId.SLAVE_ID, Integer.class);
        return slaveId;
    }

    @Override
    public int getSlaveId() {
        return slaveId;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public void ping() throws IOException {
        writeHeader(MessageId.PING);
        readAndExpectHeader(MessageId.PING);
    }

    @Override
    public SlaveSetupReport setupSlave(SlaveMatchSpecification slaveMatchSpecification) throws IOException {
        writeHeaderAndContent(MessageId.SLAVE_SETUP, slaveMatchSpecification);
        return readHeaderAndContentWithHeaderCheck(MessageId.SLAVE_SETUP_REPORT, SlaveSetupReport.class);
    }

    @Override
    public void runMatch() throws IOException {
        writeHeader(MessageId.RUN_MATCH);
    }

    @Override
    public SlaveMatchReport waitForResult() throws IOException {
        return readHeaderAndContentWithHeaderCheck(MessageId.SLAVE_MATCH_REPORT, SlaveMatchReport.class);
    }
}
