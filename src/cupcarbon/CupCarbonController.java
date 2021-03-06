package cupcarbon;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import action.CupAction;
import action.CupActionAddRadioModule;
import action.CupActionBlock;
import action.CupActionDeleteRadioModule;
import action.CupActionModifDeviceElevation;
import action.CupActionModifDeviceGpsFile;
import action.CupActionModifDeviceId;
import action.CupActionModifDeviceLatitude;
import action.CupActionModifDeviceLongitude;
import action.CupActionModifDeviceNatEventFile;
import action.CupActionModifDeviceRadius;
import action.CupActionModifDeviceScriptFile;
import action.CupActionModifRadioCh;
import action.CupActionModifRadioDataRate;
import action.CupActionModifRadioEListen;
import action.CupActionModifRadioERx;
import action.CupActionModifRadioESleep;
import action.CupActionModifRadioETx;
import action.CupActionModifRadioMy;
import action.CupActionModifRadioNId;
import action.CupActionModifRadioRadius;
import action.CupActionModifRadioSF;
import action.CupActionModifSensorCurrentRadio;
import action.CupActionModifSensorDrift;
import action.CupActionModifSensorElevation;
import action.CupActionModifSensorEnergyMax;
import action.CupActionModifSensorGpsFile;
import action.CupActionModifSensorId;
import action.CupActionModifSensorLatitude;
import action.CupActionModifSensorLongitude;
import action.CupActionModifSensorRadius;
import action.CupActionModifSensorScriptFile;
import action.CupActionModifSensorUART;
import action.CupActionModifSensorUnitESensing;
import action.CupActionModifSensorUnitRadius;
import action.CupActionStack;
import arduino.Arduino;
import buildings.BuildingList;
import device.Device;
import device.DeviceList;
import device.SensorNode;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import map.MapLayer;
import map.NetworkParameters;
import map.WorldMap;
import markers.MarkerList;
import markers.Routes;
import perso.ExampleClass;
import perso.MonAlgoClass;
import perso.MyClass;
import project.Project;
import radio_module.RadioModule;
import radio_module.RadioModule_Lora;
import simulation.FaultInjector;
import solver.EnvelopeJarvis;
import solver.EnvelopeLPCN;
import solver.NetworkCenter;
import solver.NetworkEnvelopeC;
import solver.NetworkEnvelopeP;
import solver.NetworkPerso;
import solver.SensorSetCover;
import visibility.VisibilityLauncher;
import wisen_simulation.SimulationInputs;
import wisen_simulation.WisenSimulation;

public class CupCarbonController implements Initializable {

	private WorldMap map;
	private SwingNode sn;
	protected FaultInjector faultInjector = null;

	@FXML
	public Button saveButton;

	@FXML
	public MenuItem openRecentProjectItem1;

	@FXML
	public MenuItem openRecentProjectItem2;

	@FXML
	public MenuItem openRecentProjectItem3;

	@FXML
	public MenuItem openRecentProjectItem4;

	@FXML
	public MenuItem openRecentProjectItem5;

	@FXML
	public MenuItem openRecentProjectItem6;

	@FXML
	public MenuItem openRecentProjectItem7;

	@FXML
	public MenuItem openRecentProjectItem8;

	@FXML
	public MenuItem openRecentProjectItem9;

	@FXML
	public MenuItem openRecentProjectItem10;

	@FXML
	public TextField resultsWPeriod;

	@FXML
	public ComboBox<String> radio_spreading_factor;

	@FXML
	public Button spreading_factor_apply_button;

	@FXML
	public Label simulationTimeLabel;

	@FXML
	public MenuItem resetItem;

	@FXML
	public MenuItem newProjectItem;

	@FXML
	public MenuItem newProjectFromCurrentItem;

	@FXML
	public MenuItem openProjectItem;

	@FXML
	public MenuItem openLastProjectItem;

	@FXML
	public MenuItem saveProjectItem;

	@FXML
	public MenuItem undoItem;

	@FXML
	public MenuItem redoItem;

	@FXML
	public MenuItem selectAllItem;

	@FXML
	public TitledPane markerPane;

	@FXML
	public TitledPane radioParamPane;

	@FXML
	public TitledPane deviceParamPane;

	@FXML
	public ComboBox<String> scriptFileComboBox;

	@FXML
	public ComboBox<String> gpsFileComboBox;

	@FXML
	public ComboBox<String> nateventFileComboBox;

	@FXML
	public ComboBox<String> uartComboBox;

	@FXML
	public CheckBox visibilityCheckBox;

	@FXML
	public CheckBox ackCheckBox;

	@FXML
	public CheckBox symmetricalLinkCheckBox;

	@FXML
	public CheckBox clockDriftCheckBox;

	@FXML
	public CheckBox logCheckBox;

	@FXML
	public CheckBox resultsCheckBox;

	@FXML
	public CheckBox ackShowCheckBox;

	@FXML
	public ComboBox<String> probaComboBox;

	@FXML
	public TextField probaTextField;

	@FXML
	public TextField simulationTimeTextField;

	@FXML
	public CheckBox mobilityCheckBox;

	@FXML
	public TextField simulationSpeedTextField;

	@FXML
	public TextField arrowSpeedTextField;

	@FXML
	public TextField txtFileName;

	@FXML
	public TextField txtTitle;

	@FXML
	public TextField txtFrom;

	@FXML
	public TextField txtTo;

	@FXML
	public CheckBox loopCheckBox;

	@FXML
	public TextField loopAfter;

	@FXML
	public TextField loopNumber;

	@FXML
	public TextField bytxtfield;

	@FXML
	public TitledPane acc1;

	@FXML
	public StackPane pane;

	@FXML
	public Accordion accord;

	@FXML
	public ProgressBar progress;

	@FXML
	public ComboBox<RadioStandardModel> radioStdComboBox;

	@FXML
	public ComboBox<String> radioNameComboBox;

	@FXML
	public ListView<String> radioListView;

	@FXML
	public SplitPane split;

	@FXML
	public MenuBar menuBar;

	// Radio attributes
	@FXML
	public TextField radio_nid;

	@FXML
	public TextField radio_my;

	@FXML
	public TextField radio_ch;

	@FXML
	public TextField radio_radius;

	@FXML
	public TextField radio_etx;

	@FXML
	public TextField radio_erx;

	@FXML
	public TextField radio_esleep;

	@FXML
	public TextField radio_elisten;

	@FXML
	public TextField radio_drate;

	@FXML
	public CheckMenuItem addSelectionMenuItem;

	@FXML
	public CheckBox addSelectionCheckBox;

	@FXML
	public Label sensorName;

	@FXML
	public Label numberOfDevices;

	@FXML
	public ListView<String> gpsListView;

	@FXML
	public ListView<String> deviceListView;

	@FXML
	public ListView<String> eventListView;

	@FXML
	public void closeLeftMenu() {
		split.setDividerPositions(0.0);
		map.repaint();
		mapFocus();
	}

	@FXML
	public void openLeftMenu() {
		split.setDividerPositions(0.19);
		map.repaint();
		mapFocus();
	}

