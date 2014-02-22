#pragma once

#include <map>
#include <set>
#include <string>

namespace BWU
{
	class Flags
	{
		private:
			std::map<std::string, bool> flags;
		public:
			bool hasFlag(std::string name);
			bool isFlagEnabled(std::string name);
			void toggleFlag(std::string name);
			void setFlag(std::string name, bool state);

			std::set<std::string> getFlagsNames();
	};
}
