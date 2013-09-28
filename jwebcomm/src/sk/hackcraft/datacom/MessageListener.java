package sk.hackcraft.datacom;

public interface MessageListener
{
	public void onMessageReceived(byte content[]);
}
