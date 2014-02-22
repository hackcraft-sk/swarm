#pragma once

namespace BWU
{
	class Dimension
	{
		private:
			int width, height;
		public:
			Dimension()
			: width(0)
			, height(0)
			{
			}

			Dimension(int width, int height)
			: width(width)
			, height(height)
			{
			}

			int getWidth() const
			{
				return width;
			}

			int getHeight() const
			{
				return height;
			}
	};
}
