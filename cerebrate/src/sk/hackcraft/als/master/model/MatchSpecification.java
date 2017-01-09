package sk.hackcraft.als.master.model;

import java.util.ArrayList;
import java.util.List;

public class MatchSpecification {

    private final int tournamentId;
    private final int matchId;
    private final String mapUrl;
    private final String mapFileHash;
    private final List<UserBotInfo> userBotInfos;
    private final UserBotInfo videoStreamTargetBotId;

    public MatchSpecification(int tournamentId, int matchId, String mapUrl, String mapFileHash, List<UserBotInfo> userBotInfos, UserBotInfo videoStreamTargetBotId) {
        this.tournamentId = tournamentId;
        this.matchId = matchId;
        this.mapUrl = mapUrl;
        this.mapFileHash = mapFileHash;

        this.userBotInfos = new ArrayList<>(userBotInfos);
        this.videoStreamTargetBotId = videoStreamTargetBotId;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public int getMatchId() {
        return matchId;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public String getMapFileHash() {
        return mapFileHash;
    }

    public List<UserBotInfo> getUserBotInfos() {
        return new ArrayList<>(userBotInfos);
    }

    public UserBotInfo getVideoStreamTarget() {
        return videoStreamTargetBotId;
    }

    @Override
    public String toString() {
        return String.format("#%d, bots %s", matchId, userBotInfos);
    }
}
