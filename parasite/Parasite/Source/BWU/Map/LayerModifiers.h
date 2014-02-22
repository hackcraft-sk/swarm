#pragma once

#include <algorithm>
#include <map>

#include "BWU/Core/Point.h"

namespace BWU
{
	namespace LayerModifiers
	{
		class MaxModifier
		{
			public:
				int operator ()(Point &position, int sourceValue, int actualValue)
				{
					return std::max(sourceValue, actualValue);
				}
		};

		class TranslationMapModifier
		{
			private:
				std::map<int, int> rules;
			public:
				TranslationMapModifier &addRule(int sourceValue, int newValue)
				{
					rules.insert(std::make_pair(sourceValue, newValue));

					return *this;
				}

				int operator ()(Point &position, int sourceValue, int actualValue)
				{
					std::map<int, int>::iterator it = rules.find(sourceValue);

					return it == rules.end() ? actualValue : it->second;
				}
		};
	}
}
