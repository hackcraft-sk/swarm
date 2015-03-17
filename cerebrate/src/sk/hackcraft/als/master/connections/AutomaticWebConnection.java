package sk.hackcraft.als.master.connections;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import sk.hackcraft.als.master.MatchInfo;
import sk.hackcraft.als.master.MatchReport;
import sk.hackcraft.als.master.ReplaysStorage;

public class AutomaticWebConnection implements WebConnection {

	private int nextMatchId = 1;
	
	private Set<Integer> botIds = new HashSet<Integer>();
	
	private String mapUrl;
	
	public AutomaticWebConnection(int[] botIds, String mapUrl) {
		this.mapUrl = mapUrl;
		
		for(int id : botIds) {
			this.botIds.add(id);
		}
	}
	
	@Override
	public MatchInfo requestMatch() throws IOException {
		return new MatchInfo(nextMatchId++, mapUrl, botIds);
	}

	@Override
	public void postMatchResult(MatchReport matchResult, ReplaysStorage replaysStorage) throws IOException {
		// TODO So far doing nothing, should be perfectly fine...
	}

}
