package sk.hackcraft.bwtv;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

import sk.hackcraft.als.utils.Config;
import sk.hackcraft.bwtv.connections.MockSlaveConnection;
import sk.hackcraft.bwtv.connections.MockWebConnection;
import sk.hackcraft.bwtv.connections.RealSlaveConnection;
import sk.hackcraft.bwtv.connections.RealWebConnection;
import sk.hackcraft.bwtv.connections.SlaveConnection;
import sk.hackcraft.bwtv.connections.SlaveConnectionFactory;
import sk.hackcraft.bwtv.connections.WebConnection;
import sk.hackcraft.bwtv.control.Remote;
import sk.hackcraft.bwtv.crudeOverlay.CrudeOverlay;
import sk.hackcraft.bwtv.stream.FfmpegStream;
import sk.hackcraft.bwtv.stream.Stream;
import sk.hackcraft.bwtv.stream.StreamFactory;

public class Application
{
	public static void main(String[] args)
	{
		new Application();
	}

	public Application()
	{		
		File configFile = new File("bwtv.cfg");
		
		if (!configFile.exists())
		{
			throw new RuntimeException("bwtv.cfg is missing.");
		}
		
		Config.IniFileParser iniFileParser = new Config.IniFileParser(configFile);
		
		Config settings;
		try
		{
			settings = iniFileParser.create();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Can't create config from file.", e);
		}
		
		Config.Section componentsSection = settings.getSection("components");
		final boolean slaveMock = componentsSection.get("slaveConnection").equals("mock");
		final boolean webMock = componentsSection.get("webConnection").equals("mock");
		
		String webAddress = settings.get("web", "address");
		String rawSlaveAddress = settings.get("slave", "address");

		final WebConnection webConnection;
		if (webMock)
		{
			webConnection = new MockWebConnection();
		}
		else
		{
			try
			{
				webConnection = new RealWebConnection(webAddress);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		final InetAddress slaveAddress;
		try
		{
			slaveAddress = InetAddress.getByName(rawSlaveAddress);
		}
		catch (UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
		
		final SlaveConnection slaveConnection;
		if (slaveMock)
		{
			slaveConnection = new MockSlaveConnection();
		}
		else
		{
			slaveConnection = new RealSlaveConnection(slaveAddress);
		}
		
		SlaveConnectionFactory slaveConnectionFactory = new SlaveConnectionFactory()
		{
			@Override
			public SlaveConnection create()
			{				
				return slaveConnection;
			}
		};
		
		final MatchEventBroadcaster matchEventBroadcaster = new MatchEventBroadcaster(slaveConnectionFactory);

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				StreamFactory streamFactory = new StreamFactory()
				{
					@Override
					public Stream create(int x, int y) throws IOException
					{
						FfmpegStream stream = new FfmpegStream(matchEventBroadcaster, x, y);
						
						return stream;
					}
				};
				
				final Overlay overlay = new CrudeOverlay(webConnection, matchEventBroadcaster);
				
				new Remote(streamFactory, overlay);
			}
		});
	}
}
