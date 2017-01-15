package sk.hackcraft.als.utils.connection.masterslave;

public enum MessageId {
    PING(1),
    SLAVE_SETUP(3),
    SLAVE_SETUP_REPORT(4),
    RUN_MATCH(5),
    SLAVE_MATCH_REPORT(6),
    SLAVE_ID(7);

    public static MessageId fromRawId(int rawId) {
        for (MessageId messageId : values()) {
            int id = messageId.getRawId();
            if (id == rawId) {
                return messageId;
            }
        }

        throw new IllegalArgumentException(rawId + " is not a valid rawId.");
    }

    private final int rawId;

    MessageId(int rawId) {
        this.rawId = rawId;
    }

    public int getRawId() {
        return rawId;
    }
}
