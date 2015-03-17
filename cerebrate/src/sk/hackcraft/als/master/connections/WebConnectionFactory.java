package sk.hackcraft.als.master.connections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import sk.hackcraft.als.utils.Config;

public interface WebConnectionFactory {
	public static WebConnectionFactory getFromClassAndConfig(String className, Config.Section config) {
		try {
			Class<? extends WebConnectionFactory> clasz = (Class<? extends WebConnectionFactory>)Class.forName(className);
			Constructor<? extends WebConnectionFactory> constructor = clasz.getConstructor(Config.Section.class);
			return constructor.newInstance(config);
		} catch(Exception cause) {
			throw new RuntimeException(cause);
		}
	}
	
	public WebConnection createWebConnection();
}
