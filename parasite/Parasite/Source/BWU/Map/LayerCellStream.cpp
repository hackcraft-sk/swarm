#include "BWU/Map/LayerCellStream.h"

#include "BWU/Core/Dimension.h"
#include "BWU/Map/ReadonlyLayer.h"

namespace BWU
{
	LayerCellStream::LayerCellStream(ReadonlyLayer &layer)
	: layer(layer)
	, actualIndex(0)
	{
		Dimension size = layer.getSize();

		width = size.getWidth();
		height = size.getHeight();

		length = width * height;
	}

	Point LayerCellStream::indexToPosition(unsigned int index)
	{
		int x = index % width;
		int y = index / height;

		return Point(x, y);
	}

	bool LayerCellStream::hasNext()
	{
		return actualIndex < length;
	}

	LayerCellStream::Cell LayerCellStream::getNext()
	{
		Point position = indexToPosition(actualIndex);

		actualIndex++;

		int value = layer.getValue(position);

		return Cell(position, value);
	}
}
