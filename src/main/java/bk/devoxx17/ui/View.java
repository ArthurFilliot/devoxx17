package bk.devoxx17.ui;

import java.util.Timer;
import java.util.TimerTask;

import bk.devoxx17.global.ApplicationScope;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class View {
	
	private final static KeyCodeCombination ENTER_FULLSCREEN_CODE = new KeyCodeCombination(KeyCode.F,
			KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination EXIT_FULLSCREEN_CODE = new KeyCodeCombination(KeyCode.G,
			KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination SHOWHIDE_MENU = new KeyCodeCombination(KeyCode.M,
            KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination RESET_GAME = new KeyCodeCombination(KeyCode.N,
			KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination STOP_GAME = new KeyCodeCombination(KeyCode.S,
			KeyCombination.CONTROL_DOWN);

	private static Label resultLabel;
    private static Label timerLabel;
    private static Label scoreLabel;
	private MenuBar menuBar = new MenuBar();
	private TextField loginTxt = new TextField();
	private CheckBox checkbox = new javafx.scene.control.CheckBox();
	private PasswordField  passwordTxt = new PasswordField ();
	private TextField dispPwd = new TextField();
	private Button connectBtn = new Button();
	private Menu mainMenu = new Menu("File");
	private MenuItem fullscreenCmd = new MenuItem("Fullscreen");
	private MenuItem menuCmd = new MenuItem("Show/Hide menus");
	private MenuItem menuStop = new MenuItem("Stop Game");
	private MenuItem menuReset = new MenuItem("Reset Game");

	private static boolean gameRunning = false;
	private static final Timer timer = new Timer();
	    
	public View() {}
	
	public void draw(final Stage primaryStage) {
		Controller.initKonamiCode();

		primaryStage.setTitle("Big Kahuna Log Hack Game");

		/**
		 * Setup Listeners.
		 */
		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> Controller.logKey(event));
		primaryStage.setFullScreenExitKeyCombination(EXIT_FULLSCREEN_CODE);
		fullscreenCmd.setOnAction(event -> toggleFullScreenMode());
		fullscreenCmd.setAccelerator(ENTER_FULLSCREEN_CODE);
		menuCmd.setOnAction(event -> toggleShowMenuBar());
		menuCmd.setAccelerator(SHOWHIDE_MENU);
		menuReset.setOnAction(event -> resetGame());
		menuReset.setAccelerator(RESET_GAME);
		menuStop.setOnAction(event -> stopGame());
		menuStop.setAccelerator(STOP_GAME);
		connectBtn.setOnAction(event -> doTry());
		
		/**
		 * Build Menu.
		 */
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		mainMenu.getItems().addAll(fullscreenCmd, menuCmd, menuReset, menuStop);
		menuBar.getMenus().add(mainMenu);

		/**
		 * Create, fill a Grid and package it into a Group
		 */
		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.add(new Label("Login: "), 0, 0);
		grid.add(loginTxt, 1, 0);				
		grid.add(new Label("Password: "), 0, 1);
		grid.add(passwordTxt, 1, 1);
		passwordTxt.managedProperty().bind(checkbox.selectedProperty().not());
		passwordTxt.visibleProperty().bind(checkbox.selectedProperty().not());
		dispPwd.managedProperty().bind(checkbox.selectedProperty());
		dispPwd.visibleProperty().bind(checkbox.selectedProperty());
		dispPwd.textProperty().bindBidirectional(passwordTxt.textProperty());
		grid.add(dispPwd, 1, 1);
		grid.add(checkbox, 2, 1);
		
		connectBtn.setText("Connect");
		grid.add(connectBtn, 1, 2);

        timerLabel = new Label("05:00:00");
        timerLabel.setVisible(true);
        grid.add(timerLabel, 0, 4, 2, 1);
        
        scoreLabel = new Label("0");
        scoreLabel.setVisible(true);
        grid.add(scoreLabel, 0, 5, 2, 1);

		resultLabel = new Label("ErrorText");
		resultLabel.setTextFill(Color.web("#EE0000"));
		resultLabel.setVisible(false);
		grid.add(resultLabel, 0, 3, 2, 1);
		Group group = new Group();
		group.getChildren().add(grid);

		BorderPane root = new BorderPane();
		root.setTop(menuBar);
		root.setCenter(group);
		primaryStage.setScene(new Scene(root, 300, 250));
	}
	
	private void toggleFullScreenMode() {
		menuBar.setVisible(!menuBar.isVisible());
	}
	
	private void toggleShowMenuBar() {
		menuBar.setVisible(!menuBar.isVisible());
	}
	
	private static void resetGame() {
		if(!gameRunning) {
			gameRunning = true;
			Controller.downloadTimer.resetTimer(5, 0);
			Controller.initStopWatch();

			try {
				TimerTask timerTask = new TimerTask() {
					@Override
					public void run() {
						tickTimer(timer);
					}
				};
				timer.scheduleAtFixedRate(timerTask, 0, 1000);
			}
			catch (Exception e) {
			}
		}
	}

	private static void stopGame() {
		if(gameRunning) {
			gameRunning = false;
			Controller.downloadTimer.stop();
		}
	}
	
	private static void tickTimer(Timer timer) {
		Platform.runLater(new Runnable() {
			public void run() {
				timerLabel.setText(Controller.downloadTimer.getTime());
			}
		});
		if (Controller.downloadTimer.getIsActive() == false){
            timer.cancel();
            timer.purge();
        } else {

        }
	}
	
	private void doTry() {
//		log.info("Typed login/password: " + loginTxt.getText() + "/" + passwordTxt.getText());
		ApplicationScope.getInstance().setErrorMessage(null);
		ApplicationScope.getInstance().setFoundMethodMessage(null);
		boolean result = Controller.check(loginTxt.getText(), passwordTxt.getText());
		if (result) {
			if (ApplicationScope.getInstance().getFoundMethodMessage()!=null) {
				resultLabel.setTextFill(Color.web("#00EE00"));
				resultLabel.setVisible(true);
				resultLabel.setText(ApplicationScope.getInstance().getFoundMethodMessage());
				scoreLabel.setText(ApplicationScope.getInstance().getScore().toString());
			}else{
				resultLabel.setVisible(false);
			}
		}else if (ApplicationScope.getInstance().getErrorMessage()!=null) {
			resultLabel.setTextFill(Color.web("#EE0000"));
			resultLabel.setVisible(true);
			resultLabel.setText(ApplicationScope.getInstance().getErrorMessage());
		}else{
			resultLabel.setVisible(false);
		}
//		log.info("Result:" + (result ? "OK" : "KO"));
	}
}
