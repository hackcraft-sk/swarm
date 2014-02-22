#pragma once

#include "BWU/Core/Drawable.h"

#include <set>

#include <BWAPI.h>

#include "BWU/Core/Canvas.h"
#include "BWU/Util/BasicUnitsPool.h"

namespace BWU
{
	class UnitsPoolDrawable : public Drawable
	{
		private:
			BasicUnitsPool &unitsPool;

			void drawUnit(Canvas &canvas, BWAPI::Unit &unit);
			void drawUnit(Canvas &canvas, BWAPI::Unit &unit, BasicUnitsPool::UsageRecord &usageRecord);
		public:
			UnitsPoolDrawable(BasicUnitsPool &unitsPool);

			virtual void draw(Canvas &canvas);
	};
}
