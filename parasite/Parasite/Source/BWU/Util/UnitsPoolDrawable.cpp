#include "BWU/Util/UnitsPoolDrawable.h"

#include <string>

#include "BWU/Core/UnitsOwner.h"

using std::set;
using std::string;

using BWAPI::Unit;
using BWAPI::Position;
using BWAPI::Color;

namespace BWU
{
	UnitsPoolDrawable::UnitsPoolDrawable(BasicUnitsPool &unitsPool)
	: unitsPool(unitsPool)
	{
	}

	void UnitsPoolDrawable::draw(Canvas &canvas)
	{
		set<Unit*> units = unitsPool.managedUnits;
		set<Unit*>::const_iterator it;
		for (it = units.begin(); it != units.end(); it++)
		{
			Unit *unit = *it;

			BasicUnitsPool::UsageRecordsMap::const_iterator urmit = unitsPool.usageRecords.find(unit);
			if (urmit == unitsPool.usageRecords.end())
			{
				drawUnit(canvas, *unit);
			}
			else
			{
				BasicUnitsPool::UsageRecord &usageRecord = *urmit->second;
				drawUnit(canvas, *unit, usageRecord);
			}
		}
	}

	void UnitsPoolDrawable::drawUnit(Canvas &canvas, BWAPI::Unit &unit)
	{
		Position position = unit.getPosition();

		const int circleRadius = 15;
		BWAPI::Color circleColor = BWAPI::Colors::Green;

		canvas
		.setPosition(position)
		.setColor(circleColor)
		.setSolid(false)
		.drawCircle(circleRadius);
	}

	void UnitsPoolDrawable::drawUnit(Canvas &canvas, BWAPI::Unit &unit, BasicUnitsPool::UsageRecord &usageRecord)
	{
		Position position = unit.getPosition();

		const int circleRadius = 15;
		BWAPI::Color circleColor = BWAPI::Colors::Green;

		canvas
		.setPosition(position)
		.setColor(circleColor)
		.setSolid(false)
		.drawCircle(circleRadius);

		UnitsOwner &unitsOwner = usageRecord.getUnitsOwner();
		string name = unitsOwner.getName();

		Position textPosition = position + Position(10, 20);
		canvas
		.setPosition(textPosition)
		.drawText(name);
	}
}
