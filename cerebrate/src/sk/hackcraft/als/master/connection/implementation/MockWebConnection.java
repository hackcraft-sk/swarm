package sk.hackcraft.als.master.connection.implementation;

import sk.hackcraft.als.master.connection.definition.WebConnection;
import sk.hackcraft.als.master.model.MatchReport;
import sk.hackcraft.als.master.model.MatchSpecification;
import sk.hackcraft.als.master.model.UserBotInfo;
import sk.hackcraft.als.utils.StandardAchievements;
import sk.hackcraft.als.utils.components.AbstractComponent;
import sk.hackcraft.als.utils.model.BotInfo;
import sk.hackcraft.als.utils.model.BotType;
import sk.hackcraft.als.utils.reports.SlaveMatchReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MockWebConnection extends AbstractComponent implements WebConnection {

    private int botsCount;

    public MockWebConnection(int botsCount) {
        this.botsCount = botsCount;
    }

    @Override
    public MatchSpecification requestMatch() throws IOException {
        List<UserBotInfo> userBotInfos = new ArrayList<>();

        for (int i = 0; i < botsCount; i++) {
            UserBotInfo userBotInfo = new UserBotInfo(i, i);
            userBotInfos.add(userBotInfo);
        }

        UserBotInfo videoStreamBotInfo = userBotInfos.get(0);

        return new MatchSpecification(1, 1, "maps/scmai/scmai2-arena-beginners.scx", "xxx", userBotInfos, videoStreamBotInfo);
    }

    @Override
    public void postMatchReport(MatchReport matchReport) throws IOException {
        if (matchReport.isMatchValid()) {
            for (SlaveMatchReport botResult : matchReport.getSlavesMatchReports()) {
                int botId = botResult.getBotId();
                boolean victor = botResult.getAchievements().contains(StandardAchievements.VICTORY);
                log.m("#%d - %s%n", botId, victor);
            }
        } else {
            log.m("Match was INVALID.");
        }
    }

    @Override
    public BotInfo getBotInfo(int botId) throws IOException {
        return new BotInfo(botId, "Random bot", BotType.JAVA_CLIENT, "hh", "hh");
    }

    @Override
    public byte[] downloadMap(String url, String hash) throws IOException {
        return new byte[0];
    }

    @Override
    public byte[] downloadBot(String url, String hash) throws IOException {
        return new byte[0];
    }
}
