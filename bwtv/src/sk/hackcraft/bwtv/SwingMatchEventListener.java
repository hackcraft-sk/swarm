package sk.hackcraft.bwtv;

import javax.swing.SwingUtilities;

public class SwingMatchEventListener implements MatchEventListener
{
	private final MatchEventListener listener;
	
	public SwingMatchEventListener(MatchEventListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void onEvent(final EventInfo eventInfo)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				listener.onEvent(eventInfo);
			}
		});
	}
}
