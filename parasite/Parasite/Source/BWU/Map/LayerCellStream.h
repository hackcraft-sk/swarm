#pragma once

#include "BWU/Core/Point.h"

namespace BWU
{
	class ReadonlyLayer;

	class LayerCellStream
	{
		public:
			class Cell
			{
				private:
					Point position;
					int value;
				public:
					Cell(Point position, int value)
					: position(position)
					, value(value)
					{
					}

					Point getPosition()
					{
						return position;
					}

					int getValue()
					{
						return value;
					}
			};

		private:
			ReadonlyLayer &layer;

			unsigned int actualIndex;
			unsigned int length;
			unsigned int width;
			unsigned int height;

			Point indexToPosition(unsigned int index);
		public:
			LayerCellStream(ReadonlyLayer &layer);

			bool hasNext();
			Cell getNext();
	};
}
