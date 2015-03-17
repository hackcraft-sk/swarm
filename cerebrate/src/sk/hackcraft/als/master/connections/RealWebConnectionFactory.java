package sk.hackcraft.als.master.connections;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.utils.Config;

public class RealWebConnectionFactory implements WebConnectionFactory {

	private Config config;
	
	public RealWebConnectionFactory(Config config) {
		this.config = config;
	}
	
	@Override
	public WebConnection createWebConnection() {
		try
		{
			String address = config.getSection("webConnection").getPair("address").getStringValue();

			String rawAcceptingTournamentIds[] = config.getSection("tournament").getPair("acceptingIds").getStringValueAsArray();
			Set<Integer> acceptingTournamentIds = new HashSet<>();
			for (String rawId : rawAcceptingTournamentIds)
			{
				int id = Integer.parseInt(rawId);
				acceptingTournamentIds.add(id);
			}

			String rawStreamIds[] = config.getSection("tournament").getPair("videoStreams").getStringValueAsArray();
			Set<Integer> streamIds = new HashSet<>();
			for (String rawId : rawStreamIds)
			{
				int id = Integer.parseInt(rawId);
				streamIds.add(id);
			}

			return new RealWebConnection(address, acceptingTournamentIds, streamIds);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Can't create connection to web.", e);
		}
	}

}
