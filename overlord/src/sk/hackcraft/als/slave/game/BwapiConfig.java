package sk.hackcraft.als.slave.game;

import java.io.File;
import java.io.IOException;

import sk.hackcraft.als.utils.IniFileConfig;

public class BwapiConfig
{
	private final String iniFilePath;
	private final IniFileConfig ini;
	
	public BwapiConfig(String starCraftPath) throws IOException
	{
		iniFilePath = starCraftPath + "/bwapi-data/bwapi.ini";
		File iniFile = new File(iniFilePath);
		IniFileConfig.IniFileParser parser = new IniFileConfig.IniFileParser(iniFile);
		this.ini = parser.create();
	}
	
	public String getMap()
	{
		return ini.get("auto_menu", "map");
	}
	
	public String getReplay()
	{
		return ini.get("config", "save_replay");
	}
	
	public Editor edit()
	{
		return new Editor(ini.edit());
	}

	public class Editor
	{
		private final IniFileConfig.Editor configEditor;
		
		public Editor(IniFileConfig.Editor configEditor)
		{
			this.configEditor = configEditor;
		}
		
		public void setMap(String mapRelativePath)
		{
			configEditor.set("auto_menu", "map", mapRelativePath);
		}
		
		public void setReplay(String replayName)
		{
			String replayGamePath = "maps\\replays\\" + replayName;
			configEditor.set("auto_menu", "save_replay", replayGamePath);
		}
		
		public void save() throws IOException
		{
			configEditor.saveAsIniFile(iniFilePath);
		}
	}
}
