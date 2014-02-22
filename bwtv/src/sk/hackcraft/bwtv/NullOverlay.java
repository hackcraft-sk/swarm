package sk.hackcraft.bwtv;

public class NullOverlay implements Overlay
{
	private boolean visible;

	@Override
	public void changeScreen(EventInfo stateInfo)
	{
	}

	@Override
	public void setLocation(int x, int y)
	{
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void dispose()
	{
	}
}
