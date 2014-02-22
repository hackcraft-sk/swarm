#include "Parasite/Achievement.h"

using std::string;

namespace Parasite
{
	Achievement::Achievement(string name)
	: name(name)
	{
	}

	string Achievement::getName()
	{
		return name;
	}
}
