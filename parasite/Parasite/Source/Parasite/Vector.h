#pragma once

#include <BWAPI.h>

namespace Parasite
{
	class Vector
	{
		private:
			double x, y;
		public:
			static Vector fromPositions(const BWAPI::Position &from, const BWAPI::Position &to);
			static Vector fromVectors(const Vector &from, const Vector &to);

			Vector(int x, int y);
			Vector(double x, double y);

			double getLength() const;
			Vector operator+(const Vector &vector) const;
			Vector operator-(const Vector &vector) const;
			Vector operator*(double coeficient) const;

			Vector normalise() const;

			double getX() const;
			double getY() const;

			BWAPI::Position toPosition() const;
	};

	BWAPI::Position operator+(const BWAPI::Position &position, const Vector &vector);
}
