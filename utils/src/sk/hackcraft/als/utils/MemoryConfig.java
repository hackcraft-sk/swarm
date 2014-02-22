package sk.hackcraft.als.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MemoryConfig implements Config
{
	private Map<String, MemorySection> sections;

	public MemoryConfig()
	{
		sections = new HashMap<>();
	}

	@Override
	public boolean hasSection(String section)
	{
		return sections.containsKey(section);
	}

	@Override
	public MemorySection getSection(String section)
	{
		return sections.get(section);
	}
	
	public void addSection(MemorySection section)
	{
		sections.put(section.getName(), section);
	}

	@Override
	public Set<MemorySection> getAllSections()
	{
		return new HashSet<>(sections.values());
	}

	public static class MemorySection implements Section
	{
		private String name;
		protected final Map<String, MemoryPair> pairs;

		public MemorySection(String name)
		{
			this.name = name;
			this.pairs = new HashMap<>();
		}

		public MemorySection(String name, Map<String, String> values)
		{
			this(name);

			for (Map.Entry<String, String> entry : values.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();

				this.pairs.put(key, new MemoryPair(key, value));
			}
		}

		@Override
		public String getName()
		{
			return name;
		}
		
		@Override
		public boolean hasPair(String key)
		{
			return pairs.containsKey(key);
		}

		@Override
		public MemoryPair getPair(String key)
		{
			return pairs.get(key);
		}
		
		public void addPair(MemoryPair pair)
		{
			pairs.put(pair.getKey(), pair);
		}

		@Override
		public Set<MemoryPair> getAllPairs()
		{
			return new TreeSet<>(pairs.values());
		}
	}

	public static class MemoryPair implements Pair
	{
		private String key;
		private String value;

		public MemoryPair(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey()
		{
			return key;
		}

		@Override
		public String getStringValue()
		{
			return value;
		}
		
		public void setStringValue(String value)
		{
			this.value = value;
		}

		@Override
		public String[] getStringValueAsArray()
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
		public int getIntValue()
		{
			return Integer.parseInt(value);
		}
		
		public void setIntValue(int value)
		{
			this.value = Integer.toString(value);
		}
		
		@Override
		public boolean getBooleanValue()
		{
			return Boolean.parseBoolean(value);
		}
		
		public void setBooleanValue(boolean value)
		{
			this.value = Boolean.toString(value);
		}
		
		@Override
		public double getDoubleValue()
		{
			return Double.parseDouble(value);
		}
		
		public void setDoubleValue(double value)
		{
			this.value = Double.toString(value);
		}
	}
}
