package sk.hackcraft.als.utils;

import java.io.IOException;

public class ProcessStarter
{
	private String command;
	
	public ProcessStarter(String command)
	{
		this.command = command;
	}
	
	public void start() throws IOException
	{
		Runtime.getRuntime().exec(command);
	}
}