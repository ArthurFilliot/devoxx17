package bk.devoxx17.ui;

import org.apache.log4j.Logger;

import bk.devoxx17.front.ApplicationScope;
import bk.devoxx17.front.Dispatcher;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {
		primaryStage.setTitle("Big Kahuna Log Hack Game");
		primaryStage.setFullScreenExitKeyCombination(EXIT_FULLSCREEN_CODE);

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
}
