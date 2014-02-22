package sk.hackcraft.als.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import sk.hackcraft.als.utils.Config.Pair;

public class IniFileConfig implements Config
{
	private Map<String, IniSection> sections;

	public IniFileConfig()
	{
		sections = new TreeMap<>();
	}

	private IniFileConfig(IniFileParser parser)
	{
		this();

		for (Map.Entry<String, Map<String, String>> entry : parser.structure
				.entrySet())
		{
			String sectionName = entry.getKey();
			IniSection section = new IniSection(sectionName, entry.getValue());

			sections.put(sectionName, section);
		}
	}

	@Override
	public boolean hasSection(String section)
	{
		return sections.containsKey(section);
	}

	@Override
	public Section getSection(String section)
	{
		return sections.get(section);
	}

	@Override
	public Set<Section> getSections()
	{
		return new TreeSet<Section>(sections.values());
	}

	@Override
	public String get(String section, String name)
	{
		return getSection(section).get(name);
	}

	@Override
	public String[] getArray(String section, String name)
	{
		return getSection(section).getArray(name);
	}

	public Editor edit()
	{
		return new Editor();
	}

	public static class IniFileParser
	{
		private final File iniFile;

		private final Map<String, Map<String, String>> structure;

		public IniFileParser(File iniFile)
		{
			this.iniFile = iniFile;
			structure = new TreeMap<>();
		}

		public IniFileConfig create() throws IOException
		{
			String actualSection = null;

			try (FileReader fileReader = new FileReader(iniFile);
					BufferedReader reader = new BufferedReader(fileReader);)
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					if (line.trim().length() == 0)
					{
						continue;
					}

					if (line.charAt(0) == ';')
					{
						continue;
					}

					if (line.charAt(0) == '[')
					{
						actualSection = line.substring(1, line.length() - 1);
						structure.put(actualSection,
								new TreeMap<String, String>());
						continue;
					}

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

					Map<String, String> section = structure.get(actualSection);
					section.put(key, value);
				}
			}

			return new IniFileConfig(this);
		}
	}
	
	public class IniSection implements Section, Comparable<IniSection>
	{
		private String name;
		protected final Map<String, IniPair> values;

		public IniSection(String name)
		{
			this.name = name;
			this.values = new HashMap<>();
		}

		public IniSection(String name, Map<String, String> values)
		{
			this(name);

			for (Map.Entry<String, String> entry : values.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();

				this.values.put(key, new IniPair(key, value));
			}
		}

		public String getName()
		{
			return name;
		}

		public boolean has(String key)
		{
			return values.containsKey(key);
		}

		public String get(String key)
		{
			return values.get(key).getValue();
		}
		
		public void set(String key, String newValue)
		{
			values.put(key, new IniPair(key, newValue));
		}

		public String[] getArray(String key)
		{
			return values.get(key).getValueAsArray();
		}

		public Set<Pair> getPairs()
		{
			return new TreeSet<Pair>(values.values());
		}

		@Override
		public int compareTo(IniSection section)
		{
			return name.compareTo(section.getName());
		}
	}

	public class IniPair implements Pair, Comparable<IniPair>
	{
		private String key;
		private String value;

		public IniPair(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		public String getKey()
		{
			return key;
		}

		public String getValue()
		{
			return value;
		}

		public String[] getValueAsArray()
		{
			if (value.length() == 0)
			{
				return new String[0];
			}

			String items[] = value.split(",");

			for (int i = 0; i < items.length; i++)
			{
				items[i] = items[i].trim();
			}

			return items;
		}

		@Override
		public int compareTo(IniPair pair)
		{
			return key.compareTo(pair.getKey());
		}
	}

	public class Editor
	{
		public void set(String sectionName, String key, String newValue)
		{
			IniSection editableSection = getIniSection(sectionName);
			editableSection.set(key, newValue);
		}

		public IniSection getIniSection(String sectionName)
		{
			if (!sections.containsKey(sectionName))
			{
				sections.put(sectionName, new IniSection(sectionName));
			}

			return sections.get(sectionName);
		}

		public void saveAsIniFile(String pathname) throws IOException
		{
			File outputFile = new File(pathname);

			try (FileWriter fileWriter = new FileWriter(outputFile);
					BufferedWriter writer = new BufferedWriter(fileWriter);)
			{
				boolean firstSection = true;

				for (String sectionName : sections.keySet())
				{
					if (firstSection)
					{
						firstSection = false;
					}
					else
					{
						writer.newLine();
					}

					writer.write("[" + sectionName + "]");
					writer.newLine();

					for (Pair pair : sections.get(sectionName).getPairs())
					{
						writer.write(pair.getKey() + "=" + pair.getValue());
						writer.newLine();
					}
				}
			}
		}
	}
}
