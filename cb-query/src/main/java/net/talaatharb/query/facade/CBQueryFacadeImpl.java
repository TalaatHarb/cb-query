package net.talaatharb.query.facade;

import java.util.Map;

import org.springframework.data.couchbase.core.CouchbaseTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.query.service.CBConnectionService;
import net.talaatharb.query.service.CBQueryService;

@Slf4j
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
		log.info("Exexuting query:\n{}", query);
		return queryService.fetchUsingQuery(srcTemplate, query);
	}

	@Override
	public String fetchUsingQuery(String queryString, Map<String, String> parameters) {
		log.info("Exexuting prepared query:\n{}", queryString);
		final var builder = new StringBuilder();
		parameters.entrySet().forEach(kv -> {
			builder.append(kv.getKey());
			builder.append(" = ");
			builder.append(kv.getValue());
			builder.append("\n");
		});
		log.info("With parameters:\n{}", builder.toString());
		return queryService.fetchUsingQuery(srcTemplate, queryString, parameters);
	}
}
