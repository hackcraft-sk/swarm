package sk.hackcraft.bwtv;

public interface Overlay
{
	public void changeScreen(EventInfo eventInfo);
	
	public void setLocation(int x, int y);
	
	public void setVisible(boolean visible);
	public boolean isVisible();
	
	public void dispose();
}
