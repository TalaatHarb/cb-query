package net.talaatharb.query.ui.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
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
import net.talaatharb.query.service.JSONSyntaxHighliter;
import net.talaatharb.query.service.SQLSyntaxHighliter;

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

	private final CBQueryFacade queryFacade;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Button editButton;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Button fetchButton;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Button fetchPreparedButton;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private Button fetchWithParametersButton;

	private final ObjectMapper objectMapper;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private CodeArea queryTextArea;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private TextArea parametersTextArea;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private CodeArea resultTextArea;

	private JsonNode result;

	public CBQueryUiController() {
		objectMapper = HelperBeans.buildObjectMapper();
		queryFacade = HelperBeans.buildCopierFacade(HelperBeans.buildConnectionService(),
				HelperBeans.buildCopierService());
	}

	@FXML
	void connect() {
		final boolean connectionResult = queryFacade.connect();
		if (connectionResult) {
			log.info("Connection Successful...");

			connectionStatus.setStyle("-fx-background-color: #00ff00");

			connectButton.setDisable(true);
			editButton.setDisable(true);

			fetchButton.setDisable(false);
			fetchPreparedButton.setDisable(false);
			fetchWithParametersButton.setDisable(false);

		} else {
			log.info("Connection Failed...");
			connectionStatus.setStyle("-fx-background-color: #ff0000");
		}
	}

	@FXML
	void editConnection() {
		log.info("Edit connection");
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(EDIT_WINDOW_FXML));
		Scene editConnectionScene;
		try {
			final Parent root = loader.load();
			editConnectionScene = new Scene(root);
			editConnectionScene.getStylesheets().add(getClass().getResource("../theme.css").toExternalForm());
		} catch (final IOException ex) {
			log.error(ex.getMessage());
			return;
		}

		final Stage editConnectionWindow = new Stage();
		editConnectionWindow.setTitle("Edit connection details");
		editConnectionWindow.initOwner(null);
		editConnectionWindow.setScene(editConnectionScene);
		editConnectionWindow.showAndWait();
	}

	@FXML
	void fetchUsingQuery() throws IOException {
		log.info("Fetch data using the query");
		final String resultString = queryFacade.fetchUsingQuery(queryTextArea.getText());
		result = objectMapper.readTree(new ByteArrayInputStream(resultString.getBytes(StandardCharsets.UTF_8)));
		replaceResults();
	}

	private void replaceResults() throws JsonProcessingException {
		String resultPrettyString = objectMapper.writeValueAsString(result).replace("\r\n", "\n");
		String existingText = resultTextArea.getText();
		if (!resultPrettyString.equals(existingText)) {
			resultTextArea.replaceText(resultPrettyString);
		}
	}

	@FXML
	void fetchUsingQueryAndParameters() throws IOException {
		log.info("Fetch data using the prepared query and parameters");

		final var parameters = getQueryParametersAsMap();

		final String resultString = queryFacade.fetchUsingQuery(queryTextArea.getText(), parameters);
		result = objectMapper.readTree(new ByteArrayInputStream(resultString.getBytes(StandardCharsets.UTF_8)));
		replaceResults();
	}

	private Map<String, String> getQueryParametersAsMap() {
		return Arrays.stream(parametersTextArea.getText().split("\\n")).map(s -> {
			String[] keyValue = s.split("=");
			if (keyValue.length != 2) {
				return null;
			}
			return keyValue;
		}).filter(o -> o != null).collect(Collectors.toMap(kv -> kv[0].strip(), kv -> kv[1].stripLeading()));
	}

	@FXML
	void fetchUsingQueryAndParametersReplaced() throws IOException {
		log.info("Fetch data using the query with parameters replaced");

		var queryString = queryTextArea.getText();
		final var parameters = getQueryParametersAsMap();

		for (var kv : parameters.entrySet()) {
			queryString = queryString.replace('$' + kv.getKey(), "'" + kv.getValue() + "'");
		}

		final String resultString = queryFacade.fetchUsingQuery(queryString);
		result = objectMapper.readTree(new ByteArrayInputStream(resultString.getBytes(StandardCharsets.UTF_8)));
		replaceResults();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("Initializing UI application Main window controller...");

		queryTextArea.setParagraphGraphicFactory(LineNumberFactory.get(queryTextArea)); // Adds line numbers
		queryTextArea.setStyle("-fx-font-size: 14;"); // Set font size/style if needed

		queryTextArea.textProperty().addListener((obs, oldText, newText) -> queryTextArea.setStyleSpans(0,
				SQLSyntaxHighliter.computeHighlighting(newText)));

		resultTextArea.setParagraphGraphicFactory(LineNumberFactory.get(resultTextArea)); // Adds line numbers
		resultTextArea.setStyle("-fx-font-size: 14;"); // Set font size/style if needed

		resultTextArea.textProperty().addListener((obs, oldText, newText) -> Platform
				.runLater(() -> resultTextArea.setStyleSpans(0, JSONSyntaxHighliter.computeHighlighting(newText))));
	}
}
