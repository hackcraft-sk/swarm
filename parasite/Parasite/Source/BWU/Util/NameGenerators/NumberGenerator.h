#pragma once

#include <string>

#include "BWU/Core/NamesGenerator.h"
#include "BWU/Core/IdsGenerator.h"

namespace BWU
{
	class NumberGenerator : public NamesGenerator, public IdsGenerator
	{
		private:
			unsigned int nextNumber;
			unsigned int maxNumber;
		public:
			NumberGenerator();
			virtual ~NumberGenerator();

			virtual std::string getNextName();
			virtual Id getNextId();
			virtual bool hasNext();
	};
}
