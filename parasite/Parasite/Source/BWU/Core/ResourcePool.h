#pragma once

#include "BWU/Core/Priority.h"

namespace BWU
{
	class ResourcePool
	{
		public:
			class RequestHandle
			{
				private:
					bool canceled;
				public:
					RequestHandle()
					: canceled(false)
					{}

					virtual ~RequestHandle() {};

					virtual void onResourcesGranted(int quantity) = 0;

					void cancel()
					{
						canceled = true;
					}

					bool isCanceled()
					{
						return canceled;
					}
			};

		public:
			virtual ~ResourcePool() {};

			virtual void modifyQuantityBy(int quantity) = 0;
			virtual int getAvailableQuantity() = 0;

			virtual void postRequest(int count, Priority::Enum priority, RequestHandle *handle) = 0;
	};
}
