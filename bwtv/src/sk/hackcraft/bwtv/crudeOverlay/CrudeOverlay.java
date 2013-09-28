package sk.hackcraft.bwtv.crudeOverlay;

import java.awt.Container;
import java.awt.Point;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import sk.hackcraft.bwtv.EventInfo;
import sk.hackcraft.bwtv.MatchEventBroadcaster;
import sk.hackcraft.bwtv.MatchEventListener;
import sk.hackcraft.bwtv.Overlay;
import sk.hackcraft.bwtv.SwingMatchEventListener;
import sk.hackcraft.bwtv.connections.WebConnection;

public class CrudeOverlay implements Overlay
{
	private static final Logger logger = Logger.getLogger(CrudeOverlay.class.getName());
	
	private MatchEventBroadcaster matchEventBroadcaster;
	private final MatchEventListener matchEventListener;
	
	private final JFrame frame;
	
	private Point pivot;
	private Point offset;
	
	private final InfoPanelFactory infoPanelFactory;
	
	public CrudeOverlay(WebConnection webConnection, MatchEventBroadcaster matchEventBroadcaster)
	{
		this.matchEventBroadcaster = matchEventBroadcaster;
		
		frame = new JFrame("BWTV Overlay");
		
		frame.setUndecorated(true);
		frame.setFocusable(false);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		pivot = new Point();
		offset = new Point();
		
		infoPanelFactory = new InfoPanelFactory(webConnection);
		
		matchEventListener = new SwingMatchEventListener(new MatchEventListener()
		{
			@Override
			public void onEvent(EventInfo eventInfo)
			{
				changeScreen(eventInfo);
			}
		});
		matchEventBroadcaster.registerListener(matchEventListener);
	}
	
	@Override
	public void changeScreen(EventInfo eventInfo)
	{
		Container contentPane = frame.getContentPane();
		contentPane.removeAll();

		final boolean showInfoPanel = infoPanelFactory.hasInfoPanelForEvent(eventInfo);
		
		if (showInfoPanel)
		{
			Container infoPanel;
			try
			{
				infoPanel = infoPanelFactory.createInfoPanel(eventInfo);
			}
			catch (IOException e)
			{
				logger.log(Level.WARNING, "Getting match info error", e);

				infoPanel = infoPanelFactory.createErrorInfoPanel();
			}
	
			contentPane.add(infoPanel);
		}
		
		frame.pack();
		
		if (showInfoPanel)
		{
			int frameWidth = frame.getWidth();
			int frameHeight = frame.getHeight();
			
			int offsetX = (640 - frameWidth) / 2;
			int offsetY = (480 - frameHeight) / 2;
			
			offset = new Point(offsetX, offsetY);
			resetInnerLocation();
		}
		else
		{
			offset = new Point(10000, 10000);
			resetInnerLocation();
		}
		
		frame.pack();
		
		frame.setAlwaysOnTop(true);
	}
	
	@Override
	public void setLocation(int x, int y)
	{
		pivot = new Point(x, y);
		resetInnerLocation();
	}
	
	private void resetInnerLocation()
	{
		int newX = (int)(pivot.getX() + offset.getX());
		int newY = (int)(pivot.getY() + offset.getY());
		
		Point newLocation = new Point(newX, newY);
		frame.setLocation(newLocation);
	}

	@Override
	public void setVisible(boolean visible)
	{
		frame.setVisible(visible);
	}

	@Override
	public boolean isVisible()
	{
		return frame.isVisible();
	}
	
	@Override
	public void dispose()
	{
		frame.dispose();
		
		matchEventBroadcaster.unregisterListener(matchEventListener);
	}
}
