#include "BWU/Util/NameGenerators/GreekAlphabetGenerator.h"

#include <set>

#include "BWU/Util/NameGenerators/SetNamesGenerator.h"

using std::string;
using std::set;

namespace BWU
{
	GreekAlphabetGenerator::GreekAlphabetGenerator()
	{
		string rawNames[] = {
			"Alpha",
			"Beta",
			"Gamma",
			"Delta",
			"Epsilon",
			"Zeta",
			"Eta",
			"Theta",
			"Iota",
			"Kappa",
			"Lambda",
			"Mu",
			"Nu",
			"Omicron",
			"Pi",
			"Rho",
			"Sigma",
			"Tau",
			"Upsilon",
			"Phi",
			"Chi",
			"Psi",
			"Omega"
		};

		set<string> names;
		unsigned int count = sizeof(rawNames) / sizeof(string);
		for (unsigned int i = 0; i < count; i++)
		{
			string name = rawNames[i];
			names.insert(name);
		}

		innerGenerator = new SetNamesGenerator(names);
	}

	GreekAlphabetGenerator::~GreekAlphabetGenerator()
	{
		delete innerGenerator;
	}

	std::string GreekAlphabetGenerator::getNextName()
	{
		return innerGenerator->getNext();
	}

	bool GreekAlphabetGenerator::hasNext()
	{
		return innerGenerator->hasNext();
	}
}
