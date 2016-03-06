package sk.hackcraft.als.master;

public class UserBotInfo {

    private final int userId;
    private final int botId;

    public UserBotInfo(int userId, int botId) {
        this.userId = userId;
        this.botId = botId;
    }

    public int getUserId() {
        return userId;
    }

    public int getBotId() {
        return botId;
    }

}
