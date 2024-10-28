package net.talaatharb.query.service;

import java.io.IOException;
import java.util.Properties;

import org.springframework.data.couchbase.core.CouchbaseTemplate;

public interface CBConnectionService {

	String SCOPE = "scope";
	String BUCKET = "bucket";
	String PASS = "pass";
	String USER = "user";
	String CONNECTION = "connection";

	String SRC_CONNECTION_FILE = "./src.properties";

	CouchbaseTemplate connect(String connectionFile);

	void editConnectionDetails(Properties properties, String connectionFile);

	Properties loadConnectionDetails(String string) throws IOException;
}
