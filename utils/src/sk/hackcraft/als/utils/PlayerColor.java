package sk.hackcraft.als.utils;

public class PlayerColor
{
	private final String hexColor;
	
	public PlayerColor(String hexColor)
	{
		this.hexColor = hexColor;
	}
	
	public String getPlayerHexColor()
	{
		return hexColor;
	}
	
	@Override
	public String toString()
	{
		return "PlayerColor: " + hexColor;
	}
}
