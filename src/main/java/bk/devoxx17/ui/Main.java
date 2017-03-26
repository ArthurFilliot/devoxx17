package bk.devoxx17.ui;

import java.io.File;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	public static void main(String[] args) {
		if (resetDb()) {
			Controller.init();
		} else {
			System.exit(0);
		}
		launch(args);
	}
	
	@Override
	public void start(final Stage primaryStage) {
		primaryStage.setOnCloseRequest(event -> onClose(event, primaryStage));
		View v = new View();
		v.draw(primaryStage);
		primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		Controller.terminateDb();
		resetDb();
		super.stop();
		System.exit(0);
	}
	
	private void onClose(WindowEvent event, Stage primaryStage) {
		if (Controller.doClose) {
			try {
				stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			Controller.doClose=true;
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Nice try");
			alert.setHeaderText("Alt + F4 detected");
			alert.setContentText("You know better than that");

			alert.showAndWait();

			event.consume();
			primaryStage.show();
		}
	}
	
	private static boolean resetDb() {
		File file = new File("./test.db");
//		log.debug("Reset db:" + file.getAbsolutePath());
		if (file.exists()) {
			return file.delete();
		}
		return true;
	}
}
