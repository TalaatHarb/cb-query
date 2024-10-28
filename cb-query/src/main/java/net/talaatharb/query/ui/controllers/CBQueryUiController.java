package net.talaatharb.query.ui.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.query.config.HelperBeans;
import net.talaatharb.query.facade.CBQueryFacade;

@Slf4j
@RequiredArgsConstructor
public class CBQueryUiController implements Initializable {

	private static final String EDIT_WINDOW_FXML = "../EditWindow.fxml";

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Button connectButton;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Label connectionStatus;


	private final CBQueryFacade copierFacade;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Button editButton;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Button fetchButton;
	
	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Button fetchPreparedButton;

	private final ObjectMapper objectMapper;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private TextArea queryTextArea;
	
	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private TextArea parametersTextArea;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private TextArea resultTextArea;

	private JsonNode result;

	public CBQueryUiController() {
		objectMapper = HelperBeans.buildObjectMapper();
		copierFacade = HelperBeans.buildCopierFacade(HelperBeans.buildConnectionService(),
				HelperBeans.buildCopierService());
	}

	@FXML
	void connect() {
		final boolean connectionResult = copierFacade.connect();
		if (connectionResult) {
			log.info("Connection Successful...");

			connectionStatus.setStyle("-fx-background-color: #00ff00");

			connectButton.setDisable(true);
			editButton.setDisable(true);

			fetchButton.setDisable(false);
			fetchPreparedButton.setDisable(false);

		} else {
			log.info("Connection Failed...");
			connectionStatus.setStyle("-fx-background-color: #ff0000");
		}
	}

	@FXML
	void editConnection() {
		log.info("Edit connection");
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(EDIT_WINDOW_FXML));
		Scene newScene;
		try {
			final Parent root = loader.load();
			newScene = new Scene(root);
		} catch (final IOException ex) {
			log.error(ex.getMessage());
			return;
		}

		final Stage editConnectionWindow = new Stage();
		editConnectionWindow.initOwner(null);
		editConnectionWindow.setScene(newScene);
		editConnectionWindow.showAndWait();
	}

	@FXML
	void fetchUsingQuery() throws IOException {
		log.info("Fetch data using the query");
		final String resultString = copierFacade.fetchUsingQuery(queryTextArea.getText());
		result = objectMapper
				.readTree(new ByteArrayInputStream(resultString.getBytes(StandardCharsets.UTF_8)));
		resultTextArea.setText(objectMapper.writeValueAsString(result));
	}
	
	@FXML
	void fetchUsingQueryAndParameters() throws IOException {
		log.info("Fetch data using the query and parameters");
		
		final var parameters = Arrays.stream(parametersTextArea.getText().split("\\n")).map(s -> {
			String[] keyValue = s.split("=");
			if(keyValue.length != 2) {
				return null;
			}
			return keyValue;
		}).filter(o -> o != null).collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
		
		final String resultString = copierFacade.fetchUsingQuery(queryTextArea.getText(), parameters);
		result = objectMapper
				.readTree(new ByteArrayInputStream(resultString.getBytes(StandardCharsets.UTF_8)));
		resultTextArea.setText(objectMapper.writeValueAsString(result));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("Initializing UI application Main window controller...");
	}
}
