package sk.hackcraft.als.master.connection.definition;

import sk.hackcraft.als.utils.SlaveMatchSpecification;
import sk.hackcraft.als.utils.components.Component;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;
import sk.hackcraft.als.utils.reports.SlaveSetupReport;

import java.io.IOException;

public interface SlaveConnection extends Component {

    void close() throws IOException;

    void ping() throws IOException;

    SlaveSetupReport setupSlave(SlaveMatchSpecification slaveMatchSpecification) throws IOException;

    void runMatch() throws IOException;

    SlaveMatchReport waitForResult() throws IOException;
}
