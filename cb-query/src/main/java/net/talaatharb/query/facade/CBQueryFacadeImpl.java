package net.talaatharb.query.facade;

import java.util.Map;

import org.springframework.data.couchbase.core.CouchbaseTemplate;

import lombok.RequiredArgsConstructor;
import net.talaatharb.query.service.CBConnectionService;
import net.talaatharb.query.service.CBQueryService;

@RequiredArgsConstructor
public class CBQueryFacadeImpl implements CBQueryFacade {

	private final CBConnectionService connectionService;
	private final CBQueryService queryService;

	private CouchbaseTemplate srcTemplate;

	@Override
	public boolean connect() {
		srcTemplate = connectionService.connect(CBConnectionService.SRC_CONNECTION_FILE);
		return srcTemplate != null;
	}

	@Override
	public String fetchUsingQuery(String query) {
		return queryService.fetchUsingQuery(srcTemplate, query);
	}

	@Override
	public String fetchUsingQuery(String query, Map<String, String> parameters) {
		return queryService.fetchUsingQuery(srcTemplate, query, parameters);
	}
}
