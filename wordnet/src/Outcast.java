/**
 * Outcast Class for WordNet
 * Name: Tri Minh Cao
 * Email: trimcao@gmail.com
 * Date: December 2015
 */
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.HashMap;

public class Outcast {
    private WordNet wordnet;
    private HashMap<String, Integer> distances;

    public Outcast(WordNet wordnet)         // constructor takes a WordNet object
    {
        this.wordnet = wordnet;
        //distances = new HashMap<String, Integer>();
    }

    public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
    {
        distances = new HashMap<String, Integer>();
        // compute all the distances in the nouns list
        for (int i = 0; i < (nouns.length - 1); i++) {
            for (int j = i + 1; j < nouns.length; j++) {
                String noun1 = nouns[i];
                String noun2 = nouns[j];
                int dist = wordnet.distance(noun1, noun2);
                String nounCombo = noun1 + "_" + noun2;
                //System.out.println(nounCombo + ": " + dist);
                distances.put(nounCombo, dist);
            }
        }

        // find the max Distance
        int maxDist = -1;
        String outcastWord = null;
        for (int i = 0; i < nouns.length; i++) {
            int totalDist = 0;
            for (int j = 0; j < nouns.length; j++) {
                if (i != j) {
                    String key = nouns[i] + "_" + nouns[j];
                    if (distances.get(key) == null) {
                        key = nouns[j] + "_" + nouns[i];
                    }
                    int dist = distances.get(key);
                    totalDist += dist;
                }
            }
            if (totalDist > maxDist) {
                maxDist = totalDist;
                outcastWord = nouns[i];
            }
        }

        return outcastWord;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
