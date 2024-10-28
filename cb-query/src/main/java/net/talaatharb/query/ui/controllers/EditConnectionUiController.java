package net.talaatharb.query.ui.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.query.config.HelperBeans;
import net.talaatharb.query.service.CBConnectionService;

@Slf4j
@RequiredArgsConstructor
public class EditConnectionUiController implements Initializable {

	private final CBConnectionService connectionService;

	public EditConnectionUiController() {
		connectionService = HelperBeans.buildConnectionService();
	}

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private TextField srcConnectionText;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private TextField srcUserText;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private PasswordField srcPasswordText;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private TextField srcBucketText;

	@FXML
	@Setter(value = AccessLevel.PACKAGE)
	private TextField srcScopeText;

	@FXML
	void save() {
		log.info("Save Edit");
		final Properties srcProps = new Properties();

		srcProps.setProperty(CBConnectionService.CONNECTION, srcConnectionText.getText());
		srcProps.setProperty(CBConnectionService.USER, srcUserText.getText());
		srcProps.setProperty(CBConnectionService.PASS, srcPasswordText.getText());
		srcProps.setProperty(CBConnectionService.BUCKET, srcBucketText.getText());
		srcProps.setProperty(CBConnectionService.SCOPE, srcScopeText.getText());

		connectionService.editConnectionDetails(srcProps, CBConnectionService.SRC_CONNECTION_FILE);

		getStage().close();
	}

	@FXML
	void cancel() {
		log.info("Cancel Edit");
		getStage().close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("Editing Connection Details...");

		try {
			final Properties srcProps = connectionService
					.loadConnectionDetails(CBConnectionService.SRC_CONNECTION_FILE);

			srcConnectionText.setText(srcProps.getProperty(CBConnectionService.CONNECTION));
			srcUserText.setText(srcProps.getProperty(CBConnectionService.USER));
			srcPasswordText.setText(srcProps.getProperty(CBConnectionService.PASS));
			srcBucketText.setText(srcProps.getProperty(CBConnectionService.BUCKET));
			srcScopeText.setText(srcProps.getProperty(CBConnectionService.SCOPE));

		} catch (final IOException e) {
			log.error(e.getMessage());
		}

	}

	Stage getStage() {
		return (Stage) srcConnectionText.getScene().getWindow();
	}

}
