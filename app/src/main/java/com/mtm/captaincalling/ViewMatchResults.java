package com.mtm.captaincalling;

public class ViewMatchResults {
    private String MatchNoText, Team1NameText, Team2NameText, MatchResultText;

    public ViewMatchResults(String matchNoText, String team1NameText, String team2NameText, String matchResultText) {
        MatchNoText = matchNoText;
        Team1NameText = team1NameText;
        Team2NameText = team2NameText;
        MatchResultText = matchResultText;
    }

    public ViewMatchResults() {

    }

    public String getMatchNoText() {
        return MatchNoText;
    }

    public String getTeam1NameText() {
        return Team1NameText;
    }

    public String getTeam2NameText() {
        return Team2NameText;
    }

    public String getMatchResultText() {
        return MatchResultText;
    }

}
