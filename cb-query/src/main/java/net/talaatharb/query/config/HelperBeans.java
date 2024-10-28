package net.talaatharb.query.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.talaatharb.query.facade.CBQueryFacade;
import net.talaatharb.query.facade.CBQueryFacadeImpl;
import net.talaatharb.query.service.CBConnectionService;
import net.talaatharb.query.service.CBConnectionServiceImpl;
import net.talaatharb.query.service.CBQueryService;
import net.talaatharb.query.service.CBQueryServiceImpl;

public class HelperBeans {

	private HelperBeans() {
	}

	public static final ObjectMapper buildObjectMapper() {
		return JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS) // ignore case
				.enable(SerializationFeature.INDENT_OUTPUT) // pretty format for json
				.addModule(new JavaTimeModule()) // time module
				.build();
	}

	public static final CBConnectionService buildConnectionService() {
		return new CBConnectionServiceImpl();
	}

	public static final CBQueryService buildCopierService() {
		return new CBQueryServiceImpl();
	}

	public static final CBQueryFacade buildCopierFacade(CBConnectionService connectionService,
			CBQueryService copierService) {
		return new CBQueryFacadeImpl(connectionService, copierService);
	}

}
