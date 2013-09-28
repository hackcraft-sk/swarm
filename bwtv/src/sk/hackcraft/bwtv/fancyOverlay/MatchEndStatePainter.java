package sk.hackcraft.bwtv.fancyOverlay;

import java.awt.Component;
import java.awt.Graphics2D;

import sk.hackcraft.bwtv.connections.WebConnection;
import sk.hackcraft.bwtv.swing.ScreenPainterBase;
import sk.hackcraft.bwtv.utils.BannerPicker;

public class MatchEndStatePainter extends ScreenPainterBase
{
	/*private String matchResult;
	private String winner;
	private String looser;
	
	private WebConnection webConnection;*/
	
	public MatchEndStatePainter(WebConnection webConnection, BannerPicker bannerPicker)
	{
		/*matchResult = "INVALID";
		
		winner = "Moergil";
		looser = "Epholl";
		
		this.webConnection = webConnection;*/
	}

	@Override
	public void paint(Component component, Graphics2D g2d)
	{
		// TODO Auto-generated method stub
		
	}
	
	/*@Override
	public void reload(int matchId)
	{
		setError(false);
		
		try
		{
			MatchInfo matchInfo = null;
			MatchInfo.MatchState state = null;
			
			int tries = 100;
			while (tries > 0)
			{
				matchInfo = webConnection.getMatchInfo(matchId);
				
				state = matchInfo.getState();
				
				if (state.equals("NONE"))
				{
					tries--;
					
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException ie)
					{
					}
					
					System.out.println(".");
					continue;
				}
				else
				{
					break;
				}
			}

			if (state == MatchInfo.MatchState.OK)
			{
				List<MatchInfo.Bot> bots = matchInfo.getBots();
				
				for (int i = 0; i < bots.size(); i++)
				{
					MatchInfo.Bot bot = bots.get(i);
	
					MatchResult botResult = bot.getResult();
					
					if (botResult == MatchResult.WIN || botResult == MatchResult.PARTIAL_WIN)
					{
						winner = bot.getName();
						matchResult = botResult.toString();
						
						int secondBotIndex = (i == 0) ? 1 : 0;
						
						MatchInfo.Bot secondBot = bots.get(secondBotIndex);
						looser = secondBot.getName();
					}
					else if (botResult == MatchResult.DRAW)
					{
						matchResult = "DRAW";
					}
				}
			}
			else
			{
				matchResult = "INVALID";
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			setError(true);
		}
	}*/

	/*@Override
	public void paint(Component component, Graphics2D g2d)
	{
		if (hasError())
		{
			drawError(g2d);
			return;
		}
		
		g2d.setColor(Color.BLACK);
		g2d.fillRect(100, 170, 460, 120);
		
		g2d.setColor(Color.WHITE);

		switch (matchResult)
		{
			case "WIN":
				paintMatchResult(g2d, winner + " wins!");
				paintPoints(g2d, winner + " +3pts");
				break;
			case "PARTIAL_WIN":
				paintMatchResult(g2d, winner + " partially wins!");
				paintPoints(g2d, winner + " +2pts, " + looser + " +1pts");
				break;
			case "DRAW":
				paintMatchResult(g2d, "Draw!");
				paintPoints(g2d, "+1pts to both");
				break;
			case "INVALID":
				paintMatchResult(g2d, "Error!");
				paintPoints(g2d, "Match will be replayed later.");
				break;
			default:
				throw new IllegalArgumentException("Wrong match result: " + matchResult);
		}
	}
	
	private void paintMatchResult(Graphics2D g2d, String text)
	{
		paintCenteredText(g2d, text, 220, 30);
	}
	
	private void paintPoints(Graphics2D g2d, String text)
	{
		paintCenteredText(g2d, text, 250, 20);
	}*/
}
