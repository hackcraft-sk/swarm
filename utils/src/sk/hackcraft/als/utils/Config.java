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

public class Config
{	
	private Map<String, Section> sections;
	
	public Config()
	{
		sections = new TreeMap<>();
	}
	
	private Config(IniFileParser parser)
	{
		this();
		
		for (Map.Entry<String, Map<String, String>> entry : parser.structure.entrySet())
		{
			String sectionName = entry.getKey();
			Section section = new Section(sectionName, entry.getValue());
			
			sections.put(sectionName, section);
		}
	}
	
	public boolean hasSection(String section)
	{
		return sections.containsKey(section);
	}
	
	public Section getSection(String section)
	{
		return sections.get(section);
	}
	
	public Set<Section> getSections()
	{
		return new TreeSet<>(sections.values());
	}
	
	public String get(String section, String name)
	{
		return getSection(section).get(name);
	}
	
	public String[] getArray(String section, String name)
	{
		return getSection(section).getArray(name);
	}
	
	public Editor edit()
	{
		return new Editor();
	}
	
	public class Section implements Comparable<Section>
	{
		private String name;
		private final Map<String, Pair> values;
		
		public Section(String name)
		{
			this.name = name;
			this.values = new HashMap<>();
		}
		
		public Section(String name, Map<String, String> values)
		{
			this(name);
			
			for (Map.Entry<String, String> entry : values.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();
				
				this.values.put(key, new Pair(key, value));
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
		
		public String[] getArray(String key)
		{
			return values.get(key).getValueAsArray();
		}
		
		public Set<Pair> getPairs()
		{
			return new TreeSet<>(values.values());
		}

		@Override
		public int compareTo(Section section)
		{
			return name.compareTo(section.getName());
		}
	}
	
	public class Pair implements Comparable<Pair>
	{
		private String key;
		private String value;
		
		public Pair(String key, String value)
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
		public int compareTo(Pair pair)
		{
			return key.compareTo(pair.getKey());
		}
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
		
		public Config create() throws IOException
		{
			String actualSection = null;
			
			try
			(
				FileReader fileReader = new FileReader(iniFile);
				BufferedReader reader = new BufferedReader(fileReader);
			)
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
						structure.put(actualSection, new TreeMap<String, String>());
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
			
			return new Config(this);
		}
	}
	
	public class Editor
	{
		public void set(String sectionName, String key, String newValue)
		{
			EditableSection editableSection = getSection(sectionName);
			editableSection.set(key, newValue);
		}
		
		public EditableSection getSection(String sectionName)
		{
			if (!sections.containsKey(sectionName))
			{
				sections.put(sectionName, new Section(sectionName));
			}
			
			return new EditableSection(sections.get(sectionName));
		}
		
		public void saveAsIniFile(String pathname) throws IOException
		{
			File outputFile = new File(pathname);
			
			try
			(
				FileWriter fileWriter = new FileWriter(outputFile);
				BufferedWriter writer = new BufferedWriter(fileWriter);
			)
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
					
					writer.write("[" + sectionName +"]");
					writer.newLine();
					
					for (Pair pair : sections.get(sectionName).getPairs())
					{
						writer.write(pair.getKey() + "=" + pair.getValue());
						writer.newLine();
					}
				}
			}
		}
		
		public class EditableSection
		{
			private Section section;
			
			public EditableSection(Section section)
			{
				this.section = section;
			}
			
			public void set(String key, String newValue)
			{
				section.values.put(key, new Pair(key, newValue));
			}
		}
	}
}
