package AdminGUI;
import VoterGUI.VoterGUI;
import javafx.application.Platform;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;


public class BallotProcessingModule {
    private static int voteCount = 0;
    private static final String DIRECTORY_PATH = "current_ballot_box";
    private static Map<String, Integer> fileCounts = new HashMap<>();
    public static Boolean exit = false;

    public static void watchDirectory() {
        try {
            Path pathDir = Paths.get(DIRECTORY_PATH);

            // Initialize the watch service
            WatchService watchService = FileSystems.getDefault().newWatchService();
            pathDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);

            // Watch for events in a separate thread
            new Thread(() -> {
                try {
                    updateFileCounts();
                    while (!exit) {
                        WatchKey key = watchService.take();

                        for (WatchEvent<?> event : key.pollEvents()) {
                            WatchEvent.Kind<?> kind = event.kind();

                            if (kind == StandardWatchEventKinds.ENTRY_CREATE ||
                                    kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                // Update file counts on create or delete events
                                updateFileCounts();

                                // Update UI label on the JavaFX Application Thread
                                Platform.runLater(() -> {
                                    AdminGUI.updateVoteCount();
                                    VoterGUI.updateVoteCount();
                                });
                            }
                        }

                        key.reset();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void updateFileCounts() {
        try {
            fileCounts.clear();

            // Count the number of .json files in the directory
            Files.list(Paths.get(DIRECTORY_PATH))
                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .forEach(path -> fileCounts.merge(".json", 1, Integer::sum));
            voteCount = fileCounts.getOrDefault(".json", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getVoteCount() {
        return voteCount;
    }

    public static void parser() {
        // Maybe a helper method to parse a file
    }

    public static void openPoll() {
        // Maybe a helper method to parse a file
        createPollSettingsFile();
        clearCurrentBallotBox();
    }


    public static void closePoll() {
        // Maybe a helper method to parse a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("CurrentPollSettings.txt"))) {
            writer.write("pollstatus = closed");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an alert to the user)
        }
    }
    private static void createPollSettingsFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("CurrentPollSettings.txt"))) {
            writer.write("pollstatus = open");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an alert to the user)
        }
    }

    public static boolean isPollOpen() {
        File file = Paths.get("CurrentPollSettings.txt").toFile();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if ("pollstatus = open".equals(line.trim())) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not find the file: " + file.getAbsolutePath());
        }
        return false;
    }

    private static void clearCurrentBallotBox() {
        try (Stream<Path> paths = Files.walk(Paths.get("current_ballot_box"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Handle the exception for each file (e.g., log the error)
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception for the directory walk (e.g., show an alert to the user)
        }
    }

}
