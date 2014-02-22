#pragma once

namespace BWU
{
	class EnvironmentTime
	{
		public:
			virtual ~EnvironmentTime() {};

			virtual unsigned int getTime() = 0;
	};
}
