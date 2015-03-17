package sk.hackcraft.als.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Config class which is hold in memory. It's possible to change sections and
 * pairs. This class is not thread safe; create more copies or use external
 * synchronization to access this class.
 */
public class MemoryConfig implements Config
{
	private Map<String, MemorySection> sections;

	/**
	 * Constructs new empty config.
	 */
	public MemoryConfig()
	{
		sections = new HashMap<>();
	}

	/**
	 * Constructs new config as deep copy of specified config.
	 * 
	 * @param config
	 *            specified config to copy
	 */
	public MemoryConfig(Config config)
	{
		for (Section section : config.getAllSections())
		{
			MemorySection memorySection = new MemorySection(section);
			sections.put(memorySection.getName(), memorySection);
		}
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

	/**
	 * Adds new section to this config.
	 * 
	 * @param section
	 *            section to add
	 */
	public void addSection(MemorySection section)
	{
		sections.put(section.getName(), section);
	}

	@Override
	public Set<MemorySection> getAllSections()
	{
		return new HashSet<>(sections.values());
	}

	/**
	 * Section which holds its content in memory.
	 */
	public static class MemorySection implements Section
	{
		private String name;
		protected final Map<String, MemoryPair> pairs;

		/**
		 * Constructs new empty section.
		 * 
		 * @param name
		 *            section name
		 */
		public MemorySection(String name)
		{
			this.name = name;
			this.pairs = new HashMap<>();
		}

		/**
		 * Constructs new section as deep copy of specified section.
		 * 
		 * @param section
		 *            section to copy
		 */
		public MemorySection(Section section)
		{
			this(section.getName());

			for (Pair pair : section.getAllPairs())
			{
				MemoryPair memoryPair = new MemoryPair(pair);
				addPair(memoryPair);
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

		/**
		 * Adds new pair to this section.
		 * 
		 * @param pair
		 *            new pair to add
		 */
		public void addPair(MemoryPair pair)
		{
			pairs.put(pair.getKey(), pair);
		}

		@Override
		public Set<MemoryPair> getAllPairs()
		{
			return new HashSet<>(pairs.values());
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder("[");

			boolean first = true;
			for (Pair pair : pairs.values())
			{
				if (!first)
				{
					builder.append(", ");
				}
				else
				{
					first = false;
				}

				builder.append(pair);
			}

			builder.append("]");

			return builder.toString();
		}
	}

	/**
	 * Config pair which is holdint its content in memory. Values are internally
	 * hold as strings.
	 */
	public static class MemoryPair implements Pair
	{
		private String key;
		private String value;

		/**
		 * Constructs new pair with specified key and value.
		 * 
		 * @param key
		 *            specified key
		 * @param value
		 *            specified value
		 */
		public MemoryPair(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		/**
		 * Constructs new pair as deep copy of specified pair.
		 * 
		 * @param pair
		 *            pair to copy
		 */
		public MemoryPair(Pair pair)
		{
			this(pair.getKey(), pair.getStringValue());
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

		/**
		 * Sets string value.
		 * 
		 * @param value
		 *            new value
		 */
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

		/**
		 * Sets int value.
		 * 
		 * @param value
		 *            new value
		 */
		public void setIntValue(int value)
		{
			this.value = Integer.toString(value);
		}
		
		public int[] getIntValueAsArray()
		{
			if (value.length() == 0)
			{
				return new int[0];
			}

			String parts[] = value.split(",");
			int[] items = new int[parts.length];

			for (int i = 0; i < items.length; i++)
			{
				items[i] = Integer.parseInt(parts[i].trim());
			}

			return items;
		}

		@Override
		public boolean getBooleanValue()
		{
			return Boolean.parseBoolean(value);
		}

		/**
		 * Sets boolean value.
		 * 
		 * @param value
		 *            new value
		 */
		public void setBooleanValue(boolean value)
		{
			this.value = Boolean.toString(value);
		}

		@Override
		public double getDoubleValue()
		{
			return Double.parseDouble(value);
		}

		/**
		 * Sets double value.
		 * 
		 * @param value
		 *            new value
		 */
		public void setDoubleValue(double value)
		{
			this.value = Double.toString(value);
		}

		@Override
		public String toString()
		{
			return key + "=" + value;
		}
	}
}
