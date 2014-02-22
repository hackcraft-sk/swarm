#pragma once

#include "BWU/Core/Priority.h"
#include "BWU/Core/Id.h"

namespace BWU
{
	class PriorityUniqueRequest
	{
		public:
			class Comparator
			{
				public:
					bool operator()(const PriorityUniqueRequest *request1, const PriorityUniqueRequest *request2) const
					{
						Priority::Enum priority1 = request1->getPriority();
						Priority::Enum priority2 = request2->getPriority();

						if (priority1 == priority2)
						{
							return request1->getId() > request2->getId();
						}
						else
						{
							return priority1 < priority2;
						}
					}
			};
		private:
			Id id;
			Priority::Enum priority;
		public:
			PriorityUniqueRequest(Id id, Priority::Enum priority)
			: id(id)
			, priority(priority)
			{
			}

			Id getId() const
			{
				return id;
			}

			Priority::Enum getPriority() const
			{
				return priority;
			}
	};
}
