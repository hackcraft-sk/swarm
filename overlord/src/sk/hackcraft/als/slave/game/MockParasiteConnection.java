package sk.hackcraft.als.slave.game;

import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.PlayerColor;
import sk.hackcraft.als.utils.StandardAchievements;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MockParasiteConnection extends AbstractComponent implements ParasiteConnection {

    @Override
    public void close() throws IOException {

    }

    @Override
    public PlayerColor waitForMatchStart() throws IOException {
        return new PlayerColor("#ff0000");
    }

    @Override
    public Set<Achievement> waitForMatchEnd() throws IOException {
        Set<Achievement> achievements = new HashSet<>();
        achievements.add(StandardAchievements.DRAW);
        return achievements;
    }
}
