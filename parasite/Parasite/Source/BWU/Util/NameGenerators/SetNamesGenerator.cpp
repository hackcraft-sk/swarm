#include "BWU/Util/NameGenerators/SetNamesGenerator.h"

using std::set;
using std::string;

namespace BWU
{
	SetNamesGenerator::SetNamesGenerator(set<string> names)
	: names(names)
	{
		it = this->names.begin();
	}

	string SetNamesGenerator::getNext()
	{
		string name = *it;
		it++;

		return name;
	}

	bool SetNamesGenerator::hasNext()
	{
		return it != names.end();
	}
}
