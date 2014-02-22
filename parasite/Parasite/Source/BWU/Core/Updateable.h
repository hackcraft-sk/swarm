#pragma once

namespace BWU
{
	class Updateable
	{
		public:
			virtual ~Updateable() {};

			virtual void update() = 0;
	};
}
