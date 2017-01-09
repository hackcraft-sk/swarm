package sk.hackcraft.als.master.storage.implementation;

import sk.hackcraft.als.master.storage.definition.BotPersistentDataStorage;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.IOException;
import java.nio.file.Path;

public class MockBotPersistentDataStorage extends AbstractComponent implements BotPersistentDataStorage {

    @Override
    public void setSlaveEntryPointMapping(int slaveId, Path entryPoint) {

    }

    @Override
    public void prepareStorage(int tournamentId, int userId, int slaveId) throws IOException {

    }
}
