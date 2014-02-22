#include "BitmapLayer.h"

namespace BWU
{
	BitmapLayer::BitmapLayer(Dimension size, int initialValue)
	: size(size)
	{
		int width = size.getWidth();
		int height = size.getHeight();

		matrix = new int[width * height];
		for (int i = 0; i < width * height; i++)
		{
			matrix[i] = initialValue;
		}
	}

	BitmapLayer::BitmapLayer(Layer &layer)
	{
		this->size = layer.getSize();

		int width = size.getWidth();
		int height = size.getHeight();

		matrix = new int[width * height];

		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				Point position(x, y);

				int index = positionToIndex(position);
				matrix[index] = layer.getValue(position);
			}
		}
	}

	BitmapLayer::~BitmapLayer()
	{
		delete [] matrix;
	}

	Dimension BitmapLayer::getSize() const
	{
		return size;
	}

	void BitmapLayer::setValue(Point position, int value)
	{
		int index = positionToIndex(position);
		matrix[index] = value;
	}

	int BitmapLayer::getValue(Point position) const
	{
		int index = positionToIndex(position);
		return matrix[index];
	}

	int BitmapLayer::positionToIndex(Point position) const
	{
		if (!isValid(position))
		{
			throw new InvalidPointException();
		}

		return position.getX() * size.getWidth() + position.getY();
	}

	bool BitmapLayer::isValid(Point position) const
	{
		int x = position.getX();
		int y = position.getX();

		int width = size.getWidth();
		int height = size.getHeight();

		return x >= 0 && x < width && y >= 0 && y < height;
	}
}
