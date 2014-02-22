#pragma once

#include <string>
#include <vector>

#include "BWU/Enhanced/TextCommandsReceiver.h"
#include "BWU/Enhanced/Flags.h"

namespace BWU
{
	class Log;

	class FlagsController : public TextCommandsReceiver
	{
		private:
			Flags &flags;
			Log &console;

			void printPossibleFlags();
		public:
			FlagsController(Flags &flags, Log &console);

			virtual void onCommandReceived(std::vector<std::string> command);
	};
}
