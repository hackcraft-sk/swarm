#pragma once

#include <string>

namespace Parasite
{
	class Achievement
	{
		private:
			std::string name;
		public:
			Achievement(std::string name);
			std::string getName();
	};
}
