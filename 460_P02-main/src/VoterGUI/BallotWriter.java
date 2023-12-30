package VoterGUI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class BallotWriter {

    private static String voterID;
    private int raceID1 = -1;
    private int raceID2 = -1;
    private String raceTitle1;
    private String raceTitle2;
    private String selectedCandidate1;
    private String selectedCandidate2;


    public BallotWriter(String voterID, String[][] raceInfo) {
        BallotWriter.voterID = voterID;
        this.raceID1 = Integer.parseInt(raceInfo[0][0]);
        this.raceID2 = Integer.parseInt(raceInfo[1][0]);
        this.raceTitle1 = raceInfo[0][1];
        this.raceTitle2 = raceInfo[1][1];
        this.selectedCandidate1 = raceInfo[0][2];
        this.selectedCandidate2 = raceInfo[1][2];
    }

    public void writeBallot() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        SimpleDateFormat fileDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = dateFormat.format(currentDate);
        String fileDate = fileDateFormat.format(currentDate);
        Ballot vote = new Ballot(voterID, formattedDate);

        Race race1 = new Race(raceID1, raceTitle1, selectedCandidate1);
        Race race2 = new Race(raceID2, raceTitle2, selectedCandidate2);

        vote.setRaces(Arrays.asList(race1, race2));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // Write the JSON to the file
            objectMapper.writeValue(new File("current_ballot_box", voterID + "_" + fileDate + ".json"), vote);

            System.out.println("JSON file created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
