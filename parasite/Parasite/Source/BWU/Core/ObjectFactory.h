#pragma once

#include "BWU/Core/Drawable.h"

namespace BWU
{
	template <class O>
	class ObjectFactory
	{
		public:
			virtual ~ObjectFactory() {};

			virtual O *create();
	};
}
