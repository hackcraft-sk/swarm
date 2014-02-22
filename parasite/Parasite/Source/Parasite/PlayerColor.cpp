#include "Parasite/PlayerColor.h"

#include <iomanip>
#include <sstream>

using std::stringstream;
using std::hex;
using std::setfill;
using std::setw;

namespace Parasite
{
	PlayerColor::PlayerColor(BWAPI::Color color)
	: red(color.red())
	, green(color.green())
	, blue(color.blue())
	{
	}

	std::string PlayerColor::toHexString()
	{
		stringstream s;
		s << "#" << hex;
		s << setfill('0') << setw(2) << red;
		s << setfill('0') << setw(2) << green;
		s << setfill('0') << setw(2) << blue;

		return s.str();
	}
}
