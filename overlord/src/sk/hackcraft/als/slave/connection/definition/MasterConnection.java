package sk.hackcraft.als.slave.connection.definition;

import sk.hackcraft.als.utils.SlaveMatchSpecification;
import sk.hackcraft.als.utils.components.Component;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.IOException;

public interface MasterConnection extends Component {

    SlaveMatchSpecification waitForSetup() throws IOException;

    void postSetupCompleted(SlaveSetupReport report) throws IOException;

    void waitForGo() throws IOException;

    void postMatchReport(SlaveMatchReport slaveMatchReport) throws IOException;

    void close() throws IOException;
}
