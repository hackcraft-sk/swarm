package sk.hackcraft.als.slave.game;

import java.io.File;
import java.io.IOException;

import sk.hackcraft.als.utils.IniFileConfig;
import sk.hackcraft.als.utils.MemoryConfig;

public class BwapiConfig
{
	private final String iniFilePath;
	private final MemoryConfig config;
	
	public BwapiConfig(String starCraftPath) throws IOException
	{
		iniFilePath = starCraftPath + "/bwapi-data/bwapi.ini";
		File iniFile = new File(iniFilePath);
		
		IniFileConfig configLoader = new IniFileConfig();
		config = configLoader.load(iniFile);
	}
	
	public String getMap()
	{
		return config.getSection("auto_menu").getPair("map").getStringValue();
	}
	
	public String getReplay()
	{
		return config.getSection("config").getPair("save_replay").getStringValue();
	}
	
	public Editor edit()
	{
		return new Editor();
	}

	public class Editor
	{		
		public void setMap(String mapRelativePath)
		{
			config.getSection("auto_menu").getPair("map").setStringValue(mapRelativePath);
		}
		
		public void setReplay(String replayName)
		{
			String replayGamePath = "maps\\replays\\" + replayName;
			
			config.getSection("auto_menu").getPair("save_replay").setStringValue(replayGamePath);
		}
		
		public void save() throws IOException
		{
			IniFileConfig configSaver = new IniFileConfig();
			configSaver.save(config, iniFilePath);
		}
	}
}
