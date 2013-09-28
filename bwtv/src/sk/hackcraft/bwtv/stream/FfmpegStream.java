package sk.hackcraft.bwtv.stream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import sk.hackcraft.als.utils.MatchEvent;
import sk.hackcraft.bwtv.EventInfo;
import sk.hackcraft.bwtv.MatchEventBroadcaster;
import sk.hackcraft.bwtv.MatchEventListener;
import sk.hackcraft.bwtv.SwingMatchEventListener;

public class FfmpegStream implements Stream
{
	private static final Logger logger = Logger.getLogger(FfmpegStream.class.getName());
	
	private final int RESTART_TIMEOUT = 60 * 60;
	
	private final MatchEventBroadcaster matchEventBroadcaster;
	private final MatchEventListener matchEventListener;
	
	private final int x, y;
	
	private final long startTime;
	private long partStartTime;
	
	private ProcessBuilder streamProcessBuilder;
	private Process streamProcess;
	
	private boolean stopped = false;
	private boolean running;
	
	private final OutputStream outputStream;

	public FfmpegStream(MatchEventBroadcaster matchEventBroadcaster, int x, int y) throws IOException
	{
		this(matchEventBroadcaster, x, y, new OutputStream()
		{
			@Override
			public void write(int b) throws IOException
			{
			}
		});
	}
	
	public FfmpegStream(MatchEventBroadcaster matchEventBroadcaster, int x, int y, OutputStream outputStream) throws IOException
	{
		this.matchEventBroadcaster = matchEventBroadcaster;
		
		this.x = x;
		this.y = y;
		
		this.outputStream = outputStream;
		
		startStream();
		
		running = true;
		
		startTime = System.currentTimeMillis();
		
		matchEventListener = new SwingMatchEventListener(new MatchEventListener()
		{
			@Override
			public void onEvent(EventInfo eventInfo)
			{
				FfmpegStream.this.onEvent(eventInfo);
			}
		});
		matchEventBroadcaster.registerListener(matchEventListener);
	}
	
	@Override
	public void stop()
	{
		stopStream();
		
		stopped = true;
		
		matchEventBroadcaster.unregisterListener(matchEventListener);
	}
	
	@Override
	public boolean isStopped()
	{
		return stopped;
	}

	@Override
	public int getRunningTime()
	{
		return (int)(System.currentTimeMillis() - startTime) / 1000;
	}
	
	private int getPartRunningTime()
	{
		return (int)(System.currentTimeMillis() - partStartTime) / 1000;
	}

	private void onEvent(EventInfo stateInfo)
	{
		if (stopped)
		{
			return;
		}
		
		MatchEvent matchEvent = stateInfo.getEvent();
		boolean restartStream = shouldRestartStream(matchEvent);
		
		if (restartStream)
		{
			try
			{
				logger.info("Restarting stream...");
				
				restartStream();
				
				running = true;
			}
			catch (IOException e)
			{
				logger.log(Level.SEVERE, "Can't restart stream", e);
				
				running = false;
			}
		}
	}
	
	private boolean shouldRestartStream(MatchEvent matchEvent)
	{
		if (!running)
		{
			return true;
		}
		
		int partRunningTime = getPartRunningTime();
		if (partRunningTime > RESTART_TIMEOUT && matchEvent == MatchEvent.PREPARE)
		{
			return true;
		}
		
		return false;
	}
	
	private void startStream() throws IOException
	{
		streamProcessBuilder = new ProcessBuilder("bash", "stream.sh", Integer.toString(x), Integer.toString(y));
		streamProcessBuilder
		.directory(new File("./stream"))
		.redirectErrorStream(true);
		
		streamProcess = streamProcessBuilder.start();
		
		if (outputStream != null)
		{
			InputStream inputStream = streamProcess.getInputStream();
			new StreamRedirectDaemon(inputStream, outputStream).start();
		}

		partStartTime = System.currentTimeMillis();
		
		logger.info("Stream started");
	}
	
	private void stopStream()
	{
		streamProcess.destroy();
		
		logger.info("Stream stopped");
	}
	
	private void restartStream() throws IOException
	{
		stopStream();
		startStream();
	}
	
	private class StreamRedirectDaemon extends Thread
	{
		private final InputStream is;
		private final OutputStream os;
		
		public StreamRedirectDaemon(InputStream is, OutputStream os)
		{
			this.is = is;
			this.os = os;
			
			setDaemon(true);
		}
		
		@Override
		public void run()
		{
			try
			{
				byte[] buffer = new byte[1024];
				int len = is.read(buffer);
				while (len != -1)
				{
					os.write(buffer, 0, len);
				    len = is.read(buffer);
				}    
			}
			catch (IOException e)
			{
			}
		}
	}
}
