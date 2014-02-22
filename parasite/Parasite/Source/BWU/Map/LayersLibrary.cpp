#include "BWU/Map/LayersLibrary.h"

#include <cassert>

#include "BWU/Map/Layer.h"

using std::make_pair;
using std::map;
using std::string;

namespace BWU
{
	LayersLibrary::LayersLibrary()
	{
	}

	LayersLibrary::~LayersLibrary()
	{
		map<string, Layer*>::iterator it;
		for (it = layers.begin(); it != layers.end(); it++)
		{
			Layer *layer = it->second;
			delete layer;
		}
	}

	void LayersLibrary::addLayer(string name, Layer* layer)
	{
		layers.insert(make_pair(name, layer));
	}

	void LayersLibrary::removeLayer(string name)
	{
		assert(layers.find(name) != layers.end());

		layers.erase(name);
	}
}
