package sk.hackcraft.als.utils.log;

import java.io.PrintStream;

/**
 * Logging class with custom output and custom format.
 */
public class PrintStreamLog implements Log
{
	private final PrintStream printer;
	private final Formatter formatter;

	/**
	 * Constructs new log which is outputting messages to desired output in
	 * default format.
	 * 
	 * @param printer Output for messages
	 */
	public PrintStreamLog(PrintStream printer, String name)
	{
		this.printer = printer;
		this.formatter = new DefaultFormatter(name);
	}

	/**
	 * Constructs new log which is outputting messages to desired output with
	 * custom format.
	 * 
	 * @param printer Output for messages
	 * @param formatter Formatter for custom messages format
	 */
	public PrintStreamLog(PrintStream printer, Formatter formatter)
	{
		this.printer = printer;
		this.formatter = formatter;
	}

	private void print(String type, String message)
	{
		String formattedMessage = formatter.format(type, message);
		printer.println(formattedMessage);
	}

	/**
	 * {@inheritDoc}
	 */
	public void error(String message)
	{
		print("ERROR", message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void info(String message)
	{
		print("INFO", message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void debug(String message)
	{
		print("DEBUG", message);
	}

	/**
	 * Interface for defining messages formatters.
	 */
	public interface Formatter
	{
		/**
		 * Returns formatted message.
		 * 
		 * @param type Type of message
		 * @param message Content of message
		 * @return Formatted message
		 */
		String format(String type, String message);
	}

	private static class DefaultFormatter implements Formatter
	{
		private final String name;

		public DefaultFormatter(String name)
		{
			this.name = name;
		}

		public String format(String type, String message)
		{
			return "[" + type + "](" + name + ") " + message;
		}
	}
}
