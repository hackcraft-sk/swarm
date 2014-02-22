#include "Parasite/ScmaitTournamentModule.h"

#include <cstdlib>

#include <BWAPI.h>

#include "Parasite/Settings.h"

namespace Parasite
{
	ScmaitTournamentModule::ScmaitTournamentModule(BWAPI::Game *game) :
			game(game)
	{
	}

	bool ScmaitTournamentModule::onAction(int actionType)
	{
		return onAction(actionType, NULL);
	}

	bool ScmaitTournamentModule::onAction(int actionType, void *parameter)
	{
		switch (actionType)
		{
			case BWAPI::Tournament::EnableFlag:
				switch (*(int*) parameter)
				{
					case BWAPI::Flag::CompleteMapInformation:
					case BWAPI::Flag::UserInput:
						// Disallow these two flags
						return false;
				}

				// Allow other flags if we add more that don't affect gameplay specifically
				return true;
			case BWAPI::Tournament::PauseGame:
			case BWAPI::Tournament::RestartGame:
			case BWAPI::Tournament::ResumeGame:
			case BWAPI::Tournament::SetFrameSkip:
			case BWAPI::Tournament::SetGUI:
			case BWAPI::Tournament::SetLocalSpeed:
			case BWAPI::Tournament::SetMap:
			case BWAPI::Tournament::SetTextSize:
			case BWAPI::Tournament::SendText:
			case BWAPI::Tournament::Printf:
				// Disallow these actions
				return false;
			case BWAPI::Tournament::LeaveGame:
			case BWAPI::Tournament::ChangeRace:
			case BWAPI::Tournament::SetLatCom:
				// Allow these actions
				return true;
			case BWAPI::Tournament::SetCommandOptimizationLevel:
				// Set a minimum command optimization level
				// to reduce APM with no action loss
				return *(int*) parameter > Settings::MINIMUM_COMMAND_OPTIMIZATION;
		}

		return true;
	}

	void ScmaitTournamentModule::onFirstAdvertisement()
	{
		game->sendText("SCMAI match started.");
	}
}
