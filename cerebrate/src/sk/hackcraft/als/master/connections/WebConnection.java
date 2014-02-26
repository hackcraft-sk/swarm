package sk.hackcraft.als.master.connections;

import java.io.IOException;

import sk.hackcraft.als.master.MatchInfo;
import sk.hackcraft.als.master.MatchReport;
import sk.hackcraft.als.master.ReplaysStorage;

public interface WebConnection
{
	public MatchInfo requestMatch() throws IOException;

	public void postMatchResult(MatchReport matchResult, ReplaysStorage replaysStorage) throws IOException;
}
