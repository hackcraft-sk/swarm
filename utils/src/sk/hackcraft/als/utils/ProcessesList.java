package sk.hackcraft.als.utils;

public interface ProcessesList
{
	public boolean has(String processName);
	public boolean has(int pid);
	public int getPid(String processName);
}
