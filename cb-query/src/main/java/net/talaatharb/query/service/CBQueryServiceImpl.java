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
	public boolean deleteWithFilter(CouchbaseTemplate template, String filter) {
		final QueryResult result = template.getCouchbaseClientFactory().getCluster()
				.query("DELETE FROM " + template.getBucketName() + " WHERE " + filter);
		return result != null;
	}

	@Override
	public String fetchUsingQuery(CouchbaseTemplate template, String query) {
		final QueryResult result = template.getCouchbaseClientFactory().getCluster().query(query);

		// Assuming the result is not null and has content
		if (result != null && !result.rowsAsObject().isEmpty()) {
			// Convert the result to a String for simplicity
			return result.rowsAsObject().toString();
		}

		return "{\"result\":\"No results\"}";
	}
	
	@Override
	public String fetchUsingQuery(CouchbaseTemplate template, String queryString, Map<String, String> parameters) {
		
		QueryOptions options = QueryOptions.queryOptions().parameters(JsonObject.from(parameters)).adhoc(false);
		final QueryResult result = template.getCouchbaseClientFactory().getCluster().query(queryString, options);

		// Assuming the result is not null and has content
		if (result != null && !result.rowsAsObject().isEmpty()) {
			// Convert the result to a String for simplicity
			return result.rowsAsObject().toString();
		}

		return "{\"result\":\"No results\"}";
	}
}
