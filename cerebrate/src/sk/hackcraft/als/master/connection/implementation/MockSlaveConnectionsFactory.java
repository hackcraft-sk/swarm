package sk.hackcraft.als.master.connection.implementation;

import sk.hackcraft.als.master.connection.definition.SlaveConnection;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.StandardAchievements;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.util.ArrayList;
import java.util.List;

public class MockSlaveConnectionsFactory extends AbstractComponent implements SlaveConnectionsFactory {

    @Override
    public List<SlaveConnection> waitForConnections(int number) {
        List<SlaveConnection> connections = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            Achievement resultAchievement = (i == 0) ? StandardAchievements.VICTORY : StandardAchievements.DEFEAT;
            SlaveConnection slaveConnection = new MockSlaveConnection(resultAchievement);
            connections.add(slaveConnection);
        }

        return connections;
    }
}
