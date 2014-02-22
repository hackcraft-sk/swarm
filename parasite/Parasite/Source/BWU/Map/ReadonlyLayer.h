#pragma once

#include <exception>

#include "BWU/Core/Point.h"
#include "BWU/Core/Dimension.h"

namespace BWU
{
	class ReadonlyLayer
	{
		public:
			class InvalidPointException : public std::exception
			{
			};
		public:
			virtual ~ReadonlyLayer() {}

			virtual Dimension getSize() const = 0;

			virtual int getValue(Point position) const = 0;

			virtual bool isValid(Point position) const = 0;
	};
}
