package AdminGUI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AuthenticationModule {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";
    private static int attemptCounter = 0;
    private static final int MAX_ATTEMPTS = 5;
    private static long timeoutUntil = 0L;

    public static boolean checkCredentials(String username, String password) {
        // Check if currently in timeout
        if (System.currentTimeMillis() < timeoutUntil) {
            showTimeoutAlert();
            return false;
        }

        // Check credentials
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            attemptCounter = 0; // reset the counter on successful login
            return true;
        } else {
            attemptCounter++;
            if (attemptCounter >= MAX_ATTEMPTS) {
                // Trigger timeout
                timeoutUntil = System.currentTimeMillis() + 10000; // 30 seconds timeout
                showTimeoutAlert();
                attemptCounter = 0; // reset the counter after triggering timeout
            }
            return false;
        }
    }

    private static void showTimeoutAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Too many incorrect attempts. Please wait 30 seconds before trying again.", ButtonType.OK);
        alert.showAndWait();
    }
}