#include "BWU/Enhanced/Flags.h"

using std::string;
using std::map;

namespace BWU
{
	bool Flags::hasFlag(string name)
	{
		return flags.find(name) != flags.end();
	}

	bool Flags::isFlagEnabled(string name)
	{
		return flags[name];
	}

	void Flags::toggleFlag(string name)
	{
		flags[name] = !flags[name];
	}

	void Flags::setFlag(string name, bool state)
	{
		flags[name] = state;
	}

	std::set<string> Flags::getFlagsNames()
	{
		std::set<string> names;
		for (map<string, bool>::iterator it = flags.begin(); it != flags.end(); it++)
		{
			string name = it->first;
			names.insert(name);
		}

		return names;
	}
}
