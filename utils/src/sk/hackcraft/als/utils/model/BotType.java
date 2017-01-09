package sk.hackcraft.als.utils.model;

import java.util.HashMap;
import java.util.Map;

public enum BotType {

    CPP_CLIENT("exe"),
    CPP_MODULE("dll"),
    JAVA_CLIENT("jar");

    private static final Map<String, BotType> extensionToTypeMapping = new HashMap<>();

    static {
        for (BotType botType : values()) {
            String extension = botType.getFilenameExtension();
            extensionToTypeMapping.put(extension, botType);
        }
    }

    static public BotType getByExtension(String extension) {
        BotType botType = extensionToTypeMapping.get(extension);

        if (botType == null) {
            throw new IllegalArgumentException("Extension not supported " + extension);
        }

        return botType;
    }

    private final String filenameExtension;

    BotType(String filenameExtension) {
        this.filenameExtension = filenameExtension;
    }

    public String getFilenameExtension() {
        return filenameExtension;
    }

}
