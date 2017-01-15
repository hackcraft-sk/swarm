package sk.hackcraft.als.master.connection.definition;

import sk.hackcraft.als.master.model.MatchReport;
import sk.hackcraft.als.master.model.MatchSpecification;
import sk.hackcraft.als.utils.MemoryConfig;
import sk.hackcraft.als.utils.components.Component;
import sk.hackcraft.als.utils.model.BotInfo;

import java.io.IOException;

public interface WebConnection extends Component {

    MatchSpecification requestMatch() throws IOException;

    void postMatchReport(MatchReport matchReport) throws IOException;

    BotInfo getBotInfo(int botId) throws IOException;

    byte[] downloadMap(String url, String hash) throws IOException;

    byte[] downloadBot(String url, String hash) throws IOException;
}
