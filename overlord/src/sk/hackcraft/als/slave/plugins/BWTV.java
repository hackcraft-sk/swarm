package sk.hackcraft.als.slave.plugins;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import sk.hackcraft.als.utils.MatchEvent;

public class BWTV
{
	private volatile Socket socket;
	private volatile DataOutputStream output;

	private int matchId = 0;

	public BWTV()
	{
		runNewConnectionDaemon();
	}

	public void setMatchId(int matchId)
	{
		this.matchId = matchId;
	}

	public void sendEvent(MatchEvent matchEvent)
	{
		if (socket == null)
		{
			return;
		}

		try
		{
			System.out.println("Broadcasting " + matchEvent + " to BWTV.");

			output.writeInt(matchEvent.toValue());
			output.writeInt(matchId);
		}
		catch (IOException e)
		{
			System.out.println("BWTV error: " + e.getMessage());

			if (socket != null)
			{
				forceSocketClose();
				socket = null;

				runNewConnectionDaemon();
			}
		}
	}

	public void close()
	{
		if (socket != null)
		{
			forceSocketClose();
		}
	}

	private void forceSocketClose()
	{
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
		}
	}

	private void runNewConnectionDaemon()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				try (ServerSocket serverSocket = new ServerSocket(12987);)
				{
					System.out.println("Waiting for BWTV connect...");

					socket = serverSocket.accept();
					output = new DataOutputStream(socket.getOutputStream());
				}
				catch (IOException e)
				{
					e.printStackTrace();

					try
					{
						Thread.sleep(10000);
					}
					catch (InterruptedException ie)
					{
						Thread.currentThread().interrupt();
					}

					runNewConnectionDaemon();
				}
			};
		}.start();
	}
}
