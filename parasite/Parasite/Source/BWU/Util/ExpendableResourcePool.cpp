#include "BWU/Util/ExpendableResourcePool.h"

#include <BWAPI.h>

namespace BWU
{
	ExpendableResourcePool::ExpendableResourcePool()
	: availableQuantity(0)
	{
	}

	void ExpendableResourcePool::modifyQuantityBy(int additionalQuantity)
	{
		availableQuantity += additionalQuantity;
	}

	int ExpendableResourcePool::getAvailableQuantity()
	{
		return availableQuantity;
	}

	void ExpendableResourcePool::postRequest(int quantity, Priority::Enum priority, RequestHandle *handle)
	{
		unsigned int id = idGenerator.getNextId();

		Request *request = new Request(id, this, quantity, priority, handle);
		newRequests.push_back(request);
	}

	void ExpendableResourcePool::update()
	{
		addNewRequests();
		processRequests();
	}

	void ExpendableResourcePool::addNewRequests()
	{
		std::list<Request*>::const_iterator it;
		for (it = newRequests.begin(); it != newRequests.end(); it++)
		{
			Request *request = (*it);
			requestsQueue.push(request);
		}
		newRequests.clear();
	}

	void ExpendableResourcePool::processRequests()
	{
		while(!requestsQueue.empty())
		{
			Request *request = requestsQueue.top();

			if (request->handle->isCanceled())
			{
				popAndDeleteRequest(request);
			}
			else if (request->quantity <= availableQuantity)
			{
				availableQuantity -= request->quantity;

				request->handle->onResourcesGranted(request->quantity);

				popAndDeleteRequest(request);
			}
			else
			{
				break;
			}
		}
	}

	void ExpendableResourcePool::popAndDeleteRequest(Request *request)
	{
		requestsQueue.pop();
		delete request;
	}
}
