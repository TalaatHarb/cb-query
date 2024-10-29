package net.talaatharb.query.service;

import java.util.Map;

import org.springframework.data.couchbase.core.CouchbaseTemplate;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CBQueryServiceImpl implements CBQueryService {

	@Override
	public String fetchUsingQuery(CouchbaseTemplate template, String query) {
		final QueryResult result = template.getCouchbaseClientFactory().getCluster().query(query);

		if (result != null && !result.rowsAsObject().isEmpty()) {
			return result.rowsAsObject().toString();
		}

		return "{\"result\":\"No results\"}";
	}
	
	@Override
	public String fetchUsingQuery(CouchbaseTemplate template, String queryString, Map<String, String> parameters) {
		
		QueryOptions options = QueryOptions.queryOptions().parameters(JsonObject.from(parameters)).adhoc(false);
		final QueryResult result = template.getCouchbaseClientFactory().getCluster().query(queryString, options);

		if (result != null && !result.rowsAsObject().isEmpty()) {
			return result.rowsAsObject().toString();
		}

		return "{\"result\":\"No results\"}";
	}
}
