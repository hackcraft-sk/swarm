#pragma once

#include "BWU/Core/Point.h"
#include "BWU/Core/Dimension.h"

namespace BWU
{
	class Rectangle
	{
		private:
			Point position;
			Dimension size;
		public:
			Rectangle(Point position, Dimension size)
			: position(position)
			, size(size)
			{
			}

			Point getPosition()
			{
				return position;
			}

			Dimension getSize()
			{
				return size;
			}
	};
}
