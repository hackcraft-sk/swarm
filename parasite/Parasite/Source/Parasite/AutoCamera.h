#pragma once

#include <map>

#include <BWAPI.h>

namespace Parasite
{
	class AutoCamera
	{
		private:
			class Interest
			{
				public:
					virtual ~Interest()
					{
					}

					virtual BWAPI::Position getPosition() = 0;
			};

			class UnitInterest: public Interest
			{
				private:
					BWAPI::Unit &unit;
					BWAPI::Position lastValidPosition;
				public:
					UnitInterest(BWAPI::Unit &unit);
					BWAPI::Position getPosition();
			};

			class PositionInterest: public Interest
			{
				private:
					BWAPI::Position position;
				public:
					PositionInterest(BWAPI::Position position);
					BWAPI::Position getPosition();
			};

			class InterestsTable
			{
				private:
					BWAPI::Game &game;
					std::map<int, Interest*> interests;

					void update();
					void clear();
					void createInterestForUnit(BWAPI::Unit &unit);
				public:
					InterestsTable(BWAPI::Game &game);
					Interest *getMostInterestingInterest();
			};

			BWAPI::Game &game;

			const BWAPI::Position screenCenter;

			BWAPI::Position screenActualPosition;
			Interest *actualInterest;

			InterestsTable interestsTable;

			int sceneSetFrame;
			const int sceneTimeoutInFrames;

			bool isSceneOutdated();
			bool canModifyScreenPosition();

			void updateScreenPosition();

		public:
			AutoCamera(BWAPI::Game &game);
			void update();
	};
}
