#include "AiEngine.h"

#include <vector>
#include <sstream>

#include "BWU/Core/Updateable.h"
#include "BWU/Core/Drawable.h"
#include "BWU/Core/ViewDrawable.h"
#include "BWU/Core/Log.h"
#include "BWU/Core/Canvas.h"
#include "BWU/Util/TextTokenizer.h"
#include "BWU/Enhanced/FlagsController.h"
#include "BWU/Enhanced/TextCommandsReceiver.h"

using std::set;
using std::map;
using std::make_pair;
using std::vector;
using std::string;
using std::stringstream;
using namespace BWAPI;

namespace BWU
{
	AiEngine::AiEngine(Log &console, Canvas &mapCanvas, Canvas &screenCanvas)
	: console(console)
	, mapCanvas(mapCanvas)
	, screenCanvas(screenCanvas)
	{
	}

	AiEngine::~AiEngine()
	{
	}

	void AiEngine::update()
	{
		updateUpdateables();
		drawMapDrawables();
		drawScreenDrawables();
	}

	void AiEngine::updateUpdateables()
	{
		set<Updateable*>::const_iterator it;
		for (it = modules.begin(); it != modules.end(); it++)
		{
			Updateable *module = *it;

			module->update();
		}
	}

	void AiEngine::drawMapDrawables() const
	{
		MapDrawablesSet::const_iterator it;
		for (it = mapDrawables.begin(); it != mapDrawables.end(); it++)
		{
			mapCanvas.reset();

			Drawable *drawable = *it;
			drawable->draw(mapCanvas);
		}
	}

	void AiEngine::drawScreenDrawables() const
	{
		ScreenDrawablesSet::const_iterator it;
		for (it = screenDrawables.begin(); it != screenDrawables.end(); it++)
		{
			screenCanvas.reset();

			Drawable *drawable = *it;
			drawable->draw(screenCanvas);
		}
	}

	void AiEngine::addUpdateable(Updateable* updateable)
	{
		modules.insert(updateable);
	}

	void AiEngine::addMapDrawable(Drawable* drawable)
	{
		mapDrawables.insert(drawable);
	}

	void AiEngine::addScreenDrawable(Drawable* drawable)
	{
		screenDrawables.insert(drawable);
	}

	void AiEngine::addTextCommandReceiver(string commandName, TextCommandsReceiver* textCommandReceiver)
	{
		textCommandsReceivers.insert(make_pair(commandName, textCommandReceiver));
	}

	void AiEngine::onTextReceived(string text)
	{
		if (text[0] == '/' && text[1] == '/')
		{
			if (text.size() == 2)
			{
				printAvailableCommands();
			}
			else
			{
				vector<string> tokens = TextTokenizer::tokenize(text.substr(2));

				map<string, TextCommandsReceiver*>::iterator it = textCommandsReceivers.find(tokens[0]);
				if (it != textCommandsReceivers.end())
				{
					TextCommandsReceiver *receiver = it->second;
					receiver->onCommandReceived(tokens);
				}
			}
		}
		else
		{
			console.print(text);
		}		
	}

	void AiEngine::printAvailableCommands()
	{
		stringstream builder;
		builder << "Available commands: ";

		bool first = true;
		for (map<string, TextCommandsReceiver*>::iterator it = textCommandsReceivers.begin(); it != textCommandsReceivers.end(); it++)
		{
			if (!first)
			{
				builder << ", ";
			}
			else
			{
				first = false;
			}

			string commandName = it->first;
			builder << commandName;
		}

		string availableCommands = builder.str();
		console.print(availableCommands);
	}
}
