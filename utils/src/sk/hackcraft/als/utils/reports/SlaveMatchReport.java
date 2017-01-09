package sk.hackcraft.als.utils.reports;

import sk.hackcraft.als.utils.Achievement;

import java.util.HashSet;
import java.util.Set;

public class SlaveMatchReport {

    private final boolean valid;

    private final int matchId;
    private final int botId;
    private final Set<Achievement> achievements;
    private final byte[] replayBlob;

    /**
     * Constructs new slave match report.
     *
     * @param valid        validity of game
     * @param botId        id of bot
     * @param achievements earned achievements, empty if game was invalid
     * @param replayBlob   contents of replay file
     */
    public SlaveMatchReport(boolean valid, int matchId, int botId, Set<Achievement> achievements, byte[] replayBlob) {
        this.valid = valid;

        this.matchId = matchId;
        this.botId = botId;
        this.achievements = new HashSet<>(achievements);
        this.replayBlob = replayBlob;
    }

    public boolean isValid() {
        return valid;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getBotId() {
        return botId;
    }

    public Set<Achievement> getAchievements() {
        return achievements;
    }

    public byte[] getReplayBlob() {
        return replayBlob;
    }

    @Override
    public String toString() {
        return String.format("Match %d, BotInfo #%d, achievements %s", matchId, botId, achievements);
    }
}
