package sk.hackcraft.als.utils.application;

/**
 * Interface defining autonomous component in application. This component can be
 * started and stopped, and it's working should be independent.
 */
public interface ApplicationService
{
	/**
	 * Starts this service. Service should count as running immediately after
	 * calling this method. If service is running, nothing should happen.
	 * Implementation of this method should be threadsafe.
	 * 
	 * @throws IllegalStateException if service is running
	 */
	void start();

	/**
	 * Notifies this service that it should stop. Time and way of stopping
	 * depends of service. Service should be count as stopped immediately after
	 * calling this method. If service is stopped, nothing should happen.
	 * Implementation of this method should be threadsafe.
	 * 
	 * @throws IllegalStateException if service is not running
	 */
	void stop();

	/**
	 * Gets service running status.
	 * 
	 * @return true if service is running, false otherwise
	 */
	boolean isRunning();
}
