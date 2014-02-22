#pragma once

#include "BWU/Map/ReadonlyLayer.h"
#include "BWU/Map/LayerCellStream.h"

namespace BWU
{
	class Layer : public ReadonlyLayer
	{
		public:
			virtual void setValue(Point position, int value) = 0;

			template <class M>
			void modify(ReadonlyLayer &source, M modifier);
	};

	template<class M>
	void Layer::modify(ReadonlyLayer& source, M modifier)
	{
		LayerCellStream stream(source);

		while (stream.hasNext())
		{
			LayerCellStream::Cell cell = stream.getNext();

			Point position = cell.getPosition();
			int sourceValue = cell.getValue();
			int actualValue = getValue(position);

			int newValue = modifier(position, sourceValue, actualValue);

			setValue(position, newValue);
		}
	}
}
