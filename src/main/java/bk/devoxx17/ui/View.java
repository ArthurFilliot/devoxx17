package bk.devoxx17.ui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import javafx.event.EventHandler;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.log4j.Logger;

import bk.devoxx17.global.ApplicationScope;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class View {
	
	private static Logger log = Logger.getLogger(View.class);
	
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
	private static TextField txtUserName = new TextField();
	private CheckBox checkbox = new javafx.scene.control.CheckBox();
	private PasswordField  passwordTxt = new PasswordField ();
	private TextField dispPwd = new TextField();
	private Button connectBtn = new Button();
	private Menu mainMenu = new Menu("File");
	private MenuItem fullscreenCmd = new MenuItem("Fullscreen");
	private MenuItem menuCmd = new MenuItem("Show/Hide menus");
	private MenuItem menuStop = new MenuItem("Stop Game");
	private MenuItem menuReset = new MenuItem("Reset Game");
	private Stage primaryStage;
	private static boolean gameRunning = false;
	private static Timer timer;
	private static TimerTask timerTask;

	/**
	 * Keyloggers
	 */
	private static Queue<String> konamiCode = new CircularFifoQueue<String>(12);
	private static Queue<String> dernieresTouches = new CircularFifoQueue<String>(12);
	    
	public View() {
	    //resetTimer();
    }

	private static void resetTimer() {
		System.out.println("timer reset");
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				tickTimer();
			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	}

	public void draw(final Stage primaryStage) {
		this.primaryStage = primaryStage;
		initKonamiCode();

		primaryStage.setTitle("Big Kahuna Log Hack Game");

		/**
		 * Setup Listeners.
		 */
		primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> logKey(event));
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

		passwordTxt.managedProperty().bind(checkbox.selectedProperty().not());
		passwordTxt.visibleProperty().bind(checkbox.selectedProperty().not());
		dispPwd.managedProperty().bind(checkbox.selectedProperty());
		dispPwd.visibleProperty().bind(checkbox.selectedProperty());
		dispPwd.textProperty().bindBidirectional(passwordTxt.textProperty());

		/**
		 * Create, fill a Grid and package it into a Group
		 */
		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.add(new Label("Login: "), 0, 0);
		grid.add(new Label("Your Name:"), 0, 2);
		grid.add(new Label("Password: "), 0, 1);

		grid.add(loginTxt, 1, 0);
		grid.add(passwordTxt, 1, 1);
		grid.add(dispPwd, 1, 1);
		grid.add(checkbox, 2, 1);
		grid.add(txtUserName, 1, 2);
		
		connectBtn.setText("Connect");
		grid.add(connectBtn, 1, 4);

        timerLabel = new Label("05:00");
        timerLabel.setVisible(true);
        grid.add(new Label("Time : "), 0, 6);
        grid.add(timerLabel, 1, 6, 2, 1);
        
        scoreLabel = new Label("0");
        scoreLabel.setVisible(true);
        grid.add(new Label("Score : "), 0, 7);
        grid.add(scoreLabel, 1, 7, 2, 1);

		resultLabel = new Label("ErrorText");
		resultLabel.setTextFill(Color.GREEN);
		resultLabel.setVisible(false);
		grid.add(resultLabel, 0, 3, 2, 1);
		Group group = new Group();
		group.getChildren().add(grid);
		
		BackgroundFill myBF = new BackgroundFill(Color.LIGHTGREY, new CornerRadii(1),
		         new Insets(0.0,0.0,0.0,0.0));
		grid.setBackground(new Background(myBF));
		loginTxt.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER))doTry();
			}
		});
		passwordTxt.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER))doTry();
			}
		});

		dispPwd.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER))doTry();
			}
		});

		loginTxt.setMinWidth(200);
		txtUserName.minWidthProperty().bind(loginTxt.minWidthProperty());
		passwordTxt.minWidthProperty().bind(loginTxt.minWidthProperty());
		dispPwd.minWidthProperty().bind(loginTxt.minWidthProperty());
		grid.setBorder(new Border(new BorderStroke(Color.BLACK, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		menuBar.setVisible(false);



		BorderPane root = new BorderPane();
		root.setTop(menuBar);
		root.setCenter(group);
		
		BackgroundImage myBI= new BackgroundImage(new Image("background.png",1920,1080,false,true),
		        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
		          BackgroundSize.DEFAULT);
		root.setBackground(new Background(myBI));
		
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.setFullScreen(true);
	}
	
	private void toggleFullScreenMode() {
		primaryStage.setFullScreen(true);
	}
	
	private void toggleShowMenuBar() {
		menuBar.setVisible(!menuBar.isVisible());
	}
	
	private static void resetGame() {
		if(!gameRunning) {
			resetTimer();

			gameRunning = true;
			Controller.downloadTimer.resetTimer(5, 0);
			Controller.initStopWatch();
			Controller.reset();
			resultLabel.setText("");
			scoreLabel.setText("0");
		}
	}

	private static void stopGame() {
		if(gameRunning) {
			gameRunning = false;
			ApplicationScope.getInstance().setScore(0);
			Controller.downloadTimer.stop();
			timer.cancel();
			timer.purge();
		}
	}
	
	private static void tickTimer() {
		Platform.runLater(new Runnable() {
			public void run() {
				timerLabel.setText(Controller.downloadTimer.getTime());

                if(Controller.downloadTimer.isGameOver() && gameRunning){
					System.out.println("game over");
					timer.cancel();
					timer.purge();

					timerTask.cancel();

                    gameRunning = false;
                    Controller.downloadTimer.stop();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Game Over");
                    alert.setHeaderText("This round is finished, well done !!");
                    alert.setContentText(txtUserName.getText() + ", your score is : " + ApplicationScope.getInstance().getScore().toString());

					try {
						PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("scores.txt", true)));
						out.println(txtUserName.getText() + ";" + ApplicationScope.getInstance().getScore().toString());
						out.close();
					} catch (IOException e) {
						//exception handling left as an exercise for the reader
						System.out.println("Err in file");
					}

                    alert.showAndWait();
                }
			}
		});
	}
	
	static void logKey(KeyEvent event) {
		dernieresTouches.add(event.getCode().toString());
		if (dernieresTouches.toString().equals(konamiCode.toString())) {
			System.out.println("KONAMI");
			String pwd = Controller.getKonamiCode();
			resultLabel.setTextFill(Color.GREEN);
			resultLabel.setVisible(true);
			resultLabel.setText("Nice try ! Here is the Konami password : " + pwd);
		}
	}
	
	static void initKonamiCode() {
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
	
	private void doTry() {
		log.info("Typed login/password: " + loginTxt.getText() + "/" + passwordTxt.getText());
		ApplicationScope.getInstance().setErrorMessage(null);
		ApplicationScope.getInstance().setFoundMethodMessage(null);
		boolean result = Controller.check(loginTxt.getText(), passwordTxt.getText());
		if (result) {
			if (ApplicationScope.getInstance().getFoundMethodMessage()!=null) {
				resultLabel.setTextFill(Color.GREEN);
				resultLabel.setVisible(true);
				resultLabel.setText(ApplicationScope.getInstance().getFoundMethodMessage());
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
		scoreLabel.setText(ApplicationScope.getInstance().getScore().toString());
		log.info("Result:" + (result ? "OK" : "KO"));
	}
}
