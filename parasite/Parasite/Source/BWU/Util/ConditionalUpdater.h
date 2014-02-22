#pragma once

#include "BWU/Core/Updateable.h"

namespace BWU
{
	class ConditionalUpdater : public Updateable
	{
		private:
			Updateable *updateable;
		protected:
			virtual bool checkCondition() = 0;
		public:
			ConditionalUpdater(Updateable *updateable)
			: updateable(updateable)
			{
			}

			virtual ~ConditionalUpdater() {};

			void update()
			{
				if (checkCondition())
				{
					updateable->update();
				}
			}
	};
}
