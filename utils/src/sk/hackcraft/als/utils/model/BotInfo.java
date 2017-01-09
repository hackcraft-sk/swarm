package sk.hackcraft.als.utils.model;

public class BotInfo {

    private final int id;
    private final String name;
    private final BotType botType;
    private final String fileUrl;
    private final String fileHash;

    public BotInfo(int id, String name, BotType botType, String fileUrl, String fileHash) {
        this.id = id;
        this.name = name;
        this.botType = botType;
        this.fileUrl = fileUrl;
        this.fileHash = fileHash;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BotType getBotType() {
        return botType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileHash() {
        return fileHash;
    }
}
