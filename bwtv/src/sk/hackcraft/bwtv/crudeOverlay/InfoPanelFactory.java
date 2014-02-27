package sk.hackcraft.bwtv.crudeOverlay;

import java.awt.Container;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;

import sk.hackcraft.als.utils.MatchEvent;
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
		
		if (state == MatchState.INVALID)
		{
			resultText = "Match was invalid!";
			pointsText = "Technical problem, match will be replayed later.";
		}
		else
		{
			Player winnerPlayer = null;
			int highestPoints = 0;
			
			pointsText = "";
			
			boolean first = true;
			for (Player player : matchInfo.getPlayers())
			{
				if (player.getPoints() == highestPoints)
				{
					winnerPlayer = null;
				}
				else if (player.getPoints() > highestPoints)
				{
					winnerPlayer = player;
					highestPoints = player.getPoints();
				}
				
				if (!first)
				{
					pointsText += ", ";
				}
				else
				{
					first = false;
				}
				
				pointsText += String.format("%s %dpts", player.getName(), player.getPoints());
			}
			
			if (winnerPlayer != null)
			{
				resultText = "Winner: " + winnerPlayer.getName();
			}
			else
			{
				resultText = "Draw!";
			}
		}

		return new DuelMatchFinishedInfoPanel(resultText, pointsText);
	}
}
