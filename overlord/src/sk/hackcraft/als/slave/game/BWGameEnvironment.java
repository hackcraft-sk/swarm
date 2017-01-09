package sk.hackcraft.als.slave.game;

import sk.hackcraft.als.utils.*;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class BWGameEnvironment extends AbstractComponent implements GameEnvironment {

    private String starCraftDirectoryPath;
    private String chaosLauncherDirectoryPath;

    public BWGameEnvironment(String starCraftDirectoryPath, String chaosLauncherDirectoryPath) {
        this.starCraftDirectoryPath = starCraftDirectoryPath;
        this.chaosLauncherDirectoryPath = chaosLauncherDirectoryPath;
    }

    @Override
    public void launch() throws IOException {
        String chaosLauncherAbsolutePath = Paths.get(chaosLauncherDirectoryPath, "Chaoslauncher.exe").toString();
        ProcessBuilder builder = new ProcessBuilder(chaosLauncherAbsolutePath);

        File chaosLauncherDirectoryFile = new File(chaosLauncherDirectoryPath);
        builder.directory(chaosLauncherDirectoryFile);
        builder.start();
    }

    @Override
    public void close() throws IOException {
        Set<String> processNames = new TreeSet<>();
        processNames.add("StarCraft.exe");
        processNames.add("Chaoslauncher.exe");

        ProcessesListFactory processesListFactory = WindowsTasklist::new;

        ProcessesKiller processesKiller = new FastForceProcessesKiller(processNames, processesListFactory);
        processesKiller.killAll();
    }

    @Override
    public void clean() {

    }

    @Override
    public void setupPlayerProfile(String playerName) throws IOException {
        String charactersDirectoryPath = Paths.get(starCraftDirectoryPath, "characters").toString();
        File directoryFile = new File(charactersDirectoryPath);
        File[] files = directoryFile.listFiles();

        if (files == null) {
            throw new IOException("Error opening characters directory.");
        }

        int multiplayerProfilesCount = 0;
        for (File file : files) {
            String fileName = file.getName();
            String[] tokens = fileName.split("\\.");
            String extension = tokens[tokens.length - 1];

            if (!extension.equals("mpc")) {
                throw new IOException("Illegal files in characters directory: " + fileName);
            }

            multiplayerProfilesCount++;

            if (multiplayerProfilesCount != 1) {
                throw new IOException("There have to be exactly 1 multiplayer character file!");
            }
        }

        File file = files[0];

        String filePath = file.getPath();
        Path source = Paths.get(filePath);
        Path target = Paths.get(starCraftDirectoryPath, "characters", playerName + ".mpc");

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void setupMap(String path, byte[] mapBlob) throws IOException {
        Path targetPath = Paths.get(starCraftDirectoryPath, path);
        Files.write(targetPath, mapBlob, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    }

    @Override
    public void setupGameConfig(MemoryConfig config) throws IOException {
        IniFileConfig iniFileConfig = new IniFileConfig();
        String destination = Paths.get(starCraftDirectoryPath, "bwapi-data", "bwapi.ini").toString();
        iniFileConfig.save(config, destination);
    }

    @Override
    public byte[] retrieveGameReplay(int matchId) throws IOException {
        String replayFileName = matchId + ".rep";
        Path replayPath = Paths.get(starCraftDirectoryPath, "maps", "replays", replayFileName);

        long start = System.nanoTime();
        long timeout = TimeUnit.SECONDS.toNanos(5);

        while (System.nanoTime() < start + timeout) {
            if (!Files.exists(replayPath)) {
                Thread.yield();
            }
        }

        return Files.readAllBytes(replayPath);
    }
}
