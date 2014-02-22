#pragma once

#include <BWAPI.h>

#include <set>
#include <map>
#include <string>

#include "BWU/Core/Canvas.h"
#include "BWU/Util/GameLog.h"
#include "BWU/Util/GameCanvas.h"
#include "BWU/Util/GameMap.h"

namespace BWU
{
	class Log;
	class Canvas;

	class Updateable;
	class Drawable;

	class FlagsController;
	class TextCommandsReceiver;

	class AiEngine
	{
		private:
			typedef std::set<Drawable*> ScreenDrawablesSet;
			typedef std::set<Drawable*> MapDrawablesSet;
			Log &console;
			Canvas &mapCanvas;
			Canvas &screenCanvas;

			std::set<BWU::Updateable*> modules;
			MapDrawablesSet mapDrawables;
			ScreenDrawablesSet screenDrawables;
			std::map<std::string, TextCommandsReceiver*> textCommandsReceivers;

			void updateUpdateables();
			void drawMapDrawables() const;
			void drawScreenDrawables() const;

			void printAvailableCommands();
		public:
			AiEngine(Log &console, Canvas &mapCanvas, Canvas &screenCanvas);
			~AiEngine();

			void update();

			void addUpdateable(Updateable *updateable);
			void addMapDrawable(Drawable *drawable);
			void addScreenDrawable(Drawable *drawable);
			void addTextCommandReceiver(std::string commandName, TextCommandsReceiver *textCommandReceiver);

			void onTextReceived(std::string command);
	};
}
