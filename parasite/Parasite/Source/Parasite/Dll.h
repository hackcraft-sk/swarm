// Exclude rarely-used stuff from Windows headers
#define WIN32_LEAN_AND_MEAN

#include <windows.h>

namespace BWAPI
{
	class Game;
	class AIModule;
	class TournamentModule;

	Game* Broodwar;
}

BOOL APIENTRY DllMain(HANDLE hModule, DWORD ul_reason_for_call, LPVOID lpReserved);

/**
 * Creates standard AIModule. Used mainly for testing in Debug mode.
 */
extern "C" __declspec(dllexport)  BWAPI::AIModule* newAIModule(BWAPI::Game* game);

/**
 * Creates bot's AIModule.
 */
extern "C" __declspec(dllexport)  BWAPI::AIModule* newTournamentAI(BWAPI::Game* game);

/**
 * Creates tournament module.
 */
extern "C" __declspec(dllexport)  BWAPI::TournamentModule* newTournamentModule();