	public void mapFocus() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				numberOfDevices.setText("N = " + DeviceList.getSize());
				sn.requestFocus();
			}
		});
	}

	public void splitPaneShowHide() {
		if (split.getDividerPositions()[0] > 0.1)
			split.setDividerPositions(0.0);
		else
			split.setDividerPositions(0.19);
		map.repaint();
		mapFocus();
	}

	public void initComboBoxes() {
		radio_spreading_factor.getItems().removeAll(radio_spreading_factor.getItems());
		radio_spreading_factor.getItems().addAll("", "7", "8", "9", "10", "11", "12");
		radio_spreading_factor.getSelectionModel().select(0);

		radioStdComboBox.getItems().removeAll(radioStdComboBox.getItems());
		radioStdComboBox.getItems().addAll(new RadioStandardModel("802.15.4", "ZIGBEE"),
				new RadioStandardModel("WiFi", "WIFI"), new RadioStandardModel("LoRa", "LORA"));
		radioStdComboBox.getSelectionModel().select(0);

		probaComboBox.getItems().removeAll(probaComboBox.getItems());
		probaComboBox.getItems().addAll("Probability", "a-Distribution", "AWGN");
		probaComboBox.getSelectionModel().select(0);

		uartComboBox.getItems().removeAll(uartComboBox.getItems());
		uartComboBox.getItems().addAll("-", "2400", "3600", "4800", "9600", "38400", "115200");
		uartComboBox.getSelectionModel().select(0);

		radioNameComboBox.getItems().removeAll(radioNameComboBox.getItems());
		radioNameComboBox.getItems().addAll("radio1", "radio2", "radio3", "radio4", "radio5", "radio6", "radio7",
				"radio8", "radio9", "radio10");
		radioNameComboBox.getSelectionModel().select(0);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (CupCarbon.macos) {
			selectAllItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.META_DOWN));
			undoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.META_DOWN));
			redoItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.META_DOWN));
			newProjectItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN));
			newProjectFromCurrentItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.META_DOWN, KeyCombination.SHIFT_DOWN));
			openProjectItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
			openLastProjectItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN, KeyCombination.SHIFT_DOWN));
			saveProjectItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
			resetItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.META_DOWN, KeyCombination.SHIFT_DOWN));
			
			menuBar.useSystemMenuBarProperty().set(true);
		}
		initComboBoxes();
			
		deviceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		eventListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		initRecentProjectMenu();

		initMap();

		if(CupCarbon.internetIsAvailable()) {
			System.out.println("Connection: OK");
			WorldMap.changeMap(0);
		}
		else
			System.out.println("Connection: NO");
	}
	
	@FXML
	public void zoomP() {
		map.setZoom(map.getZoomSlider().getValue() - 1);
		SensorNode sensor = null;
		for (SensorNode s : DeviceList.sensors) {
			if (s.isSelected()) {
				sensor = s;
				break;
			}
		}
		if (sensor != null)
			MapLayer.mapViewer.setCenterPosition(new GeoPosition(sensor.getLatitude(), sensor.getLongitude()));

		mapFocus();
	}

	@FXML
	public void zoomM() {
		map.setZoom(map.getZoomSlider().getValue() + 1);
		mapFocus();
	}

	@FXML
	public void simulateAgent() {
		WorldMap.simulate();
	}

	@FXML
	public void simulateAllAgent() {
		WorldMap.simulateAll();
	}

	@FXML
	public void simulateSensors() {
		WorldMap.simulateSensors();
	}

	@FXML
	public void simulateMobiles() {
		WorldMap.simulateMobiles();
	}

	@FXML
	public void stopAgentSimulation() {
		DeviceList.stopAgentSimulation();
	}

	@FXML
	public void initializeAll() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				DeviceList.initAll();
				MapLayer.repaint();
				mapFocus();
			}
		});

	}

	@FXML
	public void runFaultInjection() {
		faultInjector = new FaultInjector();
		faultInjector.start();
	}

	@FXML
	public void stopFaultInjection() {
		if (faultInjector != null)
			faultInjector.stopInjection();
	}

	@FXML
	public void generateArduinoCode() {
		Arduino.generateCode();
	}

	@FXML
	public Menu menuEdition;

	@FXML
	public Menu menuSelection;

	@FXML
	public void openSenScriptWindow() throws IOException {
		// menuEdition.setDisable(true);
		// menuSelection.setDisable(true);
		if (!Project.projectName.equals(""))
			new SenScriptWindow();
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning!");
			alert.setHeaderText(null);
			alert.setContentText("Project must be created first.");
			alert.showAndWait();
		}
	}

	@FXML
	public void openNaturalEventGenerator() throws IOException {
		if (!Project.projectName.equals(""))
			new NaturalEventGenerator();
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning!");
			alert.setHeaderText(null);
			alert.setContentText("Project must be created first.");
			alert.showAndWait();
		}
	}

	@FXML
	public void addSensor() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				WorldMap.addNodeInMap('1');
				// mapFocus();
			}
		});
	}

	@FXML
	public void addGas() {
		WorldMap.addNodeInMap('2');
		mapFocus();
	}
	
	@FXML
	public void addMeteo() {
		if(DeviceList.meteo==null) {
			WorldMap.addNodeInMap('7');
			mapFocus();
		}
	}

	@FXML
	public void addMediaSensor() {
		WorldMap.addNodeInMap('4');
		mapFocus();
	}

	@FXML
	public void addBaseStation() {
		WorldMap.addNodeInMap('5');
		mapFocus();
	}

	@FXML
	public void addMobile() {
		WorldMap.addNodeInMap('6');
		mapFocus();
	}

	@FXML
	public void addMarker() {
		WorldMap.addNodeInMap('8');
		mapFocus();
	}

	@FXML
	public void connection() {
		saveButton.setDisable(false);
		DeviceList.resetPropagations();
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void propagation() {
		saveButton.setDisable(false);
		DeviceList.calculatePropagations();
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void visibility() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				VisibilityLauncher visibility = new VisibilityLauncher();
				visibility.run();
			}
		});
	}

	public WisenSimulation wisenSimulation;

	@FXML
	public void stopSimulation() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				saveButton.setDisable(false);
				qRunSimulationButton.setDefaultButton(true);
				qStopSimulationButton.setDefaultButton(false);
				runSimulationButton.setDisable(false);
				qRunSimulationButton.setDisable(false);
				stateLabel.setText("Ready");
				CupCarbon.cupCarbonController.monitor.setFill(Color.YELLOWGREEN);
				if (wisenSimulation != null)
					wisenSimulation.stopSimulation();
				mapFocus();
			}
		});
	}

	@FXML
	public void runSimulation() {
		if (!Project.projectName.equals(""))
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					saveButton.setDisable(false);
					qRunSimulationButton.setDefaultButton(false);
					qStopSimulationButton.setDefaultButton(true);
					runSimulationButton.setDisable(true);
					qRunSimulationButton.setDisable(true);
					stateLabel.setText("Simulating...");
					monitor.setFill(Color.ORANGERED);
					simulationParametersApply();
					wisenSimulation = new WisenSimulation();
					if (wisenSimulation.ready()) {
						Thread th = new Thread(wisenSimulation);
						th.start();
					} else {
						WisenSimulation.updateButtons();
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("SenScript");
						alert.setHeaderText(null);
						alert.setContentText("Sensors without Script!");
						alert.showAndWait();
						selectSensorsWithoutScript();
					}
				}
			});
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning!");
			alert.setHeaderText(null);
			alert.setContentText("Project must be created first.");
			alert.showAndWait();
		}
		mapFocus();
	}

	@FXML
	public void simulationParametersApply() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				saveButton.setDisable(false);
				SimulationInputs.simulationTime = Double.parseDouble(simulationTimeTextField.getText());
				SimulationInputs.mobilityAndEvents = mobilityCheckBox.isSelected();
				SimulationInputs.visualDelay = Integer.parseInt(simulationSpeedTextField.getText());
				SimulationInputs.resultsWritingPeriod = Double.parseDouble(resultsWPeriod.getText());
				SimulationInputs.arrowsDelay = Integer.parseInt(arrowSpeedTextField.getText());
				SimulationInputs.displayLog = logCheckBox.isSelected();
				SimulationInputs.displayResults = resultsCheckBox.isSelected();
				SimulationInputs.ackType = probaComboBox.getSelectionModel().getSelectedIndex();
				double proba = Double.parseDouble(probaTextField.getText());
				SimulationInputs.ackProba = (proba > 1) ? 1 : proba;
				SimulationInputs.showAckLinks = ackShowCheckBox.isSelected();
				SimulationInputs.ack = ackCheckBox.isSelected();
				SimulationInputs.symmetricalLinks = symmetricalLinkCheckBox.isSelected();
				SimulationInputs.clockDrift = clockDriftCheckBox.isSelected();
				SimulationInputs.visibility = visibilityCheckBox.isSelected();
				MapLayer.repaint();
				mapFocus();
			}
		});
	}

	@FXML
	public void magnetic() throws Exception {
		MapLayer.magnetic = !MapLayer.magnetic;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void loopChecked() {
		if (loopCheckBox.isSelected()) {
			loopAfter.setDisable(false);
			loopNumber.setDisable(false);
		} else {
			loopAfter.setDisable(true);
			loopNumber.setDisable(true);
		}
		mapFocus();
	}

	@FXML
	public void deleteRoute() {
		String fileName = gpsListView.getSelectionModel().getSelectedItem();
		File file = new File(Project.getProjectGpsPath() + File.separator + fileName);

		boolean delete = false;

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Delete " + fileName + "!");
		alert.setHeaderText(null);
		alert.setContentText("Delete the file?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			delete = true;
		}

		if (delete) {
			saveButton.setDisable(false);
			file.delete();
			getListOfRoutes();
			initScriptGpsEventComboBoxes();
			mapFocus();
		}
	}

	@FXML
	public void saveRoute() {
		if (!Project.projectName.equals("")) {
			if (MarkerList.size() > 0) {
				File gpsFolder = new File(Project.getProjectGpsPath());
				File[] gpsFiles = gpsFolder.listFiles();
				int i = 0;
				boolean found = false;
				String fileName = Project.getGpsFileExtension(txtFileName.getText());
				while (i < gpsFiles.length && !found) {
					if (gpsFiles[i].getName().equals(fileName)) {
						found = true;
					}
					i++;
				}
				boolean saveOk = true;
				if (found) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Save " + txtFileName.getText() + " File!");
					alert.setHeaderText(null);
					alert.setContentText("Replace the file?");
					Optional<ButtonType> result = alert.showAndWait();

					if (result.get() == ButtonType.CANCEL) {
						saveOk = false;
					}
				}
				if (saveOk) {
					saveButton.setDisable(false);
					txtFileName.setText(Project.getGpsFileExtension(txtFileName.getText()));
					MarkerList.saveGpsCoords(txtFileName.getText(), txtTitle.getText(), txtFrom.getText(),
							txtTo.getText(), loopCheckBox.isSelected(), Integer.parseInt(loopAfter.getText()),
							Integer.parseInt(loopNumber.getText()));
					getListOfRoutes();
					initScriptGpsEventComboBoxes();
					mapFocus();
				}
			} else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning!");
				alert.setHeaderText(null);
				alert.setContentText("No Markers.");
				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning!");
			alert.setHeaderText(null);
			alert.setContentText("Project must be created first.");
			alert.showAndWait();
		}
	}

	@FXML
	public void ackChecked() {
		if (ackCheckBox.isSelected()) {
			probaComboBox.setDisable(false);
			probaTextField.setDisable(false);
			ackShowCheckBox.setDisable(false);
		} else {
			probaComboBox.setDisable(true);
			probaTextField.setDisable(true);
			ackShowCheckBox.setDisable(true);
		}
		mapFocus();
	}

	@FXML
	public void addRadioStd() {
		String radioName = radioNameComboBox.getSelectionModel().getSelectedItem();
		String radioStd = radioStdComboBox.getSelectionModel().getSelectedItem().getStd();
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				RadioModule radioModule = RadioModule.newRadioModule(sensor, radioName, radioStd);
				CupAction action = new CupActionAddRadioModule(sensor, radioModule);
				block.addAction(action);
				currentDevice = sensor;
			}
		}
		if (block.size() > 0) {
			CupActionStack.add(block);
			CupActionStack.execute();
		}
		getNodeInformations();
		mapFocus();
	}

	@FXML
	public void removeRadioStd() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				if (sensor.getRadioModuleList().size() > 1) {
					RadioModule radioModule = sensor
							.getRadioModuleByName(radioListView.getSelectionModel().getSelectedItem().split(" ")[0]);
					CupAction action = new CupActionDeleteRadioModule(radioModule);
					block.addAction(action);
					currentDevice = sensor;
				}
			}
		}
		if (block.size() > 0) {
			CupActionStack.add(block);
			CupActionStack.execute();
		}
		getNodeInformations();
		mapFocus();
	}

	@FXML
	public void newProject() {
		Stage stage = new Stage();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("New CupCarbon Project");

		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CupCarbon", "*.*"));

		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			Project.newProject(file.getParentFile().toString() + File.separator + file.getName().toString(),
					file.getName().toString(), true);
			CupCarbon.stage.setTitle("CupCarbon " + CupCarbonVersion.VERSION + " [" + file.getName().toString() + "]");
		}
		initScriptGpsEventComboBoxes();
	}

	@FXML
	public void newProjectFromCurrent() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("New CupCarbon Project");

		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CupCarbon", "*.*"));

		File file = fileChooser.showSaveDialog(CupCarbon.stage);
		if (file != null) {
			Project.newProject(file.getParentFile().toString() + File.separator + file.getName().toString(),
					file.getName().toString(), false);
			CupCarbon.stage.setTitle("CupCarbon " + CupCarbonVersion.VERSION + " [" + file.getName().toString() + "]");
		}
	}

	@FXML
	public void openProject() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open CupCarbon file");

		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CupCarbon Sources", "*.cup"));

		File file = fileChooser.showOpenDialog(CupCarbon.stage);
		if (file != null) {
			Project.openProject(file.getParentFile().toString(), file.getName().toString());
			CupCarbon.stage.setTitle("CupCarbon " + CupCarbonVersion.VERSION + " [" + file.getName().toString() + "]");
			openProjectLoadParameters();
		}		
	}

	public void openProjectLoadParameters() {
		CupCarbon.cupCarbonController.simulationParametersApply();
		loadSimulationParams();
		getListOfRoutes();
		updateObjectListView();
		initScriptGpsEventComboBoxes();
		mapFocus();
	}

	@FXML
	public void reset() {
		CupCarbon.stage.setTitle("CupCarbon " + CupCarbonVersion.VERSION);
		Project.reset();
	}

	@FXML
	public void quit() {
		// int n = JOptionPane.showConfirmDialog(map, "Would you like to quit?",
		// "Quit", JOptionPane.YES_NO_OPTION);
		// if (n == 0) {
		System.exit(0);
		// }
	}

	public void initMap() {
		CupCarbon.cupCarbonController = this;
		map = new WorldMap();
		map.getMainMap().setLoadingImage(Toolkit.getDefaultToolkit().getImage("tiles/mer.png"));
		map.setZoomSliderVisible(false);
		map.setZoomButtonsVisible(false);
		map.setMiniMapVisible(false);
		map.getZoomSlider().setSnapToTicks(false);
		map.getZoomSlider().setPaintTicks(false);
		sn = new SwingNode();
		sn.setContent(map);

		pane.getChildren().add(sn);
		sn.requestFocus();
		// map.repaint();
		// mapFocus();
	}

	public void loadSimulationParams() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					FileInputStream in = new FileInputStream(
							Project.projectPath + File.separator + "config" + File.separator + "simulationParams.cfg");
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					simulationTimeTextField.setText(br.readLine().split(":")[1]);
					mobilityCheckBox.setSelected(Boolean.parseBoolean(br.readLine().split(":")[1]));
					simulationSpeedTextField.setText(br.readLine().split(":")[1]);
					arrowSpeedTextField.setText(br.readLine().split(":")[1]);
					logCheckBox.setSelected(Boolean.parseBoolean(br.readLine().split(":")[1]));
					resultsCheckBox.setSelected(Boolean.parseBoolean(br.readLine().split(":")[1]));
					probaComboBox.getSelectionModel().select((int) Double.parseDouble(br.readLine().split(":")[1]));
					probaTextField.setText(br.readLine().split(":")[1]);
					ackShowCheckBox.setSelected(Boolean.parseBoolean(br.readLine().split(":")[1]));
					ackCheckBox.setSelected(Boolean.parseBoolean(br.readLine().split(":")[1]));
					symmetricalLinkCheckBox.setSelected(Boolean.parseBoolean(br.readLine().split(":")[1]));
					clockDriftCheckBox.setSelected(Boolean.parseBoolean(br.readLine().split(":")[1]));
					visibilityCheckBox.setSelected(Boolean.parseBoolean(br.readLine().split(":")[1]));
					resultsWPeriod.setText(br.readLine().split(":")[1]);
					ackChecked();
					br.close();
					in.close();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		});
	}

	@FXML
	public void scriptFileApply() {
		CupActionBlock block = new CupActionBlock();
		String newScriptFileName = scriptFileComboBox.getSelectionModel().getSelectedItem();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String currentScriptFileName = sensor.getScriptFileName();
				CupAction action = new CupActionModifSensorScriptFile((SensorNode) sensor, currentScriptFileName,
						newScriptFileName);
				block.addAction(action);
			}
		}
		for (Device device : DeviceList.devices) {
			if (device.isSelected()) {
				String currentScriptFileName = device.getScriptFileName();
				CupActionModifDeviceScriptFile action = new CupActionModifDeviceScriptFile(device,
						currentScriptFileName, newScriptFileName);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void nateventFileApply() {
		CupActionBlock block = new CupActionBlock();
		String newNatEventFileName = nateventFileComboBox.getSelectionModel().getSelectedItem();
		for (Device device : DeviceList.devices) {
			if (device.isSelected()) {
				String currentNatEventFileName = device.getNatEventFileName();
				CupAction action = new CupActionModifDeviceNatEventFile(device, currentNatEventFileName,
						newNatEventFileName);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void gpsFileApply() {
		CupActionBlock block = new CupActionBlock();
		String newGpsFileName = gpsFileComboBox.getSelectionModel().getSelectedItem();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String currentGpsFileName = sensor.getGPSFileName();
				CupActionModifSensorGpsFile action = new CupActionModifSensorGpsFile((SensorNode) sensor,
						currentGpsFileName, newGpsFileName);
				block.addAction(action);
			}
		}
		for (Device device : DeviceList.devices) {
			if (device.isSelected()) {
				String currentGpsFileName = device.getGPSFileName();
				CupActionModifDeviceGpsFile action = new CupActionModifDeviceGpsFile(device, currentGpsFileName,
						newGpsFileName);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void idApply() {
		CupActionBlock block = new CupActionBlock();
		int newId = Integer.parseInt(device_id.getText());
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				int currentId = sensor.getId();
				CupActionModifSensorId action = new CupActionModifSensorId((SensorNode) sensor, currentId, newId);
				block.addAction(action);
			}
		}
		for (Device device : DeviceList.devices) {
			if (device.isSelected()) {
				int currentId = device.getId();
				CupActionModifDeviceId action = new CupActionModifDeviceId(device, currentId, newId);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void longitudeApply() {
		CupActionBlock block = new CupActionBlock();
		double newLongitude = Double.parseDouble(device_longitude.getText());
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				double currentLongitude = sensor.getLongitude();
				CupActionModifSensorLongitude action = new CupActionModifSensorLongitude((SensorNode) sensor,
						currentLongitude, newLongitude);
				block.addAction(action);
			}
		}
		for (Device device : DeviceList.devices) {
			if (device.isSelected()) {
				double currentLongitude = device.getLongitude();
				CupActionModifDeviceLongitude action = new CupActionModifDeviceLongitude(device, currentLongitude,
						newLongitude);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void latitudeApply() {
		CupActionBlock block = new CupActionBlock();
		double newLatitude = Double.parseDouble(device_latitude.getText());
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				double currentLatitude = sensor.getLatitude();
				CupActionModifSensorLatitude action = new CupActionModifSensorLatitude((SensorNode) sensor,
						currentLatitude, newLatitude);
				block.addAction(action);
			}
		}
		for (Device device : DeviceList.devices) {
			if (device.isSelected()) {
				double currentLatitude = device.getLatitude();
				CupActionModifDeviceLatitude action = new CupActionModifDeviceLatitude(device, currentLatitude,
						newLatitude);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void elevationApply() {
		CupActionBlock block = new CupActionBlock();
		double newElevation = Double.parseDouble(device_elevation.getText());
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				double currentElevation = sensor.getElevation();
				CupActionModifSensorElevation action = new CupActionModifSensorElevation((SensorNode) sensor,
						currentElevation, newElevation);
				block.addAction(action);
			}
		}
		for (Device device : DeviceList.devices) {
			if (device.isSelected()) {
				double currentElevation = device.getElevation();
				CupActionModifDeviceElevation action = new CupActionModifDeviceElevation(device, currentElevation,
						newElevation);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radiusApply() {
		CupActionBlock block = new CupActionBlock();
		double newRadius = Double.parseDouble(device_radius.getText());
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				double currentRadius = sensor.getRadius();
				CupActionModifSensorRadius action = new CupActionModifSensorRadius((SensorNode) sensor, currentRadius,
						newRadius);
				block.addAction(action);
			}
		}
		for (Device device : DeviceList.devices) {
			if (device.isSelected()) {
				double currentRadius = device.getRadius();
				CupActionModifDeviceRadius action = new CupActionModifDeviceRadius(device, currentRadius, newRadius);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void sensorRadiusApply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				double currentSradius = sensor.getSensorUnitRadius();
				double sRadius = Double.parseDouble(sensor_radius.getText());
				CupActionModifSensorUnitRadius action = new CupActionModifSensorUnitRadius(sensor, currentSradius,
						sRadius);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void energyMaxApply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				double currentEmax = sensor.getBatteryLevel();
				double eMax = Double.parseDouble(device_emax.getText());
				CupActionModifSensorEnergyMax action = new CupActionModifSensorEnergyMax(sensor, currentEmax, eMax);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void sensingConsApply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				double currentCons = sensor.getBatteryConsumption();
				double newCons = Double.parseDouble(device_eSensing.getText());
				CupActionModifSensorUnitESensing action = new CupActionModifSensorUnitESensing(sensor, currentCons,
						newCons);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void uartApply() {
		CupActionBlock block = new CupActionBlock();
		long newRate = 0;
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				long currentRate = sensor.getUartDataRate();
				if (uartComboBox.getSelectionModel().getSelectedIndex() != 0) {
					newRate = Long.parseLong(uartComboBox.getSelectionModel().getSelectedItem());
				}
				CupActionModifSensorUART action = new CupActionModifSensorUART(sensor, currentRate, newRate);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void driftApply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				double currentDrift = sensor.getSigmaOfDriftTime();
				double newDrift = Double.parseDouble(device_drift.getText());
				CupActionModifSensorDrift action = new CupActionModifSensorDrift(sensor, currentDrift, newDrift);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_nid_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				int currentNID = rm.getNId();
				int newNID = Integer.parseInt(radio_nid.getText());
				CupActionModifRadioNId action = new CupActionModifRadioNId(rm, currentNID, newNID);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void spreading_factor_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				int currentSF = ((RadioModule_Lora) rm).getSpreadingFactor();
				int newSP = Integer.parseInt(radio_spreading_factor.getSelectionModel().getSelectedItem());
				CupAction action = new CupActionModifRadioSF(rm, currentSF, newSP);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_my_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				int currentMy = rm.getMy();
				int newMy = Integer.parseInt(radio_my.getText());
				CupActionModifRadioMy action = new CupActionModifRadioMy(rm, currentMy, newMy);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_ch_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				int currentCh = rm.getCh();
				int newCh = Integer.parseInt(radio_ch.getText());
				CupActionModifRadioCh action = new CupActionModifRadioCh(rm, currentCh, newCh);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_radius_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				double currentRadius = rm.getRadioRangeRadius();
				double newRadius = Double.parseDouble(radio_radius.getText());
				CupActionModifRadioRadius action = new CupActionModifRadioRadius(rm, currentRadius, newRadius);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_erx_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				double currentERx = rm.getERx();
				double newERx = Double.parseDouble(radio_erx.getText());
				CupActionModifRadioERx action = new CupActionModifRadioERx(rm, currentERx, newERx);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_etx_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				double currentETx = rm.getETx();
				double newETx = Double.parseDouble(radio_etx.getText());
				CupActionModifRadioETx action = new CupActionModifRadioETx(rm, currentETx, newETx);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_esleep_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				double currentESleep = rm.getESleep();
				double newESleep = Double.parseDouble(radio_esleep.getText());
				CupActionModifRadioESleep action = new CupActionModifRadioESleep(rm, currentESleep, newESleep);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_elisten_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				double currentEListen = rm.getEListen();
				double newEListen = Double.parseDouble(radio_elisten.getText());
				CupActionModifRadioEListen action = new CupActionModifRadioEListen(rm, currentEListen, newEListen);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void radio_drate_Apply() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
				String radioName = s.split(" ")[0];
				RadioModule rm = sensor.getRadioModuleByName(radioName);
				int currentDataRate = rm.getRadioDataRate();
				int newDataRate = Integer.parseInt(radio_drate.getText());
				CupActionModifRadioDataRate action = new CupActionModifRadioDataRate(rm, currentDataRate, newDataRate);
				block.addAction(action);
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		MapLayer.repaint();
	}

	@FXML
	public void deleteSelected() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				MapLayer.nodeList.deleteIfSelected();
				MapLayer.markerList.deleteIfSelected();
				MapLayer.buildingList.deleteIfSelected();
				MapLayer.repaint();

				updateObjectListView();

				mapFocus();
			}
		});
	}

	@FXML
	public RadioMenuItem mapItem0;
	@FXML
	public RadioMenuItem mapItem1;
	@FXML
	public RadioMenuItem mapItem2;
	@FXML
	public RadioMenuItem mapItem3;
	@FXML
	public RadioMenuItem mapItem4;
	@FXML
	public RadioMenuItem mapItem5;
	@FXML
	public RadioMenuItem mapItem6;
	@FXML
	public RadioMenuItem mapItem7;
	@FXML
	public RadioMenuItem mapItem8;
	@FXML
	public RadioMenuItem mapItem9;
	@FXML
	public RadioMenuItem mapItem10;
	@FXML
	public RadioMenuItem mapItem11;

	// mapItem1 carte noire
	// std 1

	public void checkMapMenuItem(int idx) {
		switch (idx) {
		case 0:
			mapItem0.setSelected(true);
			break;
		case 1:
			mapItem1.setSelected(true);
			break;
		case 2:
			mapItem2.setSelected(true);
			break;
		case 3:
			mapItem3.setSelected(true);
			break;
		case 4:
			mapItem4.setSelected(true);
			break;
		case 5:
			mapItem5.setSelected(true);
			break;
		case 6:
			mapItem6.setSelected(true);
			break;
		case 7:
			mapItem7.setSelected(true);
			break;
		case 8:
			mapItem8.setSelected(true);
			break;
		case 9:
			mapItem9.setSelected(true);
			break;
		case 10:
			mapItem10.setSelected(true);
			break;
		case 11:
			mapItem11.setSelected(true);
			break;
		}
	}

	@FXML
	public void setMap0() {
		WorldMap.changeMap(0);
	}

	@FXML
	public void setMap1() {
		WorldMap.changeMap(1);
	}

	@FXML
	public void setMap2() {
		WorldMap.changeMap(2);
	}

	@FXML
	public void setMap3() {
		WorldMap.changeMap(3);
	}

	@FXML
	public void setMap4() {
		WorldMap.changeMap(4);
	}

	@FXML
	public void setMap5() {
		WorldMap.changeMap(5);
	}

	@FXML
	public void setMap6() {
		WorldMap.changeMap(6);
	}

	@FXML
	public void setMap7() {
		WorldMap.changeMap(7);
	}

	@FXML
	public void setMap8() {
		WorldMap.changeMap(8);
	}

	@FXML
	public void setMap9() {
		WorldMap.changeMap(9);
	}

	@FXML
	public void setMap10() {
		WorldMap.changeMap(10);
	}

	@FXML
	public void setMap11() {
		WorldMap.changeMap(11);
	}

	@FXML
	public void displayConnections() {
		for (SensorNode d : DeviceList.sensors) {
			d.setHide(1);
		}
		for (Device d : DeviceList.devices) {
			d.setHide(1);
		}
		NetworkParameters.drawRadioLinks = true;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void displayNodes() {
		for (SensorNode d : DeviceList.sensors) {
			d.setHide(1);
		}
		for (Device d : DeviceList.devices) {
			d.setHide(1);
		}
		NetworkParameters.drawRadioLinks = false;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void displaySensors() {
		for (SensorNode d : DeviceList.sensors) {
			d.setHide(4);
		}
		for (Device d : DeviceList.devices) {
			d.setHide(4);
		}
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void displayRadios() {
		NetworkParameters.drawRadioLinks = false;
		for (SensorNode d : DeviceList.sensors) {
			d.setHide(5);
		}
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void displayAll() {
		for (SensorNode d : DeviceList.sensors) {
			d.setHide(0);
		}
		for (Device d : DeviceList.devices) {
			d.setHide(0);
		}
		NetworkParameters.drawRadioLinks = true;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public TextField device_id;

	@FXML
	public TextField device_longitude;

	@FXML
	public TextField device_latitude;

	@FXML
	public TextField device_elevation;

	@FXML
	public TextField device_radius;

	@FXML
	public TextField sensor_radius;

	@FXML
	public TextField device_emax;

	@FXML
	public TextField device_eSensing;

	@FXML
	public TextField device_drift;

	public Device currentDevice;

	public void updateCoords() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (currentDevice != null) {
					getDeviceInformations();
				}
			}
		});
	}

	public void getNodeInformations() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initAllFields();
				currentDevice = null;
				for (Device device : DeviceList.devices) {
					if (device != null) {
						if (device.isSelected() || device.isInside()) {
							currentDevice = device;
							getDeviceInformations();
						}
					}
				}

				for (SensorNode device : DeviceList.sensors) {
					if (device != null) {
						if (device.isSelected() || device.isInside()) {
							currentDevice = device;
							getSensorInformations();
							getRadioList();
						}
					}
				}

			}
		});
	}

	public void getDeviceInformations() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initScriptGpsEventComboBoxes();
				scriptFileComboBox.setValue("");
				gpsFileComboBox.setValue("");
				nateventFileComboBox.setValue("");
				String[] gpsFile;
				String gpsFileName;
				String[] scriptFile;
				String scriptFileName;
				String[] nateventFile;
				String nateventFileName;
				if (!currentDevice.getScriptFileName().equals("")) {
					scriptFile = currentDevice.getScriptFileName().split(Pattern.quote(File.separator));
					scriptFileName = scriptFile[scriptFile.length - 1];
					scriptFileComboBox.setValue(scriptFileName);
				}
				if (!currentDevice.getGPSFileName().equals("")) {
					gpsFile = currentDevice.getGPSFileName().split(Pattern.quote(File.separator));
					gpsFileName = gpsFile[gpsFile.length - 1];
					gpsFileComboBox.setValue(gpsFileName);
				}
				if (!currentDevice.getNatEventFileName().equals("")) {
					nateventFile = currentDevice.getNatEventFileName().split(Pattern.quote(File.separator));
					nateventFileName = nateventFile[nateventFile.length - 1];
					nateventFileComboBox.setValue(nateventFileName);
				}
				device_id.setText(currentDevice.getId() + "");
				device_longitude.setText("" + currentDevice.getLongitude());
				device_latitude.setText("" + currentDevice.getLatitude());
				device_elevation.setText("" + currentDevice.getElevation());
				device_radius.setText("" + currentDevice.getRadius());
				uartComboBox.setValue("" + currentDevice.getUartDataRate());
				device_drift.setText("" + currentDevice.getSigmaOfDriftTime());
			}
		});
	}

	public void getSensorInformations() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (currentDevice != null)
					sensorName.setText(" of " + currentDevice.getName());
				initScriptGpsEventComboBoxes();
				scriptFileComboBox.setValue("");
				gpsFileComboBox.setValue("");
				nateventFileComboBox.setValue("");
				String[] gpsFile;
				String gpsFileName;
				String[] scriptFile;
				String scriptFileName;
				if (currentDevice != null) {
					if (!currentDevice.getScriptFileName().equals("")) {
						scriptFile = currentDevice.getScriptFileName().split(Pattern.quote(File.separator));
						scriptFileName = scriptFile[scriptFile.length - 1];
						scriptFileComboBox.setValue(scriptFileName);
					}
					if (!currentDevice.getGPSFileName().equals("")) {
						gpsFile = currentDevice.getGPSFileName().split(Pattern.quote(File.separator));
						gpsFileName = gpsFile[gpsFile.length - 1];
						gpsFileComboBox.setValue(gpsFileName);
					}
					if (!currentDevice.getNatEventFileName().equals("")) {
						gpsFile = currentDevice.getNatEventFileName().split(Pattern.quote(File.separator));
						gpsFileName = gpsFile[gpsFile.length - 1];
						nateventFileComboBox.setValue(gpsFileName);
					}
					device_id.setText(currentDevice.getId() + "");
					device_longitude.setText("" + currentDevice.getLongitude());
					device_latitude.setText("" + currentDevice.getLatitude());
					device_elevation.setText("" + currentDevice.getElevation());
					device_radius.setText("" + currentDevice.getRadius());
					sensor_radius.setText("" + currentDevice.getSensorUnitRadius());
					device_emax.setText("" + currentDevice.getBatteryLevel());
					device_eSensing.setText("" + currentDevice.getESensing());
					uartComboBox.setValue("" + currentDevice.getUartDataRate());
					device_drift.setText("" + currentDevice.getSigmaOfDriftTime());
				}
			}
		});
	}

	public void initScriptGpsEventComboBoxes() {
		File gpsFiles = new File(Project.getProjectGpsPath());
		String[] s = gpsFiles.list();
		if (s == null)
			s = new String[1];
		gpsFileComboBox.getItems().removeAll(gpsFileComboBox.getItems());
		gpsFileComboBox.getItems().add("");
		for (int i = 0; i < s.length; i++) {
			if (s[i] != null)
				if (!s[i].startsWith("."))
					gpsFileComboBox.getItems().add(s[i]);
		}

		File scriptFiles = new File(Project.getProjectScriptPath());
		s = scriptFiles.list();
		if (s == null)
			s = new String[1];
		scriptFileComboBox.getItems().removeAll(scriptFileComboBox.getItems());
		scriptFileComboBox.getItems().add("");
		for (int i = 0; i < s.length; i++) {
			if (s[i] != null)
				if (!s[i].startsWith("."))
					scriptFileComboBox.getItems().add(s[i]);
		}

		File neteventFiles = new File(Project.getProjectNatEventPath());
		s = neteventFiles.list();
		if (s == null)
			s = new String[1];
		nateventFileComboBox.getItems().removeAll(nateventFileComboBox.getItems());
		nateventFileComboBox.getItems().add("");
		for (int i = 0; i < s.length; i++) {
			if (s[i] != null)
				if (!s[i].startsWith("."))
					nateventFileComboBox.getItems().add(s[i]);
		}
	}

	public void getRadioList() {
		SensorNode sensor = (SensorNode) currentDevice;
		radioListView.getItems().removeAll(radioListView.getItems());
		String s;
		int i = 0;
		int idx = 0;
		for (RadioModule radio : sensor.getRadioModuleList()) {
			s = "";
			if (radio == ((SensorNode) currentDevice).getCurrentRadioModule()) {
				s = "<-";
				idx = i;
			}
			radioListView.getItems().add(radio.getName() + " [" + radio.getStandardName() + "] " + s);
			i++;
		}
		radioListView.getSelectionModel().select(idx);
	}

	@FXML
	public void getRadioInformations() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (!radioListView.getSelectionModel().isEmpty()) {
					String s = radioListView.getItems().get(radioListView.getSelectionModel().getSelectedIndex());
					String radioName = s.split(" ")[0];
					RadioModule rm = ((SensorNode) currentDevice).getRadioModuleByName(radioName);
					radio_my.setText("" + rm.getMy());
					radio_ch.setText("" + rm.getCh());
					radio_nid.setText("" + rm.getNId());
					radio_radius.setText("" + rm.getRadioRangeRadius());
					radio_etx.setText("" + rm.getETx());
					radio_erx.setText("" + rm.getERx());
					radio_esleep.setText("" + rm.getESleep());
					radio_elisten.setText("" + rm.getEListen());
					radio_drate.setText("" + rm.getRadioDataRate());
					if (rm.getSpreadingFactor() == -1) {
						radio_spreading_factor.setDisable(true);
						spreading_factor_apply_button.setDisable(true);
						radio_spreading_factor.getSelectionModel().select(0);
					} else {
						radio_spreading_factor.setDisable(false);
						spreading_factor_apply_button.setDisable(false);
						radio_spreading_factor.getSelectionModel().select("" + rm.getSpreadingFactor());
					}
				}
			}
		});
	}

	@FXML
	public void setCurrentRadio() {
		CupActionBlock block = new CupActionBlock();
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				String currentRadioModule = sensor.getCurrentRadioModule().getName();
				String newRadioModule = radioListView.getSelectionModel().getSelectedItem().split(" ")[0];
				CupAction action = new CupActionModifSensorCurrentRadio(sensor, currentRadioModule, newRadioModule);
				block.addAction(action);
				currentDevice = sensor;
			}
		}
		CupActionStack.add(block);
		CupActionStack.execute();
		getNodeInformations();
		if (DeviceList.propagationsCalculated)
			DeviceList.calculatePropagations();
		MapLayer.repaint();
	}

	@FXML
	public void openHelpWindow() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Help!");
		alert.setHeaderText("Please, visit our web page www.cupcarbon.com");
		alert.setContentText(null);
		alert.showAndWait();
	}

	@FXML
	public void openAboutWindow() throws IOException {
		Stage stage = new Stage();
		stage.setTitle("About CupCarbon");
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(CupCarbon.class.getResource("about.fxml"));
		BorderPane panneau = (BorderPane) loader.load();
		Scene scene = new Scene(panneau);
		stage.setScene(scene);
		stage.show();
	}

	@FXML
	public void myAction() {
		MyClass myclass = new MyClass();
		myclass.start();
	}

	@FXML
	public void example() {
		ExampleClass exc = new ExampleClass();
		exc.start();
	}

	@FXML
	public void monAlgo() {
		MonAlgoClass monAlgo = new MonAlgoClass();
		monAlgo.start();
	}

	@FXML
	public void sensorCoverage() {
		SensorSetCover.sensorSetCover();
	}

	@FXML
	public void targetCoverage() {
		SensorSetCover.sensorTargetSetCover();
	}

	@FXML
	public void networkCenter() {
		NetworkCenter netcenter = new NetworkCenter();
		netcenter.start();
	}

	@FXML
	public void pEnvelope() {
		NetworkEnvelopeP npb = new NetworkEnvelopeP(true);
		npb.start();
	}

	@FXML
	public void pEnvelopeWithConnections() {
		NetworkEnvelopeP npb = new NetworkEnvelopeP(false);
		npb.start();
	}

	@FXML
	public void runJarvisAlgorithm() {
		EnvelopeJarvis envJarvis = new EnvelopeJarvis();
		envJarvis.start();
	}

	@FXML
	public void runLPCNAlgorithm() {
		EnvelopeLPCN lpcn = new EnvelopeLPCN();
		lpcn.start();
	}

	@FXML
	public void runGrahamAlgo() {
		NetworkEnvelopeC npb = new NetworkEnvelopeC();
		npb.start();
	}

	@FXML
	public void runPersonalAlgo() {
		NetworkPerso npb = new NetworkPerso();
		npb.start();
	}

	@FXML
	public void selectAll() {
		WorldMap.setSelectionOfAllNodes(true, -1, true);
		WorldMap.setSelectionOfAllMarkers(true, true);
		deviceListView.getSelectionModel().selectAll();
		eventListView.getSelectionModel().selectAll();
		initScriptGpsEventComboBoxes();
		MapLayer.multipleSelection = true;
		mapFocus();
	}

	@FXML
	public void deselectAll() {
		WorldMap.setSelectionOfAllNodes(false, -1, false);
		WorldMap.setSelectionOfAllMarkers(false, true);
		WorldMap.setSelectionOfAllBuildings(false, false);
		deviceListView.getSelectionModel().clearSelection();
		eventListView.getSelectionModel().clearSelection();
		mapFocus();
	}

	@FXML
	public void selectInv() {
		WorldMap.invertSelection();
		updateSelectionInListView();
	}

	@FXML
	public void selectById() {
		DeviceList.selectById(bytxtfield.getText());
		updateSelectionInListView();
	}

	@FXML
	public void selectByMy() {
		DeviceList.selectByMy(bytxtfield.getText());
		updateSelectionInListView();
	}

	@FXML
	public void selectSensorsWithoutScript() {
		DeviceList.selectWitoutScript();
		updateSelectionInListView();
	}

	@FXML
	public void selectSensorsWithoutGps() {
		DeviceList.selectWitoutGps();
		updateSelectionInListView();
	}

	@FXML
	public void selectMarkedSensors() {
		DeviceList.selectMarkedSensors();
		updateSelectionInListView();
	}

	@FXML
	public void selectAllSensors() {
		WorldMap.setSelectionOfAllNodes(true, Device.SENSOR, addSelectionMenuItem.isSelected());		
		updateSelectionInListView();
	}

	@FXML
	public void selectAllnaturalEvents() {
		WorldMap.setSelectionOfAllNodes(true, Device.GAS, addSelectionMenuItem.isSelected());
		updateSelectionInListView();
	}

	@FXML
	public void selectAllMobiles() {
		WorldMap.setSelectionOfAllNodes(true, Device.MOBILE, addSelectionMenuItem.isSelected());
		updateSelectionInListView();
	}

	@FXML
	public void selectAllMobileSensors() {
		WorldMap.setSelectionOfAllMobileNodes(true, Device.SENSOR, addSelectionMenuItem.isSelected());
		updateSelectionInListView();
	}

	@FXML
	public void selectAllMediaSensors() {
		WorldMap.setSelectionOfAllNodes(true, Device.MEDIA_SENSOR, addSelectionMenuItem.isSelected());
		updateSelectionInListView();
	}

	@FXML
	public void selectAllBaseStations() {
		WorldMap.setSelectionOfAllNodes(true, Device.BASE_STATION, addSelectionMenuItem.isSelected());
		updateSelectionInListView();
	}

	@FXML
	public void selectAllMarkers() {
		WorldMap.setSelectionOfAllMarkers(true, addSelectionMenuItem.isSelected());
		mapFocus();
	}

	@FXML
	public void selectAllBuildings() {
		WorldMap.setSelectionOfAllBuildings(true, addSelectionMenuItem.isSelected());
		updateSelectionInListView();
	}

	@FXML
	public void deselectAllSensors() {
		WorldMap.setSelectionOfAllNodes(false, Device.SENSOR, false);
		updateSelectionInListView();
	}

	@FXML
	public void deselectAllnaturalEvents() {
		WorldMap.setSelectionOfAllNodes(false, Device.GAS, true);
		updateSelectionInListView();
	}

	@FXML
	public void deselectAllMobiles() {
		WorldMap.setSelectionOfAllNodes(false, Device.MOBILE, true);
		updateSelectionInListView();
	}

	@FXML
	public void deselectAllMobileSensors() {
		WorldMap.setSelectionOfAllMobileNodes(false, Device.SENSOR, true);
		updateSelectionInListView();
	}

	@FXML
	public void deselectAllMediaSensors() {
		WorldMap.setSelectionOfAllNodes(false, Device.MEDIA_SENSOR, true);
		updateSelectionInListView();
	}

	@FXML
	public void deselectAllBaseStations() {
		WorldMap.setSelectionOfAllNodes(false, Device.BASE_STATION, true);
		updateSelectionInListView();
	}

	@FXML
	public void deselectAllMarkers() {
		WorldMap.setSelectionOfAllMarkers(false, true);
		updateSelectionInListView();
	}

	@FXML
	public void deselectAllBuildings() {
		WorldMap.setSelectionOfAllBuildings(false, addSelectionCheckBox.isSelected());
		updateSelectionInListView();
	}

	@FXML
	public void routeFromMarkers() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				saveButton.setDisable(false);
				MarkerList.generateOSMRouteFile();
				selectAllMarkers();
				mapFocus();
			}
		});

	}

	public void initAllFields() {
		// device
		scriptFileComboBox.getSelectionModel().select(0);
		gpsFileComboBox.getSelectionModel().select(0);
		nateventFileComboBox.getSelectionModel().select(0);
		device_id.setText("");
		device_longitude.setText("");
		device_latitude.setText("");
		device_elevation.setText("");
		device_radius.setText("");
		sensor_radius.setText("");
		device_emax.setText("");
		device_eSensing.setText("");
		uartComboBox.getSelectionModel().select(0);
		device_drift.setText("");
		// radio
		radioListView.getItems().removeAll(radioListView.getItems());
		radioNameComboBox.getSelectionModel().select(0);
		radio_my.setText("");
		radio_ch.setText("");
		radio_nid.setText("");
		radio_radius.setText("");
		radio_etx.setText("");
		radio_erx.setText("");
		radio_esleep.setText("");
		radio_elisten.setText("");
		radio_drate.setText("");
		radio_spreading_factor.getSelectionModel().select(0);

		numberOfDevices.setText("N = " + DeviceList.getSize());
	}

	public void initListViewSelections() {
		deviceListView.getSelectionModel().clearSelection();
		eventListView.getSelectionModel().clearSelection();
	}

	@FXML
	public void updateObjectListView() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				deviceListView.getItems().removeAll(deviceListView.getItems());
				eventListView.getItems().removeAll(eventListView.getItems());
				for (int i = 0; i < DeviceList.devices.size(); i++) {
					if (DeviceList.devices.get(i).getType() == Device.GAS) {
						eventListView.getItems().add(DeviceList.devices.get(i).getName());
					} else
						deviceListView.getItems().add(DeviceList.devices.get(i).getName());
				}
				for (int i = 0; i < DeviceList.sensors.size(); i++) {
					deviceListView.getItems().add(DeviceList.sensors.get(i).getName());
				}
				mapFocus();
			}
		});
	}

	@FXML
	public void updateSelectionInListView() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				deviceListView.getSelectionModel().clearSelection();
				eventListView.getSelectionModel().clearSelection();

				for (int i = 0; i < DeviceList.devices.size(); i++) {
					if (DeviceList.devices.get(i).getType() == Device.GAS) {
						if (DeviceList.devices.get(i).isSelected()) {
							eventListView.getSelectionModel().select(DeviceList.devices.get(i).getName());
						}
					} else if (DeviceList.devices.get(i).isSelected()) {
						deviceListView.getSelectionModel().select(DeviceList.devices.get(i).getName());
					}
				}

				for (int i = 0; i < DeviceList.sensors.size(); i++) {
					if (DeviceList.sensors.get(i).isSelected()) {
						deviceListView.getSelectionModel().select(DeviceList.sensors.get(i).getName());
					}
				}
				mapFocus();
			}
		});
	}

	@FXML
	public void selectDevicesOfTheListView() {
		DeviceList.deselectAllObjects();
		ObservableList<String> selectedItems = deviceListView.getSelectionModel().getSelectedItems();
		for (String name : selectedItems) {
			DeviceList.getNodeByName(name).setSelected(true);
		}
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void selectEventsOfTheListView() {
		DeviceList.deselectAllEvents();
		ObservableList<String> selectedItems = eventListView.getSelectionModel().getSelectedItems();
		for (String name : selectedItems) {
			DeviceList.getNodeByName(name).setSelected(true);
		}
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void getEventsSelection() {
		ObservableList<String> selectedItems = eventListView.getSelectionModel().getSelectedItems();
		int id = 0;
		for (String s : selectedItems) {
			id = Integer.parseInt(s.replaceAll("[^0-9]", ""));
			DeviceList.getNodeById(id).setSelected(true);
		}
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void getListOfRoutes() {
		gpsListView.getItems().removeAll(gpsListView.getItems());
		File gpsFolder = new File(Project.getProjectGpsPath());
		File[] gpsFiles = gpsFolder.listFiles();
		if (gpsFolder.exists()) {
			for (int i = 0; i < gpsFiles.length; i++) {
				if (!(gpsFiles[i].getName().startsWith("."))) {
					gpsListView.getItems().add(gpsFiles[i].getName());
				}
			}
		}
	}

	@FXML
	public void loadRoute() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {				
				if (!gpsListView.getSelectionModel().isEmpty()) {
					saveButton.setDisable(false);
					String gpsFileName = gpsListView.getItems().get(gpsListView.getSelectionModel().getSelectedIndex());
		
					MarkerList.open(Project.getProjectGpsPath() + File.separator + gpsFileName);
		
					try {
						BufferedReader br = new BufferedReader(new FileReader(Project.getProjectGpsPath() + File.separator + gpsFileName));
						String line;
						txtFileName.setText(gpsFileName);
						line = br.readLine();
						txtTitle.setText(line);
						line = br.readLine();
						txtFrom.setText(line);
						line = br.readLine();
						txtTo.setText(line);
						line = br.readLine();
						loopCheckBox.setSelected(Boolean.valueOf(line));
						if (loopCheckBox.isSelected()) {
							loopAfter.setDisable(false);
							loopNumber.setDisable(false);
						} else {
							loopAfter.setDisable(true);
							loopNumber.setDisable(true);
						}
						line = br.readLine();
						loopNumber.setText(line);
		
						br.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@FXML
	public void loadBuildings() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				saveButton.setDisable(false);
				BuildingList.loadFromOsm();
			}
		});
	}

	@FXML
	public void escape() {
		MapLayer.escape();
	}

	@FXML
	public void addSelectionMIAction() {
		addSelectionCheckBox.setSelected(addSelectionMenuItem.isSelected());
	}

	@FXML
	public void addSelectionCBAction() {
		addSelectionMenuItem.setSelected(addSelectionCheckBox.isSelected());
	}

	@FXML
	public void duplicate() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				MapLayer.duplicate();
				MapLayer.repaint();
			}
		});
	}

	@FXML
	public void generateRandomNetwork50() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				DeviceList.addRandomSensors(50);
				mapFocus();
			}
		});
	}

	@FXML
	public void generateRandomNetwork100() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				DeviceList.addRandomSensors(100);
				mapFocus();
			}
		});
	}

	@FXML
	public void generateRandomNetwork200() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				DeviceList.addRandomSensors(200);
				mapFocus();
			}
		});
	}

	@FXML
	public void drawGraph() throws IOException {
		if (!Project.projectName.equals(""))
			new EnergyDrawWindow();
		else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning!");
			alert.setHeaderText(null);
			alert.setContentText("Project must be created first.");
			alert.showAndWait();
		}
	}

	@FXML
	public void showFileNames() {
		NetworkParameters.drawScriptFileName = !NetworkParameters.drawScriptFileName;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showDetails() {
		NetworkParameters.displayDetails = !NetworkParameters.displayDetails;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showBatteryBufferLevels() {
		for (SensorNode sensor : DeviceList.sensors) {
			if (sensor.isSelected()) {
				sensor.invertDrawBatteryLevel();
			}
		}
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showRadioMessages() {
		NetworkParameters.displayRadioMessages = !NetworkParameters.displayRadioMessages;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showRadioDistances() {
		NetworkParameters.displayRLDistance = !NetworkParameters.displayRLDistance;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showMarkerDistances() {
		NetworkParameters.displayMarkerDistance = !NetworkParameters.displayMarkerDistance;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showLinks() {
		NetworkParameters.drawRadioLinks = !NetworkParameters.drawRadioLinks;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showSensorArrows() {
		NetworkParameters.drawSensorArrows = !NetworkParameters.drawSensorArrows;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showMarkerArrows() {
		NetworkParameters.drawMarkerArrows = !NetworkParameters.drawMarkerArrows;
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void showBuildings() {
		BuildingList.showHideBuildings();
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void nextLinkColor() {
		MapLayer.nextLinkColor('v');
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void prevLinkColor() {
		MapLayer.nextLinkColor('V');
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void selectNodesMarkers() {
		MapLayer.selectNodesMarkers();
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void applyParameters() {
		simulationParametersApply();
		// System.out.println(Project.getTmpProjectNodePath());
		// System.out.println(Project.getTmpProjectRadioPath());
		// String s = Project.getProjectNodePath()+"/sensor_73";
		// SensorNode sn = DeviceList.loadSensor(s);
		// DeviceList.addSensor(sn);
		// MapLayer.repaint();
	}

	@FXML
	public Circle monitor;

	@FXML
	public Label stateLabel;

	@FXML
	public Button runSimulationButton;

	@FXML
	public Button qRunSimulationButton;

	@FXML
	public Button qStopSimulationButton;

	@FXML
	public void undo() {
		CupActionStack.antiExecute();
		if (CupCarbon.cupCarbonController != null) {
			CupCarbon.cupCarbonController.updateObjectListView();
			CupCarbon.cupCarbonController.getNodeInformations();
			CupCarbon.cupCarbonController.getRadioInformations();
			CupCarbon.cupCarbonController.updateSelectionInListView();
		}
		if (DeviceList.propagationsCalculated)
			DeviceList.calculatePropagations();
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void redo() {
		CupActionStack.execute();
		if (CupCarbon.cupCarbonController != null) {
			CupCarbon.cupCarbonController.updateObjectListView();
			CupCarbon.cupCarbonController.getNodeInformations();
			CupCarbon.cupCarbonController.getRadioInformations();
			CupCarbon.cupCarbonController.updateSelectionInListView();
		}
		if (DeviceList.propagationsCalculated)
			DeviceList.calculatePropagations();
		MapLayer.repaint();
		mapFocus();
	}

	@FXML
	public void openRecentProject1() {
		opneIthRecentProject(1);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject2() {
		opneIthRecentProject(2);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject3() {
		opneIthRecentProject(3);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject4() {
		opneIthRecentProject(4);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject5() {
		opneIthRecentProject(5);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject6() {
		opneIthRecentProject(6);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject7() {
		opneIthRecentProject(7);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject8() {
		opneIthRecentProject(8);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject9() {
		opneIthRecentProject(9);
		initRecentProjectMenu();
	}

	@FXML
	public void openRecentProject10() {
		opneIthRecentProject(10);
		initRecentProjectMenu();
	}

	public void initRecentProjectMenu() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new FileInputStream("utils/recent.rec")));

					for (int i = 0; i < 10; i++) {
						String s = br.readLine();
						if (s != null) {
							String name = s.split(":")[1];
							if (i == 0) {
								openRecentProjectItem1.setVisible(true);
								openRecentProjectItem1.setText(name);
							}
							if (i == 1) {
								openRecentProjectItem2.setVisible(true);
								openRecentProjectItem2.setText(name);
							}
							if (i == 2) {
								openRecentProjectItem3.setVisible(true);
								openRecentProjectItem3.setText(name);
							}
							if (i == 3) {
								openRecentProjectItem4.setVisible(true);
								openRecentProjectItem4.setText(name);
							}
							if (i == 4) {
								openRecentProjectItem5.setVisible(true);
								openRecentProjectItem5.setText(name);
							}
							if (i == 5) {
								openRecentProjectItem6.setVisible(true);
								openRecentProjectItem6.setText(name);
							}
							if (i == 6) {
								openRecentProjectItem7.setVisible(true);
								openRecentProjectItem7.setText(name);
							}
							if (i == 7) {
								openRecentProjectItem8.setVisible(true);
								openRecentProjectItem8.setText(name);
							}
							if (i == 8) {
								openRecentProjectItem9.setVisible(true);
								openRecentProjectItem9.setText(name);
							}
							if (i == 9) {
								openRecentProjectItem10.setVisible(true);
								openRecentProjectItem10.setText(name);
							}
						} else {
							if (i == 0) {
								openRecentProjectItem1.setVisible(false);
								openRecentProjectItem1.setText("");
							}
							if (i == 1) {
								openRecentProjectItem2.setVisible(false);
								openRecentProjectItem2.setText("");
							}
							if (i == 2) {
								openRecentProjectItem3.setVisible(false);
								openRecentProjectItem3.setText("");
							}
							if (i == 3) {
								openRecentProjectItem4.setVisible(false);
								openRecentProjectItem4.setText("");
							}
							if (i == 4) {
								openRecentProjectItem5.setVisible(false);
								openRecentProjectItem5.setText("");
							}
							if (i == 5) {
								openRecentProjectItem6.setVisible(false);
								openRecentProjectItem6.setText("");
							}
							if (i == 6) {
								openRecentProjectItem7.setVisible(false);
								openRecentProjectItem7.setText("");
							}
							if (i == 7) {
								openRecentProjectItem8.setVisible(false);
								openRecentProjectItem8.setText("");
							}
							if (i == 8) {
								openRecentProjectItem9.setVisible(false);
								openRecentProjectItem9.setText("");
							}
							if (i == 9) {
								openRecentProjectItem10.setVisible(false);
								openRecentProjectItem10.setText("");
							}
						}
					}
					br.close();
				} catch (FileNotFoundException e) {
					System.err.println("recent.rec doesn't exist");
				} catch (IOException e) {
					System.err.println("error in recent.rec file");
				}
				openProjectLoadParameters();
			}
		});
	}

	@FXML
	public void openLastProject() {
		opneIthRecentProject(1);
		initRecentProjectMenu();
	}

	@FXML
	public void saveProject() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (Project.projectPath.equals("") || Project.projectName.equals("")) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning!");
					alert.setHeaderText(null);
					alert.setContentText("Project must be created or must be open.");
					alert.showAndWait();
				} else {
					Project.saveProject();
					saveButton.setDisable(true);
				}
			}
		});
	}

	public void opneIthRecentProject(int index) {
		// idx = 1 .. 10
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new FileInputStream("utils/recent.rec")));
					String s = "";
					for (int i = 0; i < index; i++)
						s = br.readLine();
					String path = s.split(":")[0];
					String name = s.split(":")[1];
					br.close();

					CupCarbon.stage.setTitle("CupCarbon " + CupCarbonVersion.VERSION + " [" + name + "]");

					Project.openProject(path, name);
				} catch (FileNotFoundException e) {
					System.err.println("recent.rec doesn't exist");
				} catch (IOException e) {
					System.err.println("error in recent.rec file");
				}
				openProjectLoadParameters();
			}
		});
	}
	
	@FXML
	public void insertMarkers() {
		MarkerList.insertMarkers();
	}
	
	@FXML
	public void drawAllRoutes() {
		NetworkParameters.displayAllRoutes = true;
		MarkerList.reset();
		Routes.loadRoutes();
	}
	
	@FXML
	public void hideAllRoutes() {
		NetworkParameters.displayAllRoutes = false;
		Routes.hideAll();
	}

}