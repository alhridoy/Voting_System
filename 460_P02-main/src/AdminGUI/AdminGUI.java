package AdminGUI;
import AdminGUI.AuthenticationModule;
import AdminGUI.encryption;
import AdminGUI.SystemLogManager;
import AdminGUI.BatteryMonitoringService;
import java.time.LocalDateTime; // Import for timestamp
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;
import java.util.Optional;

import VoterGUI.VoterGUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import static AdminGUI.BallotProcessingModule.openPoll;


public class AdminGUI extends Application {
    private Scene scene, loginScene;
    private Stage primaryStage;
    private int WIDTH = 700, HEIGHT = 450;
    private BorderPane borderPane = new BorderPane();
    private Label mainMenuLabel = new Label("Main Menu");
    private Button openPollButton = new Button("Open Poll");
    private Button closePollButton = new Button("Close Poll");
    private Button ballotButton = new Button("Ballot Manager");
    private Button exitButton = new Button("Exit");

    private Label batteryLabel;
    private BatteryMonitoringService batteryService = new BatteryMonitoringService();

    private Button viewLogsButton = new Button("View System Logs");

    private Label pollStatusLabel;
    public static Label voterPollStatus;

    public static void main(String[] args) {
        launch(args);
    }
    public static Label voteCountLabel;
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createLoginScene();

        primaryStage.setTitle("Election Admin GUI");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
    private void createLoginScene() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        // Username and Password fields
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> checkCredentials(usernameField.getText(), passwordField.getText()));

        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);

        loginScene = new Scene(gridPane, WIDTH, HEIGHT);
        loginScene.getStylesheets().add("resources/styles.css");
    }
    private void checkCredentials(String username, String password) {
        if (AuthenticationModule.checkCredentials(username, password)) {
            createMainScene();
            primaryStage.setScene(scene);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid credentials!");
            alert.show();
        }
    }
    public static void updateVoteCount() {
        int voteCount = BallotProcessingModule.getVoteCount(); // Update the count
        if (voteCountLabel == null) {
            voteCountLabel = new Label();
        }
        voteCountLabel.setText("Number of votes: " + voteCount);
    }
    private void updatePollStatus() {
        if (BallotProcessingModule.isPollOpen()) {
            pollStatusLabel.setText("Poll is open");
            pollStatusLabel.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 5;");
        } else {
            pollStatusLabel.setText("Poll is closed");
            pollStatusLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 5;");
        }
    }
    private void displaySystemLogs() {
        List<String> logs = SystemLogManager.getLogs();
        StringBuilder logsDisplay = new StringBuilder();
        for (String log : logs) {
            logsDisplay.append(log).append("\n");
        }

        TextArea logArea = new TextArea(logsDisplay.toString());
        logArea.setEditable(false);
        Scene logScene = new Scene(new StackPane(logArea), 600, 400);
        Stage logStage = new Stage();
        logStage.setTitle("System Logs");
        logStage.setScene(logScene);
        logStage.show();
    }
    private String getCurrentTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    private void createMainScene() {
        VBox menuVBox = new VBox();
        VBox buttonVBox = new VBox();
        menuVBox.getChildren().add(buttonVBox);
        mainMenuLabel.getStyleClass().add("admin-main-menu-text");
        buttonVBox.getChildren().addAll(mainMenuLabel, openPollButton, ballotButton, closePollButton, viewLogsButton, exitButton);        // Rest of your code for setting up buttons and layout

        openPollButton.setMinSize(140,40);
        ballotButton.setMinSize(140,40);
        closePollButton.setMinSize(140,40);
        viewLogsButton.setMinSize(140,40);
        exitButton.setMinSize(140,40);

        // Exit Button should close program gracefully
        exitButton.setOnAction(e -> exitAction());

        // Define the action for the openPollButton
        openPollButton.setOnAction(e -> {
            askToOpenPoll();
            // Log the event with a timestamp
            SystemLogManager.addLog("Poll opened at " + getCurrentTimeStamp());
//            VoterGUI.voterGUI.updatePollStatus();
        });

        //display Poll status
        pollStatusLabel = new Label();
        updatePollStatus(); // Initialize the poll status label

        HBox pollStatusHBox = new HBox(pollStatusLabel);
        pollStatusHBox.setAlignment(Pos.CENTER);
        pollStatusHBox.setPadding(new Insets(15, 12, 15, 12));
        //pollStatusHBox.setStyle("-fx-background-color: green; -fx-padding: 10;"); // Styling for the green box
        borderPane.setRight(pollStatusHBox); // You might choose a different position based on your UI layout

        // Set scene to Ballot Config screen

        viewLogsButton.setOnAction(e -> displaySystemLogs());

        // Define the action for the closePollButton
        closePollButton.setOnAction(e -> {
            BallotProcessingModule.closePoll();
            updatePollStatus();
//            voterPollStatus.setText("ndfoef");
//            VoterGUI.voterGUI.updatePollStatus();
            // Perform the required functions here
            // ...
            SystemLogManager.addLog("Poll closed at " + getCurrentTimeStamp());
            // Create a VBox to hold multiple UI components
            VBox centerBox = new VBox(10); // 10 is the spacing between children
            centerBox.setAlignment(Pos.CENTER);
            centerBox.setPadding(new Insets(20));
            centerBox.setStyle("-fx-background-color: lightgray;"); // Example styling

            // Create UI components for the calculating phase
            Label calculatingLabel = new Label("Calculating results...");
            calculatingLabel.setFont(new Font("Arial", 16));
            Label additionalInfoLabel = new Label("Please wait while the votes are being processed.");
            ProgressBar progressBar = new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS);

            // Add the components for the calculating phase to the VBox
            centerBox.getChildren().addAll(calculatingLabel, additionalInfoLabel, progressBar);

            // Set the VBox as the center of the BorderPane
            borderPane.setCenter(centerBox);
            TabulatorModule.findTotalsWithTitle();

            // Timeline to wait for 1 second before changing the content
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), ev -> {
                // Clear the existing content
                centerBox.getChildren().clear();

                // Create and add new labels for the results
//                Label calculationCompleteLabel = new Label("Calculation complete:");
//                Label result1Label = new Label("Result1");
//                Label result2Label = new Label("Result2");

                //decrypt results really quickly
                try {
                    encryption.decryptJsonFile("results/results.json", "results/results.json");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                centerBox.getChildren().addAll(new ResultDisplay(new File("Results/results.json")));
//                // Add more result labels as needed
//                centerBox.getChildren().addAll(calculationCompleteLabel, result1Label, result2Label);


                //encrypt it back for safe storage
                try {
                    encryption.encryptJsonFile("results/results.json", "results/results.json");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }));
            timeline.setCycleCount(1);
            timeline.play();

            // If you have long-running tasks, handle them in a new Thread and update UI with Platform.runLater
        });

        ballotButton.setOnAction(e -> changeSceneToBallotConfig());

        menuVBox.setAlignment(Pos.TOP_CENTER);
        buttonVBox.setAlignment(Pos.CENTER);
        menuVBox.setSpacing(50);
        buttonVBox.setSpacing(10);
        menuVBox.setPadding(new Insets(20, 10, 20, 10));
