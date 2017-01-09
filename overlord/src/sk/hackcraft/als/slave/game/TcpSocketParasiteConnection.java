package sk.hackcraft.als.slave.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import sk.hackcraft.als.utils.Achievement;
import sk.hackcraft.als.utils.MarkAchievement;
import sk.hackcraft.als.utils.PlayerColor;
import sk.hackcraft.als.utils.components.AbstractComponent;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class TcpSocketParasiteConnection extends AbstractComponent implements ParasiteConnection {

    private Socket socket;
    private DataInputStream input;

    public TcpSocketParasiteConnection(int port, long connectTimeoutMillis, long pingTimeoutMillis) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout((int) connectTimeoutMillis);
            socket = serverSocket.accept();

            socket.setSoTimeout((int) pingTimeoutMillis);

            input = new DataInputStream(socket.getInputStream());
        }
    }

    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    private String waitForMessage() throws IOException {
        int size = input.readInt();
        byte bytes[] = new byte[size];
        input.readFully(bytes);

        return new String(bytes);
    }

    @Override
    public PlayerColor waitForMatchStart() throws IOException {
        String message = waitForMessage();

        JsonParser parser = new JsonParser();
        JsonObject response = parser.parse(message).getAsJsonObject();

        if (!response.has("playerColor")) {
            throw new IOException("Expected playerColor masterslave.");
        }

        String hexColor = response.get("playerColor").getAsString();
        return new PlayerColor(hexColor);
    }

    @Override
    public Set<Achievement> waitForMatchEnd() throws IOException {
        while (true) {
            String message = waitForMessage();

            JsonParser parser = new JsonParser();
            JsonObject response = parser.parse(message).getAsJsonObject();

            if (response.has("ping")) {
                log.m(".");
                continue;
            }

            if (response.has("achievements")) {
                Set<Achievement> achievements = new HashSet<>();
                JsonArray achievementsJsonArray = response.getAsJsonArray("achievements");
                for (JsonElement element : achievementsJsonArray) {
                    String achievementName = element.getAsString();
                    Achievement achievement = new MarkAchievement(achievementName);
                    achievements.add(achievement);
                }

                return achievements;
            }
        }
    }
}
