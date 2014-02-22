#include "BWU/Util/NameGenerators/NumberGenerator.h"

#include <limits>
#include <cstdlib>

using std::numeric_limits;
using std::string;

namespace BWU
{
	NumberGenerator::NumberGenerator()
	: nextNumber(1)
	, maxNumber(numeric_limits<unsigned int>::max())
	{
	}

	NumberGenerator::~NumberGenerator()
	{
	}

	std::string NumberGenerator::getNextName()
	{
		unsigned int number = getNextId();

		char str[20];
		_itoa_s(number, str, 10);
		return string(str);
	}

	Id NumberGenerator::getNextId()
	{
		int number = nextNumber;
		nextNumber++;

		return number;
	}

	bool NumberGenerator::hasNext()
	{
		return nextNumber <= maxNumber;
	}
}