//        menuVBox.setStyle("-fx-border-color: black; -fx-border-width: 0 2 0 0;");
        menuVBox.getStyleClass().add("admin-menu");
        borderPane.setLeft(menuVBox);


        batteryLabel = new Label("Battery: " + batteryService.getPercentage() + "%");
        Label locationLabel = new Label("  Albuquerque, NM 100023");
        HBox topHBox = new HBox(locationLabel, batteryLabel);

        //locationLabel.setPadding(new Insets(7, 5, 7, 5));
        topHBox.setSpacing(20); // Increase this value as needed
        topHBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 2 0;");
        topHBox.getStyleClass().add("admin-top");

        borderPane.setTop(topHBox);
        // Update battery percentage every minute
        Timeline batteryTimeline = new Timeline(new KeyFrame(Duration.minutes(1), ev -> {
            batteryLabel.setText("Battery: " + batteryService.getPercentage() + "%");
        }));
        batteryTimeline.setCycleCount(Timeline.INDEFINITE);
        batteryTimeline.play();



        // Vote Count
        // Initialize voteCountLabel with the current vote count
        BallotProcessingModule.updateFileCounts();
        BallotProcessingModule.watchDirectory();
        updateVoteCount();

        HBox bottomHBox = new HBox(voteCountLabel);
        bottomHBox.setAlignment(Pos.CENTER);
        bottomHBox.setPadding(new Insets(15, 12, 15, 12));
        bottomHBox.getStyleClass().add("admin-bottom");
        borderPane.setBottom(bottomHBox);
        bottomHBox.setStyle("-fx-border-color: black; -fx-border-width: 2 0 0 0;");


        primaryStage.setScene(scene);
        primaryStage.show();

        scene = new Scene(borderPane, WIDTH, HEIGHT);
        scene.getStylesheets().add("resources/styles.css");
    }

    // Method to ask the user for confirmation before opening the poll
    private void askToOpenPoll() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Open Poll Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to open the poll using the current ballot configuration?");
        confirmationAlert.setContentText("Opening the poll will reset the ballot box for the new poll.");

        // Wait for the user's response
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            openPoll();
            updatePollStatus();
            // Show a message that the poll has been successfully opened
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Poll Opened");
            successAlert.setHeaderText(null);
            successAlert.setContentText("The poll has been successfully opened and the ballot box has been reset.");
            successAlert.showAndWait();
            updateVoteCount();
            VoterGUI.updateVoteCount();
        }

    }

    private void changeSceneToBallotConfig() {
        Scene ballotManagerScene = new BallotManagerScene(primaryStage, scene, WIDTH, HEIGHT); // Pass the adminScene here
        primaryStage.setScene(ballotManagerScene);
    }

    private void exitAction() {
        // TODO: Add restrictions or warning if closing AdminGUI when poll is still in action or unfinished
        BallotProcessingModule.exit = true;
        System.exit(0);
    }


}
