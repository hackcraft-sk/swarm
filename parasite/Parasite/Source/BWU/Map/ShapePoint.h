#pragma once

#include "BWU/Core/Point.h"

namespace BWU
{
	class ShapePoint
	{
		private:
			Point offset;
			int value;
		public:
			ShapePoint()
			: offset(Point(0, 0))
			, value(0)
			{
			}

			ShapePoint(Point offset, int value)
			: offset(offset)
			, value(value)
			{
			}

			Point getOffset()
			{
				return offset;
			}

			int getValue()
			{
				return value;
			}
	};
}
