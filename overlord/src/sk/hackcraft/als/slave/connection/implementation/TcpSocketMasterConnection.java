package sk.hackcraft.als.slave.connection.implementation;

import com.google.gson.Gson;
import sk.hackcraft.als.slave.connection.definition.MasterConnection;
import sk.hackcraft.als.utils.SlaveMatchSpecification;
import sk.hackcraft.als.utils.connection.masterslave.AbstractTcpSocketConnection;
import sk.hackcraft.als.utils.connection.masterslave.MessageId;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.IOException;
import java.net.Socket;

public class TcpSocketMasterConnection extends AbstractTcpSocketConnection implements MasterConnection {

    public TcpSocketMasterConnection(Socket socket, Gson gson, int overlordId) throws IOException {
        super(socket, gson);
        writeHeaderAndContent(MessageId.SLAVE_ID, overlordId);
    }

    @Override
    public SlaveMatchSpecification waitForSetup() throws IOException {
        while (true) {
            MessageId id = readHeader();

            switch (id) {
                case PING:
                    log.m("Ping from master received.");
                    writeHeader(MessageId.PING);
                    break;
                case SLAVE_SETUP:
                    return readContent(SlaveMatchSpecification.class);
                default:
                    throw new IOException("Unexpected " + id + ".");
            }
        }
    }

    @Override
    public void postSetupCompleted(SlaveSetupReport report) throws IOException {
        writeHeaderAndContent(MessageId.SLAVE_SETUP_REPORT, report);
    }

    @Override
    public void waitForGo() throws IOException {
        readAndExpectHeader(MessageId.RUN_MATCH);
    }

    @Override
    public void postMatchReport(SlaveMatchReport slaveMatchReport) throws IOException {
        writeHeaderAndContent(MessageId.SLAVE_MATCH_REPORT, slaveMatchReport);
    }
}
