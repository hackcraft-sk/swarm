#pragma once

#include <string>
#include <map>

namespace BWU
{
	class Layer;

	class LayersLibrary
	{
		private:
			std::map<std::string, Layer*> layers;
		public:
			LayersLibrary();
			~LayersLibrary();

			void addLayer(std::string name, Layer *layer);
			void removeLayer(std::string name);
	};
}
