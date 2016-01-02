/**
 * Assignment 1: WordNet
 * Name: Tri Minh Cao
 * Email: trimcao@gmail.com
 * Date: December 2015
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.RedBlackBST;
import java.util.ArrayList;
import edu.princeton.cs.algs4.DirectedCycle;

public class WordNet {
    private Digraph graph;
    private RedBlackBST<String, String> nounData;
    private RedBlackBST<Integer, String> synsetData;
    private SAP findSAP;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        nounData = new RedBlackBST<String, String>();
        synsetData = new RedBlackBST<Integer, String>();
        int numID = 0;
        In in;
        try {
            // read the synsets and build a BST to store the nouns and there ids
            in = new In(synsets);
            String[] data;
            while (!in.isEmpty()) {
               numID++;
               data = in.readLine().split(",");
               // remember the synset id is still string
               String value = data[0];
               int synsetID = Integer.parseInt(value);
               // add the synset to synsetData BST
               synsetData.put(synsetID, data[1]);
               //String words = data[1];
               String[] singleWord;
               singleWord = data[1].split(" ");
               // add each noun to nounData BST
               for (int i = 0; i < singleWord.length; i++) {
                    // check if the word is already in the BST, update it to include
                    // multiple synsets
                    String word = singleWord[i];
                    String listSynsets = nounData.get(word);
                    if (listSynsets != null)
                        listSynsets += " " + value;
                    else
                        listSynsets = value;
                   nounData.put(singleWord[i], listSynsets);
                   //StdOut.println("Word: " + singleWord[i] + " - Value: " + value);
               }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        //StdOut.println("Size of BST: " + nounData.size());
        // build the digraph WordNet using file hypernyms
        graph = new Digraph(numID);
        try {
            in = new In(hypernyms);
            String[] data;
            while (!in.isEmpty()) {
                data = in.readLine().split(",");
                int tail = Integer.parseInt(data[0]);
                for (int i = 1; i < data.length; i++) {
                    graph.addEdge(tail, Integer.parseInt(data[i]));
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        // check if the graph is rooted
        int numRoot = 0;
        for (int i = 0; i < numID; i++) {
            int outdegree = graph.outdegree(i);
            if (outdegree == 0)
                numRoot++;
            if (numRoot > 1) {
                throw new IllegalArgumentException("the graph has more than one root");
            }
        }
        // check if the graph is acyclic
        DirectedCycle diCycle = new DirectedCycle(graph);
        if (diCycle.hasCycle())
            throw new IllegalArgumentException("the graph is not acyclic");
        //StdOut.println(graph.toString());
        findSAP = new SAP(graph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounData.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (nounData.get(word) == null)
            return false;
        else
            //System.out.println(nounData.get(word));
            return true;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB) )
            throw new IllegalArgumentException("noun(s) is not in WordNet");
        ArrayList<Integer> synsetsA = getSynsets(nounA);
        ArrayList<Integer> synsetsB = getSynsets(nounB);
        //System.out.println("SynsetsA size: " + synsetsA.size());
        return findSAP.length(synsetsA, synsetsB);
    }
    // helper method to find the synsets in integers from a string of synsets
    private ArrayList<Integer> getSynsets(String noun) {
        // get an iterable list of synsets of nounA and nounB
        String synsets = nounData.get(noun);
        String[] synsetsData = synsets.split(" ");
        ArrayList<Integer> synsetsInt = new ArrayList<Integer>();
        for (int i = 0; i < synsetsData.length; i++) {
            synsetsInt.add(Integer.parseInt(synsetsData[i]));
        }
        return synsetsInt;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB) )
            throw new IllegalArgumentException("noun(s) is not in WordNet");
        ArrayList<Integer> synsetsA = getSynsets(nounA);
        ArrayList<Integer> synsetsB = getSynsets(nounB);
        int ancestorID = findSAP.ancestor(synsetsA, synsetsB);
        return synsetData.get(ancestorID);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        //String synsets = "synsets15.txt";
        //String hypernyms = "hypernyms15Tree.txt";
        String synsets = args[0];
        String hypernyms = args[1];
        WordNet test = new WordNet(synsets, hypernyms);
        //StdOut.println(test.isNoun("a"));
        //StdOut.println(test.isNoun("h"));
        //StdOut.println(test.isNoun("abc"));
        while (!StdIn.isEmpty()) {
            String v = StdIn.readString();
            String w = StdIn.readString();
            int length   = test.distance(v, w);
            String ancestor = test.sap(v, w);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
            StdOut.println();
        }
        //System.out.println(test.distance("o", "b"));
        //System.out.println(test.sap("o", "b"));

    }
}
