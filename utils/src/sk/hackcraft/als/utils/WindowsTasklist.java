package sk.hackcraft.als.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WindowsTasklist implements ProcessesList
{
	private final Map<String, Integer> processes;

	public WindowsTasklist() throws IOException
	{
		processes = new HashMap<>();

		Runtime runtime = Runtime.getRuntime();
		Process ps = runtime.exec("tasklist");

		BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null)
		{
			List<String> words = tokenizeLine(line);

			// no empty lines
			if (words.size() == 0)
			{
				continue;
			}

			// only valid process names
			if (!words.get(0).contains(".exe"))
			{
				continue;
			}

			try
			{
				String processName = words.get(0);
				int pid = Integer.parseInt(words.get(1));

				processes.put(processName, pid);
			}
			catch (NumberFormatException e)
			{
				continue;
			}
		}
	}

	private List<String> tokenizeLine(String line)
	{
		List<String> words = new LinkedList<>();

		boolean insideWord = false;
		int wordStart = -1;
		for (int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);

			// word start
			if (c != ' ' && !insideWord)
			{
				wordStart = i;
				insideWord = true;
			}

			// word end
			if (insideWord && c == ' ' || i + 1 == line.length())
			{
				insideWord = false;

				int wordEnd = (i + 1 == line.length()) ? i + 1 : i;

				String word = line.substring(wordStart, wordEnd);
				words.add(word);
			}
		}

		return words;
	}

	@Override
	public boolean has(String processName)
	{
		return processes.containsKey(processName);
	}

	@Override
	public boolean has(int pid)
	{
		// TODO trosku hack, opravit casom
		return processes.containsValue(pid);
	}

	@Override
	public int getPid(String processName)
	{
		return processes.get(processName);
	}
}
