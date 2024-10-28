package net.talaatharb.query.service;

import java.util.Map;

import org.springframework.data.couchbase.core.CouchbaseTemplate;

public interface CBQueryService {

	boolean deleteWithFilter(CouchbaseTemplate template, String filter);

	String fetchUsingQuery(CouchbaseTemplate template, String query);

	String fetchUsingQuery(CouchbaseTemplate template, String query, Map<String, String> parameters);
}
