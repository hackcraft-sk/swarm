package sk.hackcraft.als.utils.connection.masterslave;

public class JsonMessage {

    private final MessageId messageId;
    private final String json;

    public JsonMessage(MessageId messageId, String json) {
        this.messageId = messageId;
        this.json = json;
    }

    public MessageId getMessageId() {
        return messageId;
    }

    public String getJson() {
        return json;
    }
}
