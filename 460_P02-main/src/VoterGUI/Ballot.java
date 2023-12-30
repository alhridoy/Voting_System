package VoterGUI;

import java.util.List;

public class Ballot {
    private String voterID;
    private String date;
    private List<Race> races;

    public Ballot(String voterID, String date) {
        this.voterID = voterID;
        this.date = date;
    }

    public void setRaces(List<Race> races) {
        this.races = races;
    }

    public void setVoterID(String voterID) {
        this.voterID = voterID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVoterID() {
        return voterID;
    }

    public String getDate() {
        return date;
    }

    public List<Race> getRaces() {
        return races;
    }
}
