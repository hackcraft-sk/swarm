package sk.hackcraft.bwtv.fancyOverlay;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JFrame;

import sk.hackcraft.als.utils.MatchEvent;
import sk.hackcraft.bwtv.EventInfo;
import sk.hackcraft.bwtv.Overlay;
import sk.hackcraft.bwtv.connections.WebConnection;
import sk.hackcraft.bwtv.swing.ScreenPainter;
import sk.hackcraft.bwtv.utils.BannerPicker;

public class JFrameOverlay extends JFrame implements Overlay
{
	private static final long serialVersionUID = 1L;

	private Map<MatchEvent, ScreenPainter> painters;
	private ScreenPainter activePainter;

	public JFrameOverlay(WebConnection webConnection)
	{
		super("StarCraft Overlay Info Panel");

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		setUndecorated(true);
		setAlwaysOnTop(true);
		setBackground(new Color(0, 0, 0, 0));

		setLocationRelativeTo(null);
		
		painters = new EnumMap<>(MatchEvent.class);
		activePainter = new ScreenPainter()
		{
			@Override
			public void paint(Component component, Graphics2D g2d)
			{
			}
		};
		
		Dimension size = new Dimension(640, 480);
		setSize(size);
		setResizable(false);
		
		setBackground(new Color(0, 0, 0, 0));
		
		BannerPicker bannerPicker = new BannerPicker();
		
		try
		{
			bannerPicker.addBanner("robime-it");
			bannerPicker.addBanner("sscai");
			bannerPicker.addBanner("fri");
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		addStatePainter(MatchEvent.PREPARE, new MatchRunStatePainter());
		addStatePainter(MatchEvent.RUN, new MatchRunStatePainter());
		addStatePainter(MatchEvent.END, new MatchEndStatePainter(webConnection, bannerPicker));
	}
	
	public void addStatePainter(MatchEvent state, ScreenPainter statePainter)
	{
		painters.put(state,  statePainter);
	}
	
	public void setState(EventInfo stateInfo)
	{
		activePainter = painters.get(stateInfo.getEvent());
		
		/*try
		{
			//activePainter.reload(stateInfo.getMatchId());
		}
		catch (IOException e)
		{
			// TODO spravit painter pre error
			e.printStackTrace();
			activePainter = new ScreenPainter()
			{
				@Override
				public void paint(Component component, Graphics2D g2d)
				{
				}
			};
		}*/
		
		repaint();
	}
	
	@Override
	public void changeScreen(EventInfo eventInfo)
	{
		setState(eventInfo);
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.clearRect(0, 0, 640, 480);
		
		activePainter.paint(this, g2d);
		
		g2d.dispose();
		Toolkit.getDefaultToolkit().sync();
	}
}
