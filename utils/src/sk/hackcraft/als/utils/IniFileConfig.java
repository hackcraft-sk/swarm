package sk.hackcraft.als.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class IniFileConfig
{
	public MemoryConfig load(File iniFile) throws IOException
	{
		MemoryConfig config = new MemoryConfig();
		
		String actualSection = null;

		try (FileReader fileReader = new FileReader(iniFile); BufferedReader reader = new BufferedReader(fileReader);)
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				// skip empty lines
				if (line.trim().length() == 0)
				{
					continue;
				}

				// skip comment lines
				if (line.charAt(0) == ';')
				{
					continue;
				}

				// found section
				if (line.charAt(0) == '[')
				{
					actualSection = line.substring(1, line.length() - 1);
					
					MemoryConfig.MemorySection section = new MemoryConfig.MemorySection(actualSection);
					config.addSection(section);
					continue;
				}

				// parsing key value pairs
				String[] tokens = line.split("=");

				String key = tokens[0].trim();

				String value;
				if (tokens.length > 1)
				{
					value = tokens[1].trim();
				}
				else
				{
					value = "";
				}

				MemoryConfig.MemoryPair pair = new MemoryConfig.MemoryPair(key, value);
				config.getSection(actualSection).addPair(pair);
			}
		}

		return config;
	}
	
	public void save(Config config, String destinationPath) throws IOException
	{
		File outputFile = new File(destinationPath);

		try
		(
			FileWriter fileWriter = new FileWriter(outputFile);
			BufferedWriter writer = new BufferedWriter(fileWriter);
		)
		{
			boolean firstSection = true;

			for (Config.Section section : config.getAllSections())
			{
				if (firstSection)
				{
					firstSection = false;
				}
				else
				{
					writer.newLine();
				}

				writer.write("[" + section.getName() +"]");
				writer.newLine();

				for (Config.Pair pair : section.getAllPairs())
				{
					writer.write(pair.getKey() + "=" + pair.getStringValue());
					writer.newLine();
				}
			}
		}
	}
}
