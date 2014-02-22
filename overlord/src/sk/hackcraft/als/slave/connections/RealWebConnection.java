package sk.hackcraft.als.slave.connections;

import java.io.IOException;
import java.nio.file.Path;

import org.json.JSONObject;

import sk.hackcraft.als.slave.Bot;
import sk.hackcraft.als.slave.Bot.Type;
import sk.hackcraft.als.slave.download.BotFilePreparer;
import sk.hackcraft.als.slave.download.MapFilePreparer;
import sk.hackcraft.jwebcomm.http.FormRequestFactory;
import sk.hackcraft.jwebcomm.json.JsonResponseException;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequest;
import sk.hackcraft.jwebcomm.json.SimpleJsonRequestFactory;

public class RealWebConnection implements WebConnection
{
	private SimpleJsonRequestFactory requestFactory;
	private BotFilePreparer botFilePreparer;
	private MapFilePreparer mapFilePreparer;

	public RealWebConnection(String url, MapFilePreparer mapFilePreparer) throws IOException
	{
		FormRequestFactory rawRequestFactory = new FormRequestFactory(url);
		this.requestFactory = new SimpleJsonRequestFactory(rawRequestFactory);

		botFilePreparer = new BotFilePreparer();
		this.mapFilePreparer = mapFilePreparer;
	}

	@Override
	public Bot getBotInfo(int botId) throws IOException
	{
		JSONObject requestData = new JSONObject();
		requestData.put("id", botId);

		SimpleJsonRequest request = requestFactory.createGetRequest("json/bot-info", requestData);

		JSONObject response = request.send();

		if (response.has("error"))
		{
			throw JsonResponseException.createFromJson(response);
		}

		String name = response.getString("name");
		Type codeType = Type.valueOf(response.getString("type"));
		String botFileUrl = response.getString("botFileUrl");
		String botFileHash = response.getString("botFileHash");

		return new Bot(botId, name, codeType, botFileUrl, botFileHash);
	}

	@Override
	public Path prepareBotFile(Bot bot) throws IOException
	{
		return botFilePreparer.prepareBotCode(bot);
	}

	@Override
	public String prepareMapFile(String mapUrl) throws IOException
	{
		return mapFilePreparer.prepareMap(mapUrl);
	}
}
