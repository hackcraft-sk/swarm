#pragma once

namespace BWU
{
	class Point
	{
		private:
			int x, y;
		public:
			Point()
			: x(0)
			, y(0)
			{
			}

			Point(int x, int y)
			: x(x)
			, y(y)
			{
			}

			int getX()
			{
				return x;
			}

			int getY()
			{
				return y;
			}

			Point operator +(const Point &point) const
			{
				return Point(x + point.x, y + point.y);
			}

			Point operator -(const Point &point) const
			{
				return Point(x - point.x, y - point.y);
			}

			bool operator ==(const Point &point) const
			{
				return x == point.x && y == point.y;
			}

			bool operator !=(const Point &point) const
			{
				return x != point.x && y != point.y;
			}

			template <class P>
			P convertTo()
			{
				return P(x, y);
			}
	};
}
