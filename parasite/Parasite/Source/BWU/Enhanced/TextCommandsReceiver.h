#pragma once

namespace BWU
{
	class TextCommandsReceiver
	{
		public:
			virtual ~TextCommandsReceiver() {};

			virtual void onCommandReceived(std::vector<std::string> command) = 0;
	};
}
