#pragma once

#include <set>
#include <string>

namespace BWU
{
	class SetNamesGenerator
	{
		private:
			std::set<std::string> names;
			std::set<std::string>::iterator it;
		public:
			SetNamesGenerator(std::set<std::string> names);
			std::string getNext();
			bool hasNext();
	};
}
