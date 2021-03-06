package sk.hackcraft.als.master.storage.implementation;

import sk.hackcraft.als.master.storage.definition.BotPersistentDataStorage;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DiskBotPersistentDataStorage extends AbstractComponent implements BotPersistentDataStorage {

    private final Map<Integer, Path> slavesEntryPointsMapping = new HashMap<>();

    private final Path storageDirectoryPath;

    public DiskBotPersistentDataStorage(String storageDirectoryPath, String storageEntryPointsPath) {
        this.storageDirectoryPath = Paths.get(storageDirectoryPath);
    }

    @Override
    public void setSlaveEntryPointMapping(int slaveId, Path entryPoint) {
        slavesEntryPointsMapping.put(slaveId, entryPoint);
    }

    @Override
    public void prepareStorage(int tournamentId, int userId, int slaveId) throws IOException {
        Path botStoragePath = assembleStoragePath(tournamentId, userId);

        createStorageForBotIfNotExists(botStoragePath);

        Path entryPointPath = slavesEntryPointsMapping.get(slaveId);

        if (entryPointPath == null) {
            throw new IOException("Entry point not available for slave #" + slaveId);
        }

        activateStorage(botStoragePath, entryPointPath);
    }

    private void createStorageForBotIfNotExists(Path storagePath) throws IOException {
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }
    }

    private Path assembleStoragePath(int tournamentId, int userId) {
        String pathString = String.format("%s/%s/%s", storageDirectoryPath, tournamentId, userId);
        return Paths.get(pathString);
    }

    private void activateStorage(Path botStoragePath, Path entryPointPath) throws IOException {
        if (Files.exists(entryPointPath)) {
            Files.delete(entryPointPath);
        }

        Files.createSymbolicLink(entryPointPath, botStoragePath);
    }
}
