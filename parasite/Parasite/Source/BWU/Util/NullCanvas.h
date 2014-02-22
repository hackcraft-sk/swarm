#pragma once

#include "BWU/Core/Canvas.h"

namespace BWU
{
	class NullCanvas : public Canvas
	{
		public:
			virtual Canvas& setPosition(BWAPI::Position position);
			virtual Canvas& setPosition(int x, int y);
			virtual Canvas& setColor(BWAPI::Color color);
			virtual Canvas& setSolid(bool solid);
			virtual Canvas& setTextSize(TextSize::Enum size);

			virtual Canvas& drawText(const char *format, ...);
			virtual Canvas& drawText(std::string text);
			virtual Canvas& drawBox(Dimension size);
			virtual Canvas& drawBox(int width, int height);
			virtual Canvas& drawCircle(int radius);
			virtual Canvas& drawEllipse(int radiusX, int radiusY);
			virtual Canvas& drawDot();
			virtual Canvas& drawLine(BWAPI::Position where);
			virtual Canvas& drawLine(int destX, int destY);
			virtual Canvas& drawTriangle(BWAPI::Position pointB, BWAPI::Position pointC);
			virtual Canvas& drawTriangle(int bX, int bY, int cX, int cY);
	};
}
