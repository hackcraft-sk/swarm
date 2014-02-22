package sk.hackcraft.als.slave;

public class Bot
{
	private final int id;
	private final String name;

	private final Type codeType;
	private final String botFileUrl;
	private final String botFileHash;

	public Bot(int id, String name, Type codeType, String botFileUrl, String botFileHash)
	{
		this.id = id;
		this.name = name;

		this.codeType = codeType;
		this.botFileUrl = botFileUrl;
		this.botFileHash = botFileHash;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public Type getType()
	{
		return codeType;
	}

	public String getBotFileUrl()
	{
		return botFileUrl;
	}

	public String getBotFileChecksum()
	{
		return botFileHash;
	}

	@Override
	public String toString()
	{
		return "Bot #" + id + " " + name;
	}

	public enum Type
	{
		CPP_CLIENT("exe"), CPP_MODULE("dll"), JAVA_CLIENT("jar");

		private final String filenameExtension;

		private Type(String filenameExtension)
		{
			this.filenameExtension = filenameExtension;
		}

		public String getFilenameExtension()
		{
			return filenameExtension;
		}

		static public Type getByExtension(String extension)
		{
			if (extension.contentEquals("exe"))
			{
				return Type.CPP_CLIENT;
			}
			else if (extension.contentEquals("dll"))
			{
				return Type.CPP_MODULE;
			}
			else if (extension.contentEquals("jar"))
			{
				return Type.JAVA_CLIENT;
			}
			return null;
		};
	};
}
