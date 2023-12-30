package VoterGUI;

public class Race {
    private int raceID;
    private String raceTitle;
    private String selectedCandidate;

    public Race(int raceID, String raceTitle, String selectedCandidate) {
        this.raceID = raceID;
        this.raceTitle = raceTitle;
        this.selectedCandidate = selectedCandidate;
    }

    public void setRaceID(int raceID) {
        this.raceID = raceID;
    }

    public void setRaceTitle(String raceTitle) {
        this.raceTitle = raceTitle;
    }

    public void setSelectedCandidate(String raceTitle) {
        this.raceTitle = raceTitle;
    }

    public int getRaceID() {
        return raceID;
    }

    public String getRaceTitle() {
        return raceTitle;
    }

    public String getSelectedCandidate() {
        return selectedCandidate;
    }
}
