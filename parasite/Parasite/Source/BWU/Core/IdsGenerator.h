#pragma once

#include "BWU/Core/Id.h"

namespace BWU
{
	class IdsGenerator
	{
		public:
			virtual ~IdsGenerator() {};

			virtual Id getNextId() = 0;
			virtual bool hasNext() = 0;
	};
}
