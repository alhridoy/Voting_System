package VoterGUI;
import AdminGUI.AdminGUI;
import AdminGUI.BallotProcessingModule;
import AdminGUI.AudioFeedback;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.ComboBox;
import java.util.Arrays;
import java.util.stream.Stream;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class VoterGUI extends Application {
    private int raceId = -1;
    private int raceId2 = -1;
    private Scene scene;
    private BorderPane borderPane = new BorderPane();
    private Button submitButton = new Button("Submit Vote");
    private Button exitButton = new Button("Exit");
    private ToggleGroup candidatesGroup = new ToggleGroup(); // ToggleGroup for RadioButtons
    private ToggleGroup candidatesGroup2 = new ToggleGroup(); // ToggleGroup for RadioButtons
    private Label errorMessageLabel = new Label(); // Error message label
    private static Label voteCountLabel;
    private Label pollStatusLabel;
    public static VoterGUI voterGUI;



    private String raceTitle;
    private String raceTitl2;

    private String[] democraticCandidates;
    private String[] republicanCandidates;
    private String[] allCandidates;
    public static void main(String[] args) {
        launch(args);
    }
    private String[] extractCandidates(String json, String party) {
        Pattern pattern = Pattern.compile(String.format("\"party\": \"%s\",\\s*\"candidates\": (\\[\"[^\"]+\"(?:,\\s*\"[^\"]+\")*\\])", party));
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            String candidatesArray = matcher.group(1);
            candidatesArray = candidatesArray.substring(1, candidatesArray.length() - 1); // Remove the surrounding brackets
            String partySuffix = party.equals("Democratic") ? " (D)" : " (R)";
            return Arrays.stream(candidatesArray.replace("\"", "").split(",\\s*"))
                    .map(name -> name + partySuffix)
                    .toArray(String[]::new); // Append party abbreviation and collect to array
        }
        return new String[0]; // Return an empty array if no match found
    }
    public void updatePollStatus() {
//        if(pollStatusLabel == null) pollStatusLabel = new Label();
//        System.out.println(pollStatusLabel == null);
        if (BallotProcessingModule.isPollOpen()) {
            pollStatusLabel.setText("Poll is open");
            pollStatusLabel.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 5;");
            pollStatusLabel.getStyleClass().add("open-message");
        } else {
            pollStatusLabel.setText("Poll is closed");
            pollStatusLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 5;");
            pollStatusLabel.getStyleClass().add("open-message");
        }

    }
    @Override
    public void start(Stage primaryStage) throws IOException {


        primaryStage.setTitle("User Voting GUI");

        Label voteTitle = new Label("Voting Module");
        voteTitle.setFont(new Font("Impact", 24));

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        borderPane.setBottom(buttonBox);
        buttonBox.getChildren().addAll(submitButton, exitButton);
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(70));
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxHeight(50);
        buttonBox.setMinHeight(50);

        //Poll status label
        pollStatusLabel = new Label();
        AdminGUI.voterPollStatus = pollStatusLabel;
        pollStatusLabel.setFont(new Font("Arial", 16));
        updatePollStatus(); // Method to update the poll status label
        System.out.println(this.pollStatusLabel.getText());
        voterGUI = this;
        System.out.println(voterGUI == null);


        // Vote count label
        voteCountLabel = new Label();
        BallotProcessingModule.updateFileCounts();
        BallotProcessingModule.watchDirectory();
        updateVoteCount();


        // AudioFeedback button
        Button audioFeedbackButton = new Button("AudioFeedback");
        audioFeedbackButton.setOnAction(e -> {
            // Place your function here
            AudioFeedback.speak(raceTitle+",Please select from the following candidates:");
            for (String candidate : democraticCandidates) {
                RadioButton radioButton = new RadioButton(candidate);
                AudioFeedback.speak(candidate);

            }
            for (String candidate : republicanCandidates) {
                AudioFeedback.speak(candidate);

            }
        });


// Style the container for the poll status label
        VBox leftContainer = new VBox(10); // 10 is the spacing between elements
        leftContainer.setAlignment(Pos.CENTER);
        leftContainer.setPadding(new Insets(10));
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(pollStatusLabel, new Rectangle(20,0,Color.TRANSPARENT), voteCountLabel,new Rectangle(20,0,Color.TRANSPARENT), audioFeedbackButton);
        leftContainer.getChildren().addAll(voteTitle,hBox);
