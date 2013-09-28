package sk.hackcraft.bwtv.crudeOverlay;

import java.awt.Container;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;

import sk.hackcraft.als.utils.MatchEvent;
import sk.hackcraft.als.utils.MatchResult;
import sk.hackcraft.bwtv.EventInfo;
import sk.hackcraft.bwtv.MatchInfo;
import sk.hackcraft.bwtv.MatchInfo.MatchState;
import sk.hackcraft.bwtv.MatchInfo.Player;
import sk.hackcraft.bwtv.connections.WebConnection;

public class InfoPanelFactory
{
	private final WebConnection webConnection;
	
	public InfoPanelFactory(WebConnection webConnection)
	{
		this.webConnection = webConnection;
	}
	
	public boolean hasInfoPanelForEvent(EventInfo eventInfo)
	{
		MatchEvent event = eventInfo.getEvent();
		
		switch (event)
		{
			case PREPARE:
			case END:
				return true;
			case RUN:
				return false;
			default:
				return false;
		}
	}
	
	public Container createInfoPanel(EventInfo eventInfo) throws IOException
	{
		MatchEvent event = eventInfo.getEvent();
		int matchId = eventInfo.getMatchId();
		
		switch (event)
		{
			case PREPARE:
			{
				MatchInfo matchInfo = webConnection.getMatchInfo(matchId);
				return createDuelMatchPrepareInfoPanel(matchInfo);
			}
			case RUN:
				return createNullInfoPanel();
			case END:
			{
				MatchInfo matchInfo = webConnection.waitForFinishedMatchInfo(matchId);
				return createDuelMatchFinishedInfoPanel(matchInfo);
			}
			default:
				return createErrorInfoPanel();
		}
	}

	public Container createErrorInfoPanel()
	{
		return new ErrorInfoPanel();
	}
	
	public Container createNullInfoPanel()
	{
		Container panel = new JComponent()
		{
			private static final long serialVersionUID = 3910236534061921810L;
		};
		
		return panel;
	}
	
	public Container createDuelMatchPrepareInfoPanel(MatchInfo matchInfo)
	{	
		List<Player> bots = matchInfo.getPlayers();
		
		if (bots.size() != 2)
		{
			throw new IllegalArgumentException("Duel Info Panel needs exactly 2 players.");
		}
		
		String player1 = bots.get(0).getName();
		String player2 = bots.get(1).getName();
		
		String tournamentName = matchInfo.getTournamentName();
		
		return new DuelMatchPrepareInfoPanel(player1, player2, tournamentName);
	}
	
	public Container createDuelMatchFinishedInfoPanel(MatchInfo matchInfo)
	{
		String resultText;
		String pointsText;
		
		MatchState state = matchInfo.getState();

		if (state == MatchInfo.MatchState.OK)
		{
			String winner = null;
			String looser = null;
			MatchResult matchResult = MatchResult.NONE;
			
			List<Player> players = matchInfo.getPlayers();
			
			if (players.size() != 2)
			{
				throw new RuntimeException("Duel Info Panel accepts only 2 players.");
			}

			for (int i = 0; i < players.size(); i++)
			{
				Player player = players.get(i);
				
				MatchResult result = player.getResult();
				
				if (result == MatchResult.WIN || result == MatchResult.PARTIAL_WIN)
				{
					winner = player.getName();
					matchResult = result;
					
					int secondPlayerIndex = (i == 0) ? 1 : 0;
					
					Player secondPlayer = players.get(secondPlayerIndex);
					looser = secondPlayer.getName();
				}
				else if (result == MatchResult.DRAW)
				{
					matchResult = MatchResult.DRAW;
				}
			}
			
			switch (matchResult)
			{
				case WIN:
					resultText = winner + " wins!";
					pointsText = winner + " +3pts";
					break;
				case PARTIAL_WIN:
					resultText = winner + " partially wins!";
					pointsText = winner + " +1pts, " + looser + " -1pts";
					break;
				case DRAW:
					resultText = "Draw!";
					pointsText = "-1pts to both";
					break;
				default:
					throw new IllegalArgumentException("Wrong match result: " + matchResult);
			}
		}
		else
		{
			resultText = "Error!";
			pointsText = "An error has occured.";
		}
		
		return new DuelMatchFinishedInfoPanel(resultText, pointsText);
	}
}
