package AdminGUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class BatteryMonitoringService {
    private int batteryPercentage = 100;
    private boolean powerSourceOn;
    private Timer timer;

    public BatteryMonitoringService() {
        initializePowerSource();
        startBatteryUpdateTask();
    }

    private void initializePowerSource() {
        try {
            File file = new File("resources/powerSourceOn.txt");
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Create directories if they don't exist
                FileWriter writer = new FileWriter(file);
                writer.write("powerSourceOn = True");
                writer.close();
                powerSourceOn = true;
            } else {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                powerSourceOn = content.trim().equalsIgnoreCase("powerSourceOn = True");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startBatteryUpdateTask() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateBatteryPercentage();
            }
        };

        timer.scheduleAtFixedRate(task, 1000, 1000); // Schedule the task to run every second
    }

    private void updateBatteryPercentage() {
        if (powerSourceOn) {
            if (batteryPercentage < 100) {
                batteryPercentage++;
            }
        } else {
            if (batteryPercentage > 0) {
                batteryPercentage--;
            }
        }
    }

    public int getPercentage() {
        return batteryPercentage;
    }

    public void setPercentage(int percentage) {
        this.batteryPercentage = Math.min(100, Math.max(0, percentage));
    }

    // Use this method to manually update the power source status
    public void setPowerSourceOn(boolean powerSourceOn) {
        this.powerSourceOn = powerSourceOn;
    }
}