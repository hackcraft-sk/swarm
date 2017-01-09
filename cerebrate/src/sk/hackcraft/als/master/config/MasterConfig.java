package sk.hackcraft.als.master.config;

import sk.hackcraft.als.utils.config.Config;

import java.util.Set;

public class MasterConfig extends Config<MasterConfig.ComponentsConfig> {

    private ComponentsConfig componentsConfig;

    public MasterConfig(ComponentsConfig componentsConfig) {
        this.componentsConfig = componentsConfig;
    }

    @Override
    public ComponentsConfig getConfigModel() {
        return componentsConfig;
    }

    public static class ComponentsConfig {

        private final RetrofitWebConnection retrofitWebConnection;
        private final MockWebConnection mockWebConnection;
        private final TcpSocketSlaveConnectionsFactory tcpSocketSlaveConnectionsFactory;
        private final MockSlaveConnectionsFactory mockSlaveConnectionsFactory;
        private final DiskReplaysStorage diskReplaysStorage;
        private final MockReplaysStorage mockReplaysStorage;
        private final DiskBotPersistentStorage diskBotPersistentStorage;
        private final MockBotPersistentDataStorage mockBotPersistentDataStorage;

        public ComponentsConfig(RetrofitWebConnection retrofitWebConnection, MockWebConnection mockWebConnection, TcpSocketSlaveConnectionsFactory tcpSocketSlaveConnectionsFactory, MockSlaveConnectionsFactory mockSlaveConnectionsFactory, DiskReplaysStorage diskReplaysStorage, MockReplaysStorage mockReplaysStorage, DiskBotPersistentStorage diskBotPersistentStorage, MockBotPersistentDataStorage mockBotPersistentDataStorage) {
            this.retrofitWebConnection = retrofitWebConnection;
            this.mockWebConnection = mockWebConnection;
            this.tcpSocketSlaveConnectionsFactory = tcpSocketSlaveConnectionsFactory;
            this.mockSlaveConnectionsFactory = mockSlaveConnectionsFactory;
            this.diskReplaysStorage = diskReplaysStorage;
            this.mockReplaysStorage = mockReplaysStorage;
            this.diskBotPersistentStorage = diskBotPersistentStorage;
            this.mockBotPersistentDataStorage = mockBotPersistentDataStorage;
        }

        public RetrofitWebConnection getRetrofitWebConnection() {
            return retrofitWebConnection;
        }

        public MockWebConnection getMockWebConnection() {
            return mockWebConnection;
        }

        public TcpSocketSlaveConnectionsFactory getTcpSocketSlaveConnectionsFactory() {
            return tcpSocketSlaveConnectionsFactory;
        }

        public MockSlaveConnectionsFactory getMockSlaveConnectionsFactory() {
            return mockSlaveConnectionsFactory;
        }

        public DiskReplaysStorage getDiskReplaysStorage() {
            return diskReplaysStorage;
        }

        public MockReplaysStorage getMockReplaysStorage() {
            return mockReplaysStorage;
        }

        public DiskBotPersistentStorage getDiskBotPersistentStorage() {
            return diskBotPersistentStorage;
        }

        public MockBotPersistentDataStorage getMockBotPersistentDataStorage() {
            return mockBotPersistentDataStorage;
        }
    }

    public static class RetrofitWebConnection {

        private final String address;
        private final String certificatePath;
        private final String masterId;
        private final Set<Integer> acceptingIds;

        public RetrofitWebConnection(String address, String certificatePath, String masterId, Set<Integer> acceptingIds) {
            this.address = address;
            this.certificatePath = certificatePath;
            this.masterId = masterId;
            this.acceptingIds = acceptingIds;
        }

        public String getAddress() {
            return address;
        }

        public String getCertificatePath() {
            return certificatePath;
        }

        public String getMasterId() {
            return masterId;
        }

        public Set<Integer> getAcceptingIds() {
            return acceptingIds;
        }
    }

    public static class MockWebConnection {

    }

    public static class TcpSocketSlaveConnectionsFactory {

        private final int port;

        public TcpSocketSlaveConnectionsFactory(int port) {
            this.port = port;
        }

        public int getPort() {
            return port;
        }
    }

    public static class MockSlaveConnectionsFactory {

        private final int targetCount;

        public MockSlaveConnectionsFactory(int targetCount) {
            this.targetCount = targetCount;
        }

        public int getTargetCount() {
            return targetCount;
        }
    }

    public static class DiskReplaysStorage {

        private final String storagePath;

        public DiskReplaysStorage(String storagePath) {
            this.storagePath = storagePath;
        }

        public String getStoragePath() {
            return storagePath;
        }
    }

    public static class MockReplaysStorage {

    }

    public static class DiskBotPersistentStorage {

        private final String entryPointsPath;
        private final String storagePath;

        public DiskBotPersistentStorage(String entryPointsPath, String storagePath) {
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

    public static class MockBotPersistentDataStorage {

    }
}