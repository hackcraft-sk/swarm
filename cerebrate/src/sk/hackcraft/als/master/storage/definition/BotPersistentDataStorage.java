package sk.hackcraft.als.master.storage.definition;

import sk.hackcraft.als.utils.components.Component;

import java.io.IOException;
import java.nio.file.Path;

public interface BotPersistentDataStorage extends Component {

    void setSlaveEntryPointMapping(int slaveId, Path entryPoint);

    void prepareStorage(int tournamentId, int userId, int slaveId) throws IOException;
}
