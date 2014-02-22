#include "FlagsController.h"

#include <set>
#include <sstream>

#include "BWU/Core/Log.h"

using std::string;
using std::vector;
using std::set;
using std::stringstream;

namespace BWU
{
	FlagsController::FlagsController(Flags &flags, Log &console)
	: flags(flags)
	, console(console)
	{
	}

	void FlagsController::onCommandReceived(vector<string> tokens)
	{
		if (tokens.size() == 1)
		{
			printPossibleFlags();
			return;
		}

		if (tokens.size() != 3)
		{
			console.print("\x08 Invalid parameters count");
			return;
		}

		string action = tokens[1];
		string flag = tokens[2];

		if (!flags.hasFlag(flag))
		{
			console.print("\x08 Unknown flag: " + flag);
			return;
		}

		if (action == "e")
		{
			flags.setFlag(flag, true);
			console.print("\x07 Enabled " + flag + " flag");
		}
		else if (action == "d")
		{
			flags.setFlag(flag, false);
			console.print("\x11 Disabled " + flag + " flag");
		}
	}

	void FlagsController::printPossibleFlags()
	{
		set<string> names = flags.getFlagsNames();

		console.print("Usage: //f [e,d] <flag name>");

		stringstream builder;
		builder << "Available flags: ";

		bool first = true;
		for (set<string>::iterator it = names.begin(); it != names.end(); it++)
		{
			if (!first)
			{
				builder << ", ";
			}
			else
			{
				first = false;
			}

			string name = *it;
			builder << name;
		}

		string allFlags = builder.str();
		console.print(allFlags);
	}
}
