package bk.devoxx17.ui;

import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import org.apache.log4j.Logger;

import bk.devoxx17.front.ApplicationScope;
import bk.devoxx17.front.Dispatcher;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class Main extends Application {
    private static final Logger log = Logger.getLogger(Application.class);
	
	private final static KeyCodeCombination ENTER_FULLSCREEN_CODE = new KeyCodeCombination(KeyCode.A, 
			KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination EXIT_FULLSCREEN_CODE = new KeyCodeCombination(KeyCode.B,
			KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination SHOWHIDE_MENU = new KeyCodeCombination(KeyCode.M,
			KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination PRINT_METHODTOFIND = new KeyCodeCombination(KeyCode.P,
			KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination CHANGE_METHODTOFIND = new KeyCodeCombination(KeyCode.C,
			KeyCombination.CONTROL_DOWN);
	private final static KeyCodeCombination RESET_GAME = new KeyCodeCombination(KeyCode.H,
			KeyCombination.CONTROL_DOWN);

	/**
	 * Keyloggers
     */
	private Queue<String> konamiCode = new CircularFifoQueue<String>(12);
	private Queue<String> dernieresTouches = new CircularFifoQueue<String>(12);

	private int intClose = 0;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {
		primaryStage.setTitle("Big Kahuna Log Hack Game");
		primaryStage.setFullScreenExitKeyCombination(EXIT_FULLSCREEN_CODE);
		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> logKey(event));

		initKonamiCode();

		/**
		 * Create a Menu.
		 */
		final MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		Menu mainMenu = new Menu("File");
		MenuItem fullscreenCmd = new MenuItem("Fullscreen");
		MenuItem menuCmd = new MenuItem("Show/Hide menus");
		MenuItem menuPrintMethodToFind = new MenuItem("Print MethodToFind");
		MenuItem menuChangeMethodToFind = new MenuItem("Change MethodToFind");
		mainMenu.getItems().addAll(fullscreenCmd, menuCmd, menuPrintMethodToFind, menuChangeMethodToFind);
		menuBar.getMenus().add(mainMenu);

		/**
		 * Setup MenuItem Listeners.
		 */
		fullscreenCmd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				primaryStage.setFullScreen(true);
			}
		});

		fullscreenCmd.setAccelerator(ENTER_FULLSCREEN_CODE);
		menuCmd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				menuBar.setVisible(!menuBar.isVisible());
			}
		});
		menuCmd.setAccelerator(SHOWHIDE_MENU);
		menuPrintMethodToFind.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				log.info(ApplicationScope.getInstance().getMethodToFind());			}
		});
		menuPrintMethodToFind.setAccelerator(PRINT_METHODTOFIND);
		menuChangeMethodToFind.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ApplicationScope.getInstance().chooseNewMethodToFind();			}
		});
		menuChangeMethodToFind.setAccelerator(CHANGE_METHODTOFIND);

		/**
		 *
         */
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (intClose++ < 1) {

					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Nice try");
					alert.setHeaderText("Alt + F4 detected");
					alert.setContentText("You know better than that");

					alert.showAndWait();

					event.consume();
					primaryStage.show();
				}
			}
		});

		/**
		 * Create, fill a Grid and package it into a Group
		 */
		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		final TextField loginTxt = new TextField();
		grid.add(new Label("Login: "), 0, 0);
		grid.add(loginTxt, 1, 0);
		final TextField passwordTxt = new TextField();
		grid.add(new Label("Password: "), 0, 1);
		grid.add(passwordTxt, 1, 1);
		Button connectBtn = new Button();
		connectBtn.setText("Connect");
		grid.add(connectBtn, 1, 2);
		connectBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)  {
				log.info("Typed login/password: "+loginTxt.getText() + "/" + passwordTxt.getText());
				String result = Dispatcher.check(loginTxt.getText(), passwordTxt.getText())?"OK":"KO";
				log.info("Result:" + result);
			}
		});
		Group group = new Group();
		group.getChildren().add(grid);
		
		BorderPane root = new BorderPane();
		root.setTop(menuBar);
		root.setCenter(group);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}

	private void logKey(KeyEvent event) {
		if(event.getCode() == KeyCode.H && event.isControlDown()){
			log.info("RESTART GAME");
		}
		else{
			dernieresTouches.add(event.getCode().toString());

			if(!dernieresTouches.toString().equals(konamiCode.toString()))return;

			log.info("KONAMI CODE");
		}
	}

	private void initKonamiCode() {
		konamiCode.add("UP");
		konamiCode.add("UP");
		konamiCode.add("DOWN");
		konamiCode.add("DOWN");
		konamiCode.add("LEFT");
		konamiCode.add("RIGHT");
		konamiCode.add("LEFT");
		konamiCode.add("RIGHT");
		konamiCode.add("B");
		konamiCode.add("A");
		konamiCode.add("ENTER");
		konamiCode.add("ENTER");

		for(int i = 0 ; i < 12 ; i++) {
			dernieresTouches.add("");
		}
	}
}
