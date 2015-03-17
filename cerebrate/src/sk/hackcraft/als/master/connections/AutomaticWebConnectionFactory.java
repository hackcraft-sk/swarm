package sk.hackcraft.als.master.connections;

import sk.hackcraft.als.utils.Config;

public class AutomaticWebConnectionFactory implements WebConnectionFactory {

	private Config config;
	
	public AutomaticWebConnectionFactory(Config config) {
		this.config = config;
	}
	
	@Override
	public WebConnection createWebConnection() {
		return new AutomaticWebConnection(
				config.getSection("automaticConnection").getPair("bots").getIntValueAsArray(),
				config.getSection("automaticConnection").getPair("mapUrl").getStringValue()
		);
	}
}
