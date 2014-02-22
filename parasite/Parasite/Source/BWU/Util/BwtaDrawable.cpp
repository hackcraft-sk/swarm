#include "BwtaDrawable.h"

#include <set>

#include <BWAPI.h>
#include <BWTA.h>

#include "BWU/Core/Canvas.h"

using std::set;
using std::pair;
using namespace BWAPI;
using namespace BWTA;

namespace BWU
{
	void BwtaDrawable::draw(Canvas& canvas)
	{
		canvas
		.setSolid(false);;

		drawRegions(canvas);
		drawBaseLocations(canvas);
		drawChokepoints(canvas);
	}

	void BwtaDrawable::drawBaseLocations(Canvas& canvas)
	{
		set<BaseLocation*> baseLocations = getBaseLocations();
		set<BaseLocation*>::iterator it;
		for (it = baseLocations.begin(); it != baseLocations.end(); it++)
		{
			BaseLocation *baseLocation = *it;

			drawBaseLocation(baseLocation, canvas);
		}
	}

	void BwtaDrawable::drawBaseLocation(BaseLocation *baseLocation, Canvas& canvas)
	{
		Position center = baseLocation->getPosition();

		Color baseColor = baseLocation->isIsland() ? Colors::Orange : Colors::Yellow;

		canvas
		.setPosition(center)
		.setColor(baseColor)
		.drawCircle(50);

		set<Unit*>::iterator it;

		set<Unit*> minerals = baseLocation->getStaticMinerals();
		for (it = minerals.begin(); it != minerals.end(); it++)
		{
			Unit *mineral = *it;

			canvas
			.setPosition(mineral->getInitialPosition())
			.setColor(Colors::Cyan)
			.drawCircle(20);
		}

		set<Unit*> geysers = baseLocation->getGeysers();
		for (it = geysers.begin(); it != geysers.end(); it++)
		{
			Unit *geyser = *it;

			canvas
			.setPosition(geyser->getInitialPosition())
			.setColor(Colors::Green)
			.drawCircle(30);
		}
	}

	void BwtaDrawable::drawRegions(Canvas& canvas)
	{
		set<BWTA::Region*> regions = getRegions();
		set<BWTA::Region*>::iterator it;
		for (it = regions.begin(); it != regions.end(); it++)
		{
			BWTA::Region *region = *it;
			drawRegion(region, canvas);
		}
	}

	void BwtaDrawable::drawRegion(BWTA::Region *region, Canvas& canvas)
	{
		canvas
		.setColor(Colors::Green);

		Polygon polygon = region->getPolygon();
		Polygon::iterator it;

		Position initialPoint = *polygon.begin();
		canvas.setPosition(initialPoint);

		for (it = polygon.begin() + 1; it != polygon.end(); it++)
		{
			Position point = *it;

			canvas
			.drawLine(point);

			canvas.setPosition(point);
		}

		canvas
		.drawLine(initialPoint);
	}

	void BwtaDrawable::drawChokepoints(Canvas& canvas)
	{
		canvas
		.setColor(Colors::Red);

		set<Chokepoint*> chokepoints = getChokepoints();
		set<Chokepoint*>::iterator it;
		for (it = chokepoints.begin(); it != chokepoints.end(); it++)
		{
			Chokepoint *chokepoint = *it;

			pair<Position, Position> sides = chokepoint->getSides();

			canvas
			.setPosition(sides.first)
			.drawLine(sides.second)
			.setSolid(true)
			.setPosition(sides.first)
			.drawCircle(5)
			.setPosition(sides.second)
			.drawCircle(5);
		}
	}
}
