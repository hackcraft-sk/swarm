#include "Parasite/AutoCamera.h"

#include <BWAPI.h>

#include "Parasite/Vector.h"

using namespace BWAPI;

using std::map;
using std::make_pair;
using std::set;

namespace Parasite
{
	AutoCamera::UnitInterest::UnitInterest(Unit &unit)
			: unit(unit)
	{
	}

	Position AutoCamera::UnitInterest::getPosition()
	{
		if (unit.getHitPoints() > 0)
		{
			lastValidPosition = unit.getPosition();
		}

		return lastValidPosition;
	}

	AutoCamera::PositionInterest::PositionInterest(Position position)
			: position(position)
	{
	}

	Position AutoCamera::PositionInterest::getPosition()
	{
		return position;
	}

	AutoCamera::InterestsTable::InterestsTable(Game &game)
			: game(game)
	{
	}

	AutoCamera::Interest *AutoCamera::InterestsTable::getMostInterestingInterest()
	{
		update();

		if (interests.empty())
		{
			return new PositionInterest(Positions::None);
		}
		else
		{
			return (*interests.rbegin()).second;
		}
	}

	void AutoCamera::InterestsTable::update()
	{
		clear();

		set<Unit*> allUnits = game.getAllUnits();
		set<Unit*>::iterator it;

		for (it = allUnits.begin(); it != allUnits.end(); it++)
		{
			Unit &unit = *(*it);

			if (!unit.isVisible())
			{
				continue;
			}

			createInterestForUnit(unit);
		}
	}

	void AutoCamera::InterestsTable::createInterestForUnit(Unit &unit)
	{
		UnitType unitType = unit.getType();

		int score = 0;

		if (unitType == UnitTypes::Zerg_Larva)
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

		if (unit.isMoving())
		{
			score += 10;
		}

		if (unit.isAttacking())
		{
			score += 30;
		}

		if (unit.isUnderAttack())
		{
			score += 30;
		}

		set<Unit*> nearbyUnits = unit.getUnitsInRadius(200);
		set<Unit*> enemyUnits;
		set<Unit*>::iterator it;
		for (it = nearbyUnits.begin(); it != nearbyUnits.end(); it++)
		{
			Unit *nearbyUnit = (*it);

			Player *nearbyUnitPlayer = nearbyUnit->getPlayer();
			Player *self = game.self();

			if (self->isEnemy(nearbyUnitPlayer))
			{
				enemyUnits.insert(nearbyUnit);
			}
		}

		score += enemyUnits.size() * 10;

		Player *self = game.self();
		Player *unitPlayer = unit.getPlayer();
		if (self == unitPlayer)
		{
			score += 50;
		}

		Interest *interest = new UnitInterest(unit);

		interests.insert(make_pair(score, interest));
	}

	void AutoCamera::InterestsTable::clear()
	{
		map<int, Interest*>::iterator it;
		for (it = interests.begin(); it != interests.end(); it++)
		{
			Interest *interest = (*it).second;
			delete interest;
		}

		interests.clear();
	}

	AutoCamera::AutoCamera(Game &game)
			: game(game), screenCenter(Position(320, 200)), sceneSetFrame(game.getFrameCount()), sceneTimeoutInFrames(100), interestsTable(InterestsTable(game))
	{
		actualInterest = interestsTable.getMostInterestingInterest();
	}

	/**
	 * How this works? Every time, when scene is outdated,
	 * code will ask for new interest. Interest is either
	 * unit or position interest. If there is no interest,
	 * position interest with Positions::None will be returned.
	 *
	 * Deallocation is happening in interestsTable; in calling
	 * getMostInterestingInterest, actualInterest is deleted, so
	 * dont keep it.
	 *
	 * Then camera will just follow interests position, or mouse,
	 * depends on canModifyScreenPosition value.
	 */
	void AutoCamera::update()
	{
		if (isSceneOutdated())
		{
			actualInterest = interestsTable.getMostInterestingInterest();
			sceneSetFrame = game.getFrameCount();
		}

		if (canModifyScreenPosition())
		{
			updateScreenPosition();
		}
	}

	bool AutoCamera::isSceneOutdated()
	{
		int actualFrame = game.getFrameCount();

		return (sceneSetFrame + sceneTimeoutInFrames < actualFrame);
	}

	bool AutoCamera::canModifyScreenPosition()
	{
		// manual view control with pressed left or middle mouse button
		if (game.getMouseState(M_LEFT) || game.getMouseState(M_MIDDLE))
		{
			return false;
		}

		return true;
	}

	void AutoCamera::updateScreenPosition()
	{
		if (actualInterest->getPosition() == Positions::None)
		{
			return;
		}

		Position interestPosition = actualInterest->getPosition();

		int x, y;
		x = interestPosition.x() - 320;
		y = interestPosition.y() - 200;

		game.setScreenPosition(x, y);
	}
}