//        leftContainer.getChildren().addAll(pollStatusLabel, voteCountLabel, audioFeedbackButton);

// Set the container to the left side of the BorderPane
        borderPane.setTop(leftContainer);





          /*      // Style the container for the poll status label
        VBox pollStatusContainer = new VBox(pollStatusLabel);
        pollStatusContainer.setAlignment(Pos.CENTER);
        pollStatusContainer.setPadding(new Insets(10));
        // Add the status label to the right side of the BorderPane
        borderPane.setLeft(pollStatusContainer);*/


/*
        updateVoteCount();

        HBox topHBox2 = new HBox(voteCountLabel);
        topHBox2.setAlignment(Pos.CENTER);
        topHBox2.setPadding(new Insets(15, 12, 15, 12));
        borderPane.setLeft(topHBox2);*/




        Path jsonFilePath = Paths.get("src/resources/ballot_template.json");
        String jsonContent = new String(Files.readAllBytes(jsonFilePath), StandardCharsets.UTF_8);


        // Extract the first object from the JSON array
        Pattern arrayPattern = Pattern.compile("\\{(?:[^{}]|\\{[^{}]*\\})*\\}");
        Matcher arrayMatcher = arrayPattern.matcher(jsonContent);
        String firstObject = "";
        if (arrayMatcher.find()) {
            firstObject = arrayMatcher.group();
        }

        // Regex pattern to skip the first object and capture the second object
        Pattern secondObjectPattern = Pattern.compile("(?:\\{(?:[^{}]|\\{[^{}]*\\})*\\})\\s*,\\s*(\\{(?:[^{}]|\\{[^{}]*\\})*\\})");
        Matcher arrayMatcher2 = secondObjectPattern.matcher(jsonContent);
        String secondObject = "";
        if (arrayMatcher2.find()) {
            secondObject = arrayMatcher2.group(1);
        }


        // Use a regex pattern to extract the raceTitle value
        Pattern pattern = Pattern.compile("\"raceTitle\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(firstObject);
        raceTitle = "";
        if (matcher.find()) {
            raceTitle = matcher.group(1);
        }





        // Create the race title label
        Label raceTitleLabel = new Label(raceTitle);
        raceTitleLabel.setFont(new Font("Impact", 24));

        Pattern pattern2 = Pattern.compile("\"raceId\":(\\d+)");
        Matcher matcher2 = pattern2.matcher(firstObject);

        this.raceId = -1; // Default or error value
        if (matcher2.find()) {
            this.raceId = Integer.parseInt(matcher2.group(1));
        }

        Label raceIdLabel = new Label();
        // After extracting the raceId
        raceIdLabel.setText("Race ID: " + String.valueOf(this.raceId));
        raceIdLabel.setFont(new Font("Arial", 16));



        // Extracting the Democratic candidates
        // Extract candidates for each party
        democraticCandidates = extractCandidates(firstObject, "Democratic");
        republicanCandidates = extractCandidates(firstObject, "Republican");
        // Merge the two arrays
        allCandidates = Stream.concat(
                Stream.of(democraticCandidates).map(name -> name ),
                Stream.of(republicanCandidates).map(name -> name )
        ).toArray(String[]::new);



        VBox radioButtonBox = new VBox(10); // 10 is the spacing between elements
        radioButtonBox.setAlignment(Pos.CENTER_LEFT);
        // Create RadioButtons for each candidate and add to the VBox
        for (String candidate : democraticCandidates) {
            RadioButton radioButton = new RadioButton(candidate);
            radioButton.setToggleGroup(candidatesGroup);
            radioButtonBox.getChildren().add(radioButton);
            radioButton.getStyleClass().add("radio");
            radioButton.minWidthProperty().bind(radioButtonBox.widthProperty());
        }
        for (String candidate : republicanCandidates) {
            RadioButton radioButton = new RadioButton(candidate);
            radioButton.setToggleGroup(candidatesGroup);
            radioButtonBox.getChildren().add(radioButton);
            radioButton.getStyleClass().add("radio");
            radioButton.minWidthProperty().bind(radioButtonBox.widthProperty());
        }

        TextField voterIdField = new TextField();
        voterIdField.setPromptText("Enter Voter ID");
        // Create a Label for the Voter ID field
        Label voterIdLabel = new Label("Voter ID:");
        voterIdLabel.setLabelFor(voterIdField);
        // Arrange the Voter ID Label and TextField horizontally
        HBox voterIdHBox = new HBox(10); // spacing of 10 pixels
        voterIdHBox.getChildren().addAll(voterIdLabel, voterIdField);

        voterIdHBox.setAlignment(Pos.CENTER);


        // Label for displaying date and time
        Label dateTimeLabel = new Label();
        dateTimeLabel.setFont(new Font("Arial", 16));

        // Format for date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Timeline to update the label every second
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            dateTimeLabel.setText("Current date/time: " + formatter.format(now)); // Prepend the label text here
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();








        //Extract info for the second race
        // Use a regex pattern to extract the raceTitle value
        Pattern patternSecondRace = Pattern.compile("\"raceTitle\":\"([^\"]+)\"");
        Matcher matcherSecondRace  = pattern.matcher(secondObject);
        String raceTitle2 = "";
        if (matcherSecondRace.find()) {
            raceTitle2 = matcherSecondRace.group(1);
        }

        // Create the race title label
        Label raceTitleLabel2 = new Label(raceTitle2);
        raceTitleLabel2.setFont(new Font("Impact", 24));

        //DONE

        Pattern patternSecondRace2 = Pattern.compile("\"raceId\":(\\d+)");
        Matcher matcherSecondRace2 = patternSecondRace2.matcher(secondObject);

        this.raceId2 = -1; // Default or error value
        if (matcherSecondRace2.find()) {
            this.raceId2 = Integer.parseInt(matcherSecondRace2.group(1));
        }

        Label raceIdLabel2 = new Label();
        // After extracting the raceId
        raceIdLabel2.setText("Race ID: " + String.valueOf(this.raceId2));
        raceIdLabel2.setFont(new Font("Arial", 16));






        // Extracting the Democratic candidates
        // Extract candidates for each party
        String[] democraticCandidates2 = extractCandidates(secondObject, "Democratic");
        String[] republicanCandidates2 = extractCandidates(secondObject, "Republican");
        // Merge the two arrays
        String[] allCandidates2 = Stream.concat(
                Stream.of(democraticCandidates2).map(name -> name ),
                Stream.of(democraticCandidates2).map(name -> name )
        ).toArray(String[]::new);

        VBox radioButtonBox2 = new VBox(10); // 10 is the spacing between elements
        radioButtonBox2.setAlignment(Pos.CENTER_LEFT);
// Create RadioButtons for each candidate and add to the VBox
        for (String candidate : democraticCandidates2) {
            RadioButton radioButton2 = new RadioButton(candidate);
            radioButton2.setToggleGroup(candidatesGroup2);
            radioButtonBox2.getChildren().add(radioButton2);
            radioButton2.getStyleClass().add("radio");
            radioButton2.minWidthProperty().bind(radioButtonBox2.widthProperty());
        }
        for (String candidate : republicanCandidates2) {
            RadioButton radioButton2 = new RadioButton(candidate);
            radioButton2.setToggleGroup(candidatesGroup2);
            radioButtonBox2.getChildren().add(radioButton2);
            radioButton2.getStyleClass().add("radio");
            radioButton2.minWidthProperty().bind(radioButtonBox2.widthProperty());
        }






        // Setup for the error message label
        errorMessageLabel.setTextFill(Color.RED); // Set text color to red for visibility
        errorMessageLabel.setFont(new Font("Arial", 14));
        errorMessageLabel.setVisible(false); // Initially hide the label



        //SUBMIT BUTTON ACTION
        submitButton.setOnAction(e -> {
            try {


                if (voterIdField.getText().trim().isEmpty()) {
                    errorMessageLabel.setText("Voter ID cannot be null");
                    errorMessageLabel.setVisible(true); // Show the error message
                    return; // Skip the rest of the action event
                }

                if (!BallotProcessingModule.isPollOpen()) {
                    errorMessageLabel.setText("Poll is not open");
                    errorMessageLabel.setVisible(true); // Show the error message
                    return; // Skip the rest of the action event
                }

                // Gather Information
                String voterId = voterIdField.getText();

                LocalDateTime now = LocalDateTime.now();
                String currentDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(now);
                String currentDateTime2 = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now);

                RadioButton selectedRadioButton = (RadioButton) candidatesGroup.getSelectedToggle();
                String selectedCandidate = "NONE";
                if(selectedRadioButton != null && selectedRadioButton.isSelected()) {
                    selectedCandidate = selectedRadioButton.getText();
                }

                String theRaceTitle = raceTitleLabel.getText();



                //Gather info for second race
                RadioButton selectedRadioButton2 = (RadioButton) candidatesGroup2.getSelectedToggle();
                String selectedCandidate2 = "NONE";
                if(selectedRadioButton2 != null && selectedRadioButton2.isSelected()) {
                    selectedCandidate2 = selectedRadioButton2.getText();
                }

                String theRaceTitle2 = raceTitleLabel2.getText();

                File inputDirectory = new File("current_ballot_box");
                File[] files = inputDirectory.listFiles();

                if (files != null) {
                    for (File file : files) {
                        String fileName = file.getName();
                        String[] separatedName = fileName.split("_");
                        System.out.println(fileName);
                        String voterIDCompare = "";
                        if (separatedName.length > 0) {
                            System.out.println(separatedName[0]);
                            voterIDCompare = separatedName[0];
                            errorMessageLabel.setVisible(false);
                            if (voterId.equals(voterIDCompare)) {
                                errorMessageLabel.setText("Invalid VoterID, Please double check your VoterID");
                                errorMessageLabel.setVisible(true);
                                return; // If duplicate VoterID is found, display error message
                            }
                        }

                    }
                } else {
                    System.out.println("Invalid directory path");
                }
                String[][] raceInfo = {{String.valueOf(raceId), theRaceTitle, selectedCandidate},
                        {String.valueOf(raceId2), theRaceTitle2, selectedCandidate2}};
                BallotWriter ballotWriter = new BallotWriter(voterId, raceInfo);
                ballotWriter.writeBallot();





                // Check/Create Directory
                Path directoryPath = Paths.get("current_ballot_box");
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }



                // Clearing the form after submission
                voterIdField.setText(""); // Clear the voter ID TextField

                // Clear RadioButton selections
                if(candidatesGroup.getSelectedToggle() != null) {
                    candidatesGroup.getSelectedToggle().setSelected(false);
                }
                if(candidatesGroup2.getSelectedToggle() != null) {
                    candidatesGroup2.getSelectedToggle().setSelected(false);
                }
                errorMessageLabel.setVisible(false); // Hide the error message

                updateVoteCount();


            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        exitButton.setOnAction(e -> exitAction());
        exitButton.getStyleClass().add("exit-button");



//        Label voteTitle = new Label("Voting Module");
        voteTitle.setFont(new Font("Impact", 24));




        VBox vbox = new VBox(20);
        borderPane.setCenter(vbox);
        vbox.getChildren().addAll(raceTitleLabel, raceIdLabel); // Add raceTitleLabel here

        vbox.getChildren().addAll(new Label("Select a candidate:"), radioButtonBox);



        //ADD SECOND RACE
        raceTitleLabel2.setWrapText(true);
        vbox.getChildren().addAll(raceTitleLabel2, raceIdLabel2);
        vbox.getChildren().addAll(new Label("Select a candidate:"), radioButtonBox2);

        vbox.getChildren().addAll(voterIdHBox);



        vbox.getChildren().addAll(dateTimeLabel);

        vbox.getChildren().add(errorMessageLabel);


        vbox.setPadding(new Insets(70));
        vbox.setAlignment(Pos.CENTER);

        // Create a ScrollPane and add contentBox to it
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
//        scrollPane.minHeightProperty().bind(borderPane.heightProperty().subtract(880));

        // Set the ScrollPane as the center of the BorderPane
        borderPane.setCenter(scrollPane);

        scene = new Scene(borderPane, 600, 650);
        scene.getStylesheets().add("resources/styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            Platform.runLater(()->updatePollStatus());
        }, 0, 1, TimeUnit.SECONDS);




    }

    public static void updateVoteCount() {
        int voteCount = BallotProcessingModule.getVoteCount(); // Update the count
        if (voteCountLabel == null) {
            voteCountLabel = new Label();
        }
        voteCountLabel.setText("Number of votes: " + voteCount);
    }

    public static void exitAction() {
        BallotProcessingModule.exit = true;
        System.exit(0);
    }

}