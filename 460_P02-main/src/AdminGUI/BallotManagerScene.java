package AdminGUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class BallotManagerScene extends Scene {

    public BallotManagerScene(Stage primaryStage, Scene adminScene, int width, int height) {
        super(ballotManagerLayout(primaryStage, adminScene), width, height);
    }

    private static BorderPane ballotManagerLayout(Stage primaryStage, Scene adminScene) {
        BorderPane layout = new BorderPane();




        Button backButton = new Button("Back");
        backButton.setOnAction(event -> primaryStage.setScene(adminScene));
        HBox bottomHBox = new HBox(backButton);
        bottomHBox.setAlignment(Pos.CENTER);
        bottomHBox.setPadding(new Insets(15, 12, 15, 12));

        layout.setBottom(bottomHBox);

        // Add a TextArea to the center of the BorderPane
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        layout.setCenter(textArea);

        // Load the content of ballot_template.json into the TextArea
        Path jsonFilePath = Paths.get("src/resources/ballot_template.json"); // Make sure the path is correct
        try {
            String content = new String(Files.readAllBytes(jsonFilePath), StandardCharsets.UTF_8);
            textArea.setText(content);
        } catch (Exception e) {
            e.printStackTrace(); // You may want to handle this exception more gracefully
        }

        // Add a Save button to the bottom HBox
        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            try {
                Files.write(jsonFilePath, textArea.getText().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace(); // Again, handle this exception as appropriate for your application
            }
        });

        // Add the Save button next to the Back button
        bottomHBox.getChildren().add(saveButton);

        layout.setBottom(bottomHBox);

        return layout;
    }
}