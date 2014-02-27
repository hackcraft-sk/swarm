#include "Parasite/AutoCamera.h"

#include <BWAPI.h>

#include "Parasite/Vector.h"

namespace Parasite
{
	AutoCamera::UnitInterest::UnitInterest(BWAPI::Unit *unit)
	: unit(unit)
	{
	}

	BWAPI::Position AutoCamera::UnitInterest::getPosition()
	{
		if (unit->getHitPoints() > 0)
		{
			lastValidPosition = unit->getPosition();
		}

		return lastValidPosition;
	}

	AutoCamera::PositionInterest::PositionInterest(BWAPI::Position position)
	: position(position)
	{
	}

	BWAPI::Position AutoCamera::PositionInterest::getPosition()
	{
		return position;
	}

	void AutoCamera::InterestsTable::addInterest(int score, Interest *interest)
	{
		interests.insert(std::pair<int, Interest*>(score, interest));
	}

	AutoCamera::Interest *AutoCamera::InterestsTable::getMostInterestingInterest()
	{
		Interest *interest = (*interests.rbegin()).second;
		return interest;
	}

	bool AutoCamera::InterestsTable::isEmpty()
	{
		return interests.empty();
	}

	void AutoCamera::InterestsTable::clear()
	{
		std::map<int, Interest*>::iterator it;
		for (it = interests.begin(); it != interests.end(); it++)
		{
			Interest *interest = (*it).second;

			delete interest;
		}

		interests.clear();
	}

	AutoCamera::AutoCamera(BWAPI::Game *game)
	: game(game)
	, screenCenter(BWAPI::Position(320, 240))
	, sceneSetFrame(game->getFrameCount())
	, sceneTimeoutInFrames(50)
	{
		Interest *defaultInterest = new PositionInterest(BWAPI::Positions::None);
		interestsTable.addInterest(1, defaultInterest);

		centerScreenAt(defaultInterest);
	}

	void AutoCamera::update()
	{
		updateInterestsTable();

		if (!interestsTable.isEmpty())
		{
			Interest *interest = interestsTable.getMostInterestingInterest();

			centerScreenAt(interest);
		}

		if (canModifyScreenPosition())
		{
			updateScreenPosition();
		}
	}

	bool AutoCamera::isSceneOutdated()
	{
		int actualFrame = game->getFrameCount();

		return (sceneSetFrame + sceneTimeoutInFrames < actualFrame);
	}

	bool AutoCamera::canModifyScreenPosition()
	{
		// Stlaèené ¾avé tlaèítko sa èasto používa pri ahaní viewportu po minimape,
		// v takomto prípade je nežiadúce meni viewport
		if (game->getMouseState(BWAPI::M_LEFT))
		{
			return false;
		}

		// Stlaèené stredné tlaèítko sa používa pre pan poh¾adu,
		// v takomto prípade je nežiadúce meni viewport
		if (game->getMouseState(BWAPI::M_MIDDLE))
		{
			return false;
		}

		return true;
	}

	void AutoCamera::updateInterestsTable()
	{
		interestsTable.clear();

		std::set<BWAPI::Unit*> allUnits = game->getAllUnits();
		std::set<BWAPI::Unit*>::iterator it;

		for (it = allUnits.begin(); it != allUnits.end(); it++)
		{
			BWAPI::Unit *unit = (*it);

			if (!unit->isVisible())
			{
				continue;
			}
			
			createInterestForUnit(unit);
		}
	}

	void AutoCamera::createInterestForUnit(BWAPI::Unit *unit)
	{
		BWAPI::UnitType unitType = unit->getType();

		int score = 0;

		if (unitType == BWAPI::UnitTypes::Zerg_Larva)
		{
			score -= 15;
		}

		if (unitType.isWorker())
		{
			score -= 15;
		}

		if (unitType.isBuilding())
		{
			score -= 20;
		}

		if (unit->isMoving())
		{
			score += 10;
		}

		if (unit->isAttacking())
		{
			score += 30;
		}

		if (unit->isUnderAttack())
		{
			score += 30;
		}

		std::set<BWAPI::Unit*> nearbyUnits = unit->getUnitsInRadius(200);
		std::set<BWAPI::Unit*> enemyUnits;
		std::set<BWAPI::Unit*>::iterator it;
		for (it = nearbyUnits.begin(); it != nearbyUnits.end(); it++)
		{
			BWAPI::Unit *nearbyUnit = (*it);
			
			BWAPI::Player *nearbyUnitPlayer = nearbyUnit->getPlayer();
			BWAPI::Player *self = game->self();

			if (self->isEnemy(nearbyUnitPlayer))
			{
				enemyUnits.insert(nearbyUnit);
			}
		}

		score += enemyUnits.size() * 10;

		BWAPI::Player *self = game->self();
		BWAPI::Player *unitPlayer = unit->getPlayer();
		if (self == unitPlayer)
		{
			score += 50;
		}

		Interest *interest = new UnitInterest(unit);

		interestsTable.addInterest(score, interest);
	}

	void AutoCamera::centerScreenAt(Interest *interest)
	{
		actualInterest = interest;

		sceneSetFrame = game->getFrameCount();
	}

	void AutoCamera::updateScreenPosition()
	{
		BWAPI::Position screenTargetPosition = actualInterest->getPosition();

		int framesToGo = (sceneSetFrame + sceneTimeoutInFrames) - game->getFrameCount();

		if (framesToGo > 0)
		{
			Vector vector = Vector::fromPositions(screenActualPosition, screenTargetPosition);

			double distance = vector.getLength();
			// ciel je moc daleko, spravime "prestrih"
			if (distance > 1000 || BWAPI::Broodwar->getFrameCount() < 5)
			{
				screenActualPosition = screenTargetPosition;
			}
			else
			{
				// na tomto sa este pracuje
				double length = vector.getLength();
				vector = vector.normalise();

				if (length > 15)
				{
					vector = vector * 10;
				}

				screenActualPosition = screenActualPosition + vector.toPosition();
			}
		}

		// offset, keïže setScreenPosition nastavuje pozíciu ¾avého horného rohu obrazovky
		int x, y;
		x = screenActualPosition.x() - screenCenter.x();
		y = screenActualPosition.y() - screenCenter.y();

		BWAPI::Broodwar->setScreenPosition(x, y);
	}
}
