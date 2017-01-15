package sk.hackcraft.als.slave.config;

import sk.hackcraft.als.utils.config.Config;

public class SlaveConfig extends Config {

    private final ComponentsConfig componentsConfig;

    public SlaveConfig(ComponentsConfig componentsConfig) {
        this.componentsConfig = componentsConfig;
    }

    @Override
    public ComponentsConfig getConfigModel() {
        return componentsConfig;
    }

    public static class ComponentsConfig {

        private final TcpSocketMasterConnection tcpSocketMasterConnection;
        private final MockMasterConnection mockMasterConnection;
        private final TcpSocketParasiteConnection tcpSocketParasiteConnection;
        private final MockParasiteConnection mockParasiteConnection;
        private final MockGameEnvironment mockGameEnvironment;
        private final BWGameEnvironment bwGameEnvironment;
        private final MockBotLauncherFactory mockBotLauncherFactory;
        private final BWBotLauncherFactory bwBotLauncherFactory;

        public ComponentsConfig(TcpSocketMasterConnection tcpSocketMasterConnection, MockMasterConnection mockMasterConnection, TcpSocketParasiteConnection tcpSocketParasiteConnection, MockParasiteConnection mockParasiteConnection, MockGameEnvironment mockGameEnvironment, BWGameEnvironment bwGameEnvironment, MockBotLauncherFactory mockBotLauncherFactory, BWBotLauncherFactory bwBotLauncherFactory) {
            this.tcpSocketMasterConnection = tcpSocketMasterConnection;
            this.mockMasterConnection = mockMasterConnection;
            this.tcpSocketParasiteConnection = tcpSocketParasiteConnection;
            this.mockParasiteConnection = mockParasiteConnection;
            this.mockGameEnvironment = mockGameEnvironment;
            this.bwGameEnvironment = bwGameEnvironment;
            this.mockBotLauncherFactory = mockBotLauncherFactory;
            this.bwBotLauncherFactory = bwBotLauncherFactory;
        }

        public TcpSocketMasterConnection getTcpSocketMasterConnection() {
            return tcpSocketMasterConnection;
        }

        public MockMasterConnection getMockMasterConnection() {
            return mockMasterConnection;
        }

        public TcpSocketParasiteConnection getTcpSocketParasiteConnection() {
            return tcpSocketParasiteConnection;
        }

        public MockParasiteConnection getMockParasiteConnection() {
            return mockParasiteConnection;
        }

        public MockGameEnvironment getMockGameEnvironment() {
            return mockGameEnvironment;
        }

        public BWGameEnvironment getBwGameEnvironment() {
            return bwGameEnvironment;
        }

        public MockBotLauncherFactory getMockBotLauncherFactory() {
            return mockBotLauncherFactory;
        }

        public BWBotLauncherFactory getBwBotLauncherFactory() {
            return bwBotLauncherFactory;
        }
    }

    public static class TcpSocketMasterConnection {

        private final String host;
        private final int port;
        private final int overlordId;

        public TcpSocketMasterConnection(String host, int port, int overlordId) {
            this.host = host;
            this.port = port;
            this.overlordId = overlordId;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public int getOverlordId() {
            return overlordId;
        }
    }

    public static class MockMasterConnection {

    }

    public static class TcpSocketParasiteConnection {

        private final int port;
        private final long connectionTimeoutMillis;
        private final long pingTimeoutMillis;

        public TcpSocketParasiteConnection(int port, long connectionTimeoutMillis, long pingTimeoutMillis) {
            this.port = port;
            this.connectionTimeoutMillis = connectionTimeoutMillis;
            this.pingTimeoutMillis = pingTimeoutMillis;
        }

        public int getPort() {
            return port;
        }

        public long getConnectionTimeoutMillis() {
            return connectionTimeoutMillis;
        }

        public long getPingTimeoutMillis() {
            return pingTimeoutMillis;
        }
    }

    public static class MockParasiteConnection {

    }

    public static class MockGameEnvironment {

    }

    public static class BWGameEnvironment {

    }

    public static class MockBotLauncherFactory {

    }

    public static class BWBotLauncherFactory {

    }
}
