#pragma once

#include <BWAPI.h>

#include "BWU/Core/Canvas.h"

namespace BWU
{
	class CanvasBase : public Canvas
	{
		protected:
			BWAPI::Position position;
			BWAPI::Color color;
			bool solid;
			int textSize;

			CanvasBase();
		public:
			Canvas& setPosition(BWAPI::Position position);
			Canvas& setPosition(int x, int y);
			Canvas& setColor(BWAPI::Color color);
			Canvas& setSolid(bool solid);
			Canvas& setTextSize(TextSize::Enum size);

			virtual void reset();

			virtual Canvas& drawText(const char *format, ...) = 0;
			virtual Canvas& drawText(std::string text) = 0;
			virtual Canvas& drawBox(Dimension size) = 0;
			virtual Canvas& drawBox(int width, int height) = 0;
			virtual Canvas& drawCircle(int radius) = 0;
			virtual Canvas& drawEllipse(int radiusX, int radiusY) = 0;
			virtual Canvas& drawDot() = 0;
			virtual Canvas& drawLine(BWAPI::Position where) = 0;
			virtual Canvas& drawLine(int destX, int destY) = 0;
			virtual Canvas& drawTriangle(BWAPI::Position pointB, BWAPI::Position pointC) = 0;
			virtual Canvas& drawTriangle(int bX, int bY, int cX, int cY) = 0;
	};
}
