package net.talaatharb.query.facade;

import java.util.Map;

public interface CBQueryFacade {

	boolean connect();

	String fetchUsingQuery(String query);
	
	String fetchUsingQuery(String query, Map<String, String> parameters);
}
