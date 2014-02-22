#pragma once

#include <string>

#include <BWAPI.h>

namespace Parasite
{
	class PlayerColor
	{
		private:
			int red, green, blue;
		public:
			PlayerColor(BWAPI::Color color);

			std::string toHexString();
	};
}
