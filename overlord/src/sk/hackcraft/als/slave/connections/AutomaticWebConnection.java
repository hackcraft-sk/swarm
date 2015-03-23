package sk.hackcraft.als.slave.connections;

import java.io.IOException;
import java.nio.file.Path;

import sk.hackcraft.als.slave.Bot;
import sk.hackcraft.als.slave.Bot.Type;
import sk.hackcraft.als.slave.download.BotFilePreparer;
import sk.hackcraft.als.slave.download.MapFilePreparer;

public class AutomaticWebConnection implements WebConnection {

	public static Bot [] createBotInfosFrom(String [] names, String [] urls, String [] types) {
		Bot [] bots = new Bot[urls.length];
		
		for (int i=0; i<bots.length; i++) {
			bots[i] = new Bot(i, names[i], Type.valueOf(types[i]), urls[i], null);
		}
		
		return bots;
	}
	
	private Bot [] botInfos;
	
	private BotFilePreparer botFilePreparer;
	private MapFilePreparer mapFilePreparer;
	
	public AutomaticWebConnection(Bot[] botInfos, MapFilePreparer mapFilePreparer) {
		this.mapFilePreparer = mapFilePreparer;
		botFilePreparer = new BotFilePreparer();
		
		this.botInfos = botInfos;
	}
	
	@Override
	public Bot getBotInfo(int botId) throws IOException {
		return botInfos[botId];
	}
	
	@Override
	public Path prepareBotFile(Bot bot) throws IOException {
		return botFilePreparer.prepareBotCode(bot);
	}

	@Override
	public String prepareMapFile(String mapUrl) throws IOException {
		return mapFilePreparer.prepareMap(mapUrl);
	}
}
