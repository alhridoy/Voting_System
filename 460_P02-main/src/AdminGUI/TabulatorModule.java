package AdminGUI;
import AdminGUI.encryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

public class TabulatorModule {

    public static Map<String, Map<String, Integer>> raceVotes = new HashMap<>();

    public static void findTotalsWithTitle() {
            // Specify the directory containing the JSON files
            String inputDirectoryPath = "current_ballot_box";
            String outputFilePath = "Results";

            // Initialize a map to store votes by race and candidate
            // Map<String, Map<String, Integer>> raceVotes = new HashMap<>();

            // Process each JSON file in the directory
            File inputDirectory = new File(inputDirectoryPath);
            File[] files = inputDirectory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        processJsonFile(file);
                    }
                }

                sortResults();
                // Write the tabulated results to a new JSON file
                writeResultsToJsonFile(outputFilePath);
            } else {
                System.out.println("Invalid directory path.");
            }
        }

        private static void processJsonFile(File file) {
            try {
                // Read and parse the JSON file
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(file);

                // Extract voter ID

                // Iterate through each race
                JsonNode racesNode = rootNode.get("races");
                for (JsonNode raceNode : racesNode) {
                    // Extract race details
                    String raceTitle = raceNode.get("raceTitle").asText();
                    String selectedCandidate = raceNode.get("selectedCandidate").asText();

                    // Update votes in the map
                    raceVotes.computeIfAbsent(raceTitle, k -> new HashMap<>())
                            .merge(selectedCandidate, 1, Integer::sum);
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + file.getName());
                e.printStackTrace();
            }
        }

    private static void writeResultsToJsonFile(String outputFilePath) {
        try {
            // Create a new ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Create a structured JSON array for races
            Object[] racesArray = raceVotes.entrySet().stream()
                    .map(entry -> {
                        // Create a map to hold the race title and candidate votes
                        Map<String, Object> raceObject = new LinkedHashMap<>();
                        raceObject.put("raceTitle", entry.getKey());

                        // Sort candidates by their values in descending order
                        Map<String, Integer> sortedVotes = entry.getValue().entrySet()
                                .stream()
                                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

                        raceObject.putAll(sortedVotes);
                        return raceObject;
                    })
                    .toArray();

            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            String formattedDate = dateFormat.format(currentDate);

            // Create a structured JSON object with the "races" array and the "date" field
            Map<String, Object> resultObject = new HashMap<>();
            resultObject.put("date", formattedDate);
            resultObject.put("races", racesArray);


            // Write the structured JSON object to a new JSON file
            objectMapper.writeValue(new File(outputFilePath, "results.json"), resultObject);

            System.out.println("Tabulated results written to: " + outputFilePath);


            encryption.encryptJsonFile("results/results.json", "results/results.json");




        } catch (IOException e) {
            System.out.println("Error writing results to file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        private static void sortResults() {
            for (Map.Entry<String, Map<String, Integer>> entry : raceVotes.entrySet()) {
                String race = entry.getKey();
                Map<String, Integer> votes = entry.getValue();

                Map<String, Integer> sortedVotes = votes.entrySet()
                        .stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                raceVotes.put(race, sortedVotes);
            }
        }
}
