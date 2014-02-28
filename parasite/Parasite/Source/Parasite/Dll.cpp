// Exclude rarely-used stuff from Windows headers
#define WIN32_LEAN_AND_MEAN

#include "Parasite/Dll.h"

#include <windows.h>

#include <cstdio>

#include <iostream>

#include <BWAPI.h>

#include "Parasite/ScmaitTournamentModule.h"
#include "Parasite/ParasiteModule.h"

#include "BWU/Util/GameLog.h"
#include "Parasite/SocketMessageInterface.h"
#include "Parasite/LogMessageInterface.h"
#include "Parasite/MockOverlordConnection.h"
#include "Parasite/MessageOverlordConnection.h"

using std::cout;
using std::endl;
using namespace BWAPI;

using BWU::GameLog;

using namespace Parasite;

BOOL APIENTRY DllMain(HANDLE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
	switch (ul_reason_for_call)
	{
		case DLL_PROCESS_ATTACH:
			BWAPI_init();
			break;
		case DLL_PROCESS_DETACH:
			break;
	}

	return TRUE;
}

extern "C" __declspec(dllexport)     AIModule* newAIModule(Game* game)
{
	return newTournamentAI(game);
}

extern "C" __declspec(dllexport)     AIModule* newTournamentAI(Game* game)
{
	Broodwar = game;

	GameLog *log = new BWU::GameLog(*game);

	MessageInterface *messageInterface;

	// real interface
	messageInterface = new SocketMessageInterface("localhost");

	// debug interface
	//messageInterface = new LogMessageInterface(*log);

	MessageOverlordConnection *overlordConnection = new MessageOverlordConnection(*messageInterface);

	return new ParasiteModule(*game, *log, *overlordConnection);

	/**
	 messageInterface->close();
	 delete messageInterface;
	 */
}

extern "C" __declspec(dllexport)     BWAPI::TournamentModule* newTournamentModule()
{
	return new ScmaitTournamentModule(Broodwar);
}
