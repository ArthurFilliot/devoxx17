package bk.devoxx17.ui;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import bk.devoxx17.emulators.Dispatcher;
import bk.devoxx17.emulators.InjectionMethod;
import bk.devoxx17.emulators.VulnerabilityEmulator;
import bk.devoxx17.emulators.VulnerabilityEmulatorSQL;
import bk.devoxx17.global.ApplicationScope;
import bk.devoxx17.utils.DatabaseSQL;
import bk.devoxx17.utils.DownloadTimer;
import bk.devoxx17.utils.PasswordGenerator;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;


public class Controller {

	private static Logger log = Logger.getLogger(Controller.class);
	
	/**
	 * Keyloggers
	 */
	private static Queue<String> konamiCode = new CircularFifoQueue<String>(12);
	private static Queue<String> dernieresTouches = new CircularFifoQueue<String>(12);

	private static DatabaseSQL db = new DatabaseSQL();
	
    static DownloadTimer downloadTimer = new DownloadTimer(5, 0);
	static boolean doClose = false;

	static {
		try {
			initDb();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	static void logKey(KeyEvent event) {
		dernieresTouches.add(event.getCode().toString());
		if (dernieresTouches.toString().equals(konamiCode.toString())) {
			System.out.println("KONAMI");
			String pwd = Controller.getKonamiCode();
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Nice try");
			alert.setHeaderText("Here is the Konami password : " + pwd);
			alert.setContentText("You know better than that");
			alert.showAndWait();
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

	static void initStopWatch(){
        downloadTimer.start();
	}

	static void initDb() throws SQLException {
		db.openConnection();
		String createSchema = db.getScript("/sql/schema.sql");
		String insertUsers = db.getScript("/sql/users.sql");
		db.executeScript(createSchema);
		db.executeScript(insertUsers);
		try {
			ArrayListMultimap<String, String> result = db
					.executeSelection("SELECT ID FROM Users WHERE login in ('lambda','Konami')");
			for (String id : result.get("ID")) {
				db.executeScript("UPDATE Users SET password='"
						+ PasswordGenerator.nextPassword().replace('\'', '_').replace(';', '_') + "' WHERE id=" + id
						+ ";");
			}
		} catch (SQLException e) {
			System.exit(0);
		}
	}

	static void terminateDb() {
		db.closeConnection();
	}

	static void setDb(DatabaseSQL db) throws SQLException {
		Controller.db.closeConnection();
		Controller.db = db;
		initDb();
	}

	static void init() {
		log.info("Dispatcher init:");
	}

	/**
	 * Dispatch given login/password to the current injectionMethodToFind
	 **/
	static boolean check(String login, String password) {
		List<InjectionMethod> methodsToFind = ApplicationScope.getInstance().getMethodsToFind();
		Integer score = ApplicationScope.getInstance().getScore();
		Iterator<InjectionMethod> it = methodsToFind.iterator();
		List<InjectionMethod> foundMethods = Lists.newArrayList();

		while (it.hasNext()) {
			InjectionMethod method = it.next();
			VulnerabilityEmulator emulator = Dispatcher.selectEmulator(method);
			if (emulator instanceof VulnerabilityEmulatorSQL) {
				((VulnerabilityEmulatorSQL) emulator).setDb(db);
			}
			if (emulator.doCheck(login, password)) {
				score += method.getScore();
				it.remove();
				foundMethods.add(method);
				break;
			}
		}
		if (foundMethods.size() > 0) {
			String message = "Access granted ! (You have found method : ";
			String sep = "";
			for (InjectionMethod method : foundMethods) {
				message += sep + method.getLabel();
				sep = ", ";
			}
			message += ")";
			ApplicationScope.getInstance().setFoundMethodMessage(message);
			ApplicationScope.getInstance().setScore(score);
			return true;
		}
		return false;
	}

	static String getKonamiCode() {
		String pwd = "";
		try {
			ArrayListMultimap<String, String> result = db
					.executeSelection("SELECT password FROM Users WHERE login='Konami'");
			pwd = result.get("PASSWORD").get(0);
		} catch (Exception e) {
		}
		return pwd;
	}

}
