package sk.hackcraft.bwtv;

import sk.hackcraft.als.utils.Config;
import sk.hackcraft.als.utils.IniFileConfig;
import sk.hackcraft.als.utils.MemoryConfig;
import sk.hackcraft.bwtv.connections.*;
import sk.hackcraft.bwtv.control.Remote;
import sk.hackcraft.bwtv.crudeOverlay.CrudeOverlay;
import sk.hackcraft.bwtv.fancyOverlay.JFrameOverlay;
import sk.hackcraft.bwtv.stream.NullStream;
import sk.hackcraft.bwtv.stream.StreamFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        new Application();
    }

    public Application() {
        File configFile = new File("bwtv.cfg");

        if (!configFile.exists()) {
            throw new RuntimeException("bwtv.cfg is missing.");
        }

        IniFileConfig configLoader = new IniFileConfig();

        MemoryConfig config;
        try {
            config = configLoader.load(configFile);
        } catch (IOException e) {
            throw new RuntimeException("Can't create config from file.", e);
        }

        Config.Section componentsSection = config.getSection("mockComponents");
        final boolean slaveMock = componentsSection.getPair("slaveConnection").getBooleanValue();
        final boolean webMock = componentsSection.getPair("webConnection").getBooleanValue();

        String webAddress = config.getSection("web").getPair("address").getStringValue();
        String slaveAddress = config.getSection("slave").getPair("address").getStringValue();

        final WebConnection webConnection;
        if (webMock) {
            webConnection = new MockWebConnection();
        } else {
            try {
                webConnection = new RealWebConnection(webAddress);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        final SlaveConnection slaveConnection;
        if (slaveMock) {
            slaveConnection = new MockSlaveConnection();
        } else {
            slaveConnection = new RealSlaveConnection(slaveAddress);
        }

        SlaveConnectionFactory slaveConnectionFactory = () -> slaveConnection;

        final MatchEventBroadcaster matchEventBroadcaster = new MatchEventBroadcaster(slaveConnectionFactory);

        SwingUtilities.invokeLater(() ->
            {
                Overlay overlay = new JFrameOverlay(webConnection);
                overlay.setVisible(true);

                MatchEventListener listener = new SwingMatchEventListener((ei) -> overlay.changeScreen(ei));
                matchEventBroadcaster.registerListener(listener);
            }
        );
    }
}
