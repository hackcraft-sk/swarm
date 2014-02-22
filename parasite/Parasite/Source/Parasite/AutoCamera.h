#pragma once

#include <BWAPI.h>

#include <map>

namespace Parasite
{
	class AutoCamera
	{
		private:
			class Interest
			{
				public:
					virtual BWAPI::Position getPosition() = 0;
			};

			class UnitInterest: public Interest
			{
				private:
					BWAPI::Unit *unit;
					BWAPI::Position lastValidPosition;
				public:
					UnitInterest(BWAPI::Unit *unit);
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
					std::map<int, Interest*> interests;
				public:
					void addInterest(int score, Interest *interest);
					Interest *getMostInterestingInterest();
					bool isEmpty();
					void clear();
			};

			BWAPI::Game *game;

			const BWAPI::Position screenCenter;
			BWAPI::Position screenActualPosition;
			Interest *actualInterest;

			InterestsTable interestsTable;

			int sceneSetFrame;
			const int sceneTimeoutInFrames;

			bool isSceneOutdated();
			bool canModifyScreenPosition();

			void updateInterestsTable();
			void createInterestForUnit(BWAPI::Unit *unit);

			void centerScreenAt(Interest *interest);
			void updateScreenPosition();

		public:
			AutoCamera(BWAPI::Game *game);
			void update();
	};
}
