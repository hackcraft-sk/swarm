package sk.hackcraft.als.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public interface Config
{
	boolean hasSection(String section);

	Section getSection(String section);

	Set<Section> getSections();

	String get(String section, String name);

	String[] getArray(String section, String name);

	interface Section
	{
		String getName();
		boolean has(String key);
		String get(String key);
		String[] getArray(String key);
		Set<Pair> getPairs();
	}

	interface Pair
	{
		String getKey();
		String getValue();
		String[] getValueAsArray();
	}
}
