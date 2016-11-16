package sk.hackcraft.als.master;

import java.util.List;
import java.util.Set;

public class MasterConfig {
    private final Tournament tournament;
    private final Storage storage;
    private final Web web;
    private final Components components;

    public MasterConfig(Tournament tournament, Storage storage, Web web, Components components) {
        this.tournament = tournament;
        this.storage = storage;
        this.web = web;
        this.components = components;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Storage getStorage() {
        return storage;
    }

    public Web getWeb() {
        return web;
    }

    public static class Tournament {
        private final String masterId;
        private final int playersCount;
        private final Set<Integer> acceptingIds;

        public Tournament(String masterId, int playersCount, Set<Integer> acceptingIds) {
            this.masterId = masterId;
            this.playersCount = playersCount;
            this.acceptingIds = acceptingIds;
        }

        public String getMasterId() {
            return masterId;
        }

        public int getPlayersCount() {
            return playersCount;
        }

        public Set<Integer> getAcceptingIds() {
            return acceptingIds;
        }
    }

    public static class Storage {
        private final String entryPointsPath;
        private final String storagePath;

        public Storage(String entryPointsPath, String storagePath) {
            this.entryPointsPath = entryPointsPath;
            this.storagePath = storagePath;
        }

        public String getEntryPointsPath() {
            return entryPointsPath;
        }

        public String getStoragePath() {
            return storagePath;
        }
    }

    public static class Web {
        private final String address;
        private final String certificatePath;

        public Web(String address, String certificatePath) {
            this.address = address;
            this.certificatePath = certificatePath;
        }

        public String getAddress() {
            return address;
        }

        public String getCertificatePath() {
            return certificatePath;
        }
    }

    public static class Components {
        private final String webConnection;
        private final String slaveConnection;
        private final String replaysStorage;

        public Components(String webConnection, String slaveConnection, String replaysStorage) {
            this.webConnection = webConnection;
            this.slaveConnection = slaveConnection;
            this.replaysStorage = replaysStorage;
        }

        public String getWebConnection() {
            return webConnection;
        }

        public String getSlaveConnection() {
            return slaveConnection;
        }

        public String getReplaysStorage() {
            return replaysStorage;
        }
    }
}
