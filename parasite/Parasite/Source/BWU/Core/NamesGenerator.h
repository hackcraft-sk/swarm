#pragma once

#include <string>

namespace BWU
{
	class NamesGenerator
	{
		public:
			virtual ~NamesGenerator() {};

			virtual std::string getNextName() = 0;
			virtual bool hasNext() = 0;
	};
}
