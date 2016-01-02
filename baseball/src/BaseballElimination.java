/**
 * Project 3: Baseball Elimination
 * Name: Tri Minh Cao
 * Email: trimcao@gmail.com
 * Date: December 2015
 */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import java.util.ArrayList;
import java.util.HashMap;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FordFulkerson;

public class BaseballElimination
{
    private int numTeams;
    private HashMap<String, Integer> teamID;
    private String[] teams;
    private int[] wins;
    private int[] loses;
    private int[] remains;
    private int[][] games;

    private class Result {
        ArrayList<String> certificate;
        boolean eliminated;
        public Result(boolean eliminated, ArrayList<String> certificate) {
            this.eliminated = eliminated;
            this.certificate = certificate;
        }
        public boolean getElim() {
            return eliminated;
        }
        public ArrayList<String> getCertificate() {
            return certificate;
        }
    }

    public BaseballElimination(String filename)                    // create a baseball division from given filename in format specified below
    {
        In in = new In(filename);
        String s = in.readLine();
        numTeams = Integer.parseInt(s);
        teamID = new HashMap<String, Integer>();
        teams = new String[numTeams];
        wins = new int[numTeams];
        loses = new int[numTeams];
        remains = new int[numTeams];
        games = new int[numTeams][numTeams];

        for (int i = 0; i < numTeams; i++) {
            s = in.readLine();
            String[] data = s.trim().split("\\s+");
            teams[i] = data[0];
            teamID.put(teams[i], i);
            wins[i] = Integer.parseInt(data[1]);
            loses[i] = Integer.parseInt(data[2]);
            remains[i] = Integer.parseInt(data[3]);
            for (int j = 4; j < data.length; j++) {
                games[i][j - 4] = Integer.parseInt(data[j]);
            }
        }

    }
    public int numberOfTeams()                        // number of teams
    {
        return numTeams;
    }
    public Iterable<String> teams()                                // all teams
    {
        ArrayList<String> teamNames = new ArrayList<String>();
        for (int i = 0; i < teams.length; i++) {
            teamNames.add(teams[i]);
        }
        return teamNames;
    }
    public int wins(String team)                      // number of wins for given team
    {
        if (teamID.get(team) == null)
            throw new IllegalArgumentException("Wrong Team Name");
        int id = teamID.get(team);
        return wins[id];
    }
    public int losses(String team)                    // number of losses for given team
    {
        if (teamID.get(team) == null)
            throw new IllegalArgumentException("Wrong Team Name");
        int id = teamID.get(team);
        return loses[id];
    }
    public int remaining(String team)                 // number of remaining games for given team
    {
        if (teamID.get(team) == null)
            throw new IllegalArgumentException("Wrong Team Name");
        int id = teamID.get(team);
        return remains[id];
    }
    public int against(String team1, String team2)    // number of remaining games between team1 and team2
    {
        if (teamID.get(team1) == null || teamID.get(team2) == null) {
            throw new IllegalArgumentException("Wrong Team Name");
        }
        int id1 = teamID.get(team1);
        int id2 = teamID.get(team2);
        return games[id1][id2];
    }

    // helper method to find the factorial
    private int factorial(int n) {
        int result = 1;
        for (int i = n; i > 0; i--) {
            result *= i;
        }
        return result;
    }

    private Result solver(String team)
    {
        // get an ArrayList of teams other than the query team
        ArrayList<String> teamNames = new ArrayList<String>();
        for (String t : teams()) {
            if (!t.equals(team))
                teamNames.add(t);
        }
        int maxPossibleWins = wins(team) + remaining(team);
        // test for trivial elimination
        for (String name : teamNames) {
            if (maxPossibleWins < wins(name)) {
                boolean eliminated = true;
                ArrayList<String> certificate = new ArrayList<String>();
                certificate.add(name);
                Result result = new Result(eliminated, certificate);
                return result;
            }
        }
        int numTeamVertices = numTeams - 1;
        int st = 2;
        int h2h = numTeamVertices * (numTeamVertices - 1) / 2;
        FlowNetwork flow = new FlowNetwork(numTeamVertices + st + h2h);
        // add edges from source vertex to each of the h2h vertices
        int h2hIndex = 1;
        int source = 0;
        int maxCapacity = 0;
        for (int i = 0; i < (teamNames.size() - 1); i++) {
            for (int j = i + 1; j < teamNames.size(); j++) {
                int h2hValue = against(teamNames.get(i), teamNames.get(j));
                maxCapacity += h2hValue;
                // 0 is the index of the source vertex
                // h2hIndex is the index of the current h2h vertex
                FlowEdge edge = new FlowEdge(source, h2hIndex, h2hValue);
                flow.addEdge(edge);
                // add two edges from a h2h vertex to each of the teams
                int team1Vertex = i + h2h + 1;
                int team2Vertex = j + h2h + 1;
                edge = new FlowEdge(h2hIndex, team1Vertex, Double.POSITIVE_INFINITY);
                flow.addEdge(edge);
                edge = new FlowEdge(h2hIndex, team2Vertex, Double.POSITIVE_INFINITY);
                flow.addEdge(edge);
                h2hIndex++;
            }
        }
        //add edges from each team to the terminal vertex
        int terminal = flow.V() - 1;
        for (int i = 0; i < teamNames.size(); i++) {
            int teamVertex = i + h2h + 1;
            int maxNumWins = maxPossibleWins - wins(teamNames.get(i));
            FlowEdge edge = new FlowEdge(teamVertex, terminal, maxNumWins);
            flow.addEdge(edge);
        }
        //System.out.println(flow);

        FordFulkerson maxflow = new FordFulkerson(flow, source, terminal);
        //System.out.println("Max Flow: " + maxflow.value());
        //System.out.println("Max Capacity: " + maxCapacity);
        boolean eliminated = (maxCapacity != maxflow.value());
        // build the Result
        if (eliminated) {
            // find the certificate of elimination
            ArrayList<String> minCut = new ArrayList<String>();
            for (int i = h2h + 1; i < terminal; i++) {
                if (maxflow.inCut(i)) {
                    minCut.add(teamNames.get(i - h2h - 1));
                }
            }
            Result result = new Result(eliminated, minCut);
            return result;
        }
        else {
            Result result = new Result(eliminated, null);
            return result;
        }
    }

    public boolean isEliminated(String team)              // is given team eliminated?
    {
        Result result = solver(team);
        return result.getElim();
    }
    public Iterable<String> certificateOfElimination(String team)  // subset R of teams that eliminates given team; null if not eliminated
    {
        Result result = solver(team);
        return result.getCertificate();
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        division.wins("Princeton");
        //division.certificateOfElimination("Detroit");
        /*
        for (String team: division.teams()) {
            //System.out.println(division.wins(team));
            System.out.println("Current Team: " + team);
            division.isEliminated(team);
            System.out.println();
        }
        */
        /*
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
        */
    }
}
