#include "Parasite/Vector.h"

#include <cmath>

namespace Parasite
{
	Vector Vector::fromPositions(const BWAPI::Position &from, const BWAPI::Position &to)
	{
		return Vector(to.x() - from.x(), to.y() - from.y());
	}

	Vector Vector::fromVectors(const Vector &from, const Vector &to)
	{
		return Vector(to.x - from.x, to.y - from.y);
	}

	Vector::Vector(int x, int y)
	{
		this->x = (double)x;
		this->y = (double)y;
	}

	Vector::Vector(double x, double y)
	{
		this->x = x;
		this->y = y;
	}

	double Vector::getX() const
	{
		return this->x;
	}

	double Vector::getY() const
	{
		return this->y;
	}

	BWAPI::Position Vector::toPosition() const
	{
		return BWAPI::Position((int)x, (int)y);
	}

	double Vector::getLength() const
	{
		return sqrt((double)(this->x*this->x + this->y*this->y));
	}

	Vector Vector::operator+(const Vector &vec) const
	{
		return Vector(vec.x + this->x, vec.y + this->y);
	}

	Vector Vector::operator-(const Vector &vec) const
	{
		return Vector(this->x - vec.x, this->y - vec.y);
	}

	Vector Vector::operator*(double coeficient) const
	{
		return Vector(this->x * coeficient, this->y * coeficient);
	}

	Vector Vector::normalise(void) const
	{
		double length = getLength();

		if (length == 0)
		{
			return Vector(0, 0);
		}

		return Vector((double)this->x * (1/length), (double)this->y * (1/length));
	}

	BWAPI::Position operator+(const BWAPI::Position &position, const Vector &vector)
	{
		BWAPI::Position vectorPosition = vector.toPosition();

		return position + vectorPosition;
	}
}
