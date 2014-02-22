#pragma once

#include <string>
#include <vector>

namespace BWU
{
	class TextTokenizer
	{
		public:
			static std::vector<std::string> tokenize(std::string text);
	};
}
