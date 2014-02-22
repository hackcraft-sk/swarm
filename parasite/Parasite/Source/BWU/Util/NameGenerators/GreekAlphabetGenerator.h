#pragma once

#include "BWU/Core/NamesGenerator.h"

namespace BWU
{
	class SetNamesGenerator;

	class GreekAlphabetGenerator : public NamesGenerator
	{
		private:
			SetNamesGenerator *innerGenerator;
		public:
			GreekAlphabetGenerator();
			virtual ~GreekAlphabetGenerator();

			virtual std::string getNextName();
			virtual bool hasNext();
	};
}
