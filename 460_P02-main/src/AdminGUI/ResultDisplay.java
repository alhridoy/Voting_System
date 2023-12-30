package AdminGUI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;


public class ResultDisplay extends Label {

    public ResultDisplay(File jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            JsonNode racesArray = jsonNode.get("races");

            // Create a StringBuilder to build the label text
            StringBuilder labelText = new StringBuilder();

            // Display each raceTitle and candidates in the label text
            for (JsonNode race : racesArray) {
                String raceTitle = race.get("raceTitle").asText();
                labelText.append(raceTitle).append(":\n");

                race.fieldNames().forEachRemaining(key -> {
                    if (!key.equals("raceTitle")) {
                        labelText.append(key).append(": ").append(race.get(key).asInt()).append("\n");
                    }
                });

                labelText.append("\n"); // Add a newline for separation between races
            }

            // Set the label text
            setText(labelText.toString());

        } catch (IOException e) {
            e.printStackTrace();
            setText("Error parsing JSON");
        }
    }
}