#pragma once

#include <queue>
#include <list>

#include "BWU/Core/ResourcePool.h"
#include "BWU/Core/Priority.h"
#include "BWU/Core/Updateable.h"
#include "BWU/Util/NameGenerators/NumberGenerator.h"

namespace BWU
{
	class ExpendableResourcePool : public ResourcePool, public Updateable
	{
		public:
			class Request
			{
				public:
					ResourcePool * const resourcePool;
					RequestHandle * const handle;
					const unsigned int id;
					const Priority::Enum priority;
					const unsigned int quantity;

					Request(unsigned int id, ResourcePool *resourcePool, unsigned int quantity,Priority::Enum priority, RequestHandle *listener)
					: id(id)
					, resourcePool(resourcePool)
					, quantity(quantity)
					, priority(priority)
					, handle(listener)
					{
					}

					~Request()
					{
						delete handle;
					}
			};

			class RequestComparator
			{
				public:
					bool operator()(const Request *request1, const Request *request2)
					{
						if (request1->priority == request2->priority)
						{
							return request1->id > request2->id;
						}
						else
						{
							return request1->priority < request2->priority;
						}
					}
			};
		private:
			NumberGenerator idGenerator;

			int availableQuantity;

			std::list<Request*> newRequests;
			std::priority_queue<Request*, std::vector<Request*>, RequestComparator> requestsQueue;

			void addNewRequests();
			void processRequests();
			void popAndDeleteRequest(Request *request);
		public:
			ExpendableResourcePool();
			void modifyQuantityBy(int quantity);
			int getAvailableQuantity();

			void postRequest(int count, Priority::Enum priority, RequestHandle *handle);

			void update();
	};
}
