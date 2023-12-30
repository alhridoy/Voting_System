package AdminGUI;

import java.util.List;
import java.util.LinkedList;

public class SystemLogManager {
    private static List<String> logs = new LinkedList<>();

    public static void addLog(String log) {
        // Add new log entry to the list
        logs.add(log);
    }

    public static List<String> getLogs() {
        // Return a copy of the logs
        return new LinkedList<>(logs);
    }
}