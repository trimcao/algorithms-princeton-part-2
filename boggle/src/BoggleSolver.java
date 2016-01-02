/**
 * Assignment 4: Boggle Solver
 * Name: Tri Minh Cao
 * Date: December 2015
 * Email: trimcao@gmail.com
 */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import java.util.HashSet;
import java.util.Iterator;

public class BoggleSolver
{
    private TrieABC<Integer> dict;
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary)
    {
        dict = new TrieABC<Integer>();
        int count = 0;
        for (int i = 0; i < dictionary.length; i++) {
            dict.put(dictionary[i], count);
            count++;
        }
    }

    private boolean validPos(BoggleBoard board, int row, int col) {
        return !((row < 0) || (row >= board.rows()) || (col < 0) || (col >= board.cols()));
    }

    private int posToNum(BoggleBoard board, int row, int col) {
        int numCols = board.cols();
        return row * numCols + col;
    }
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board)
    {
        HashSet<String> words = new HashSet<String>();
        // run the search dfs method starting from each of the board position
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                HashSet<Integer> used = new HashSet<Integer>();
                StringBuilder start = new StringBuilder();
                search(board, i, j, start, words, used);
            }
        }
        return words;
    }

    private boolean validCall(BoggleBoard board, int row, int col, HashSet<Integer> used) {
        if (validPos(board, row, col)) {
            int numPos = posToNum(board, row, col);
            if (used.contains(numPos))
                return false;
            else
                return true;
        }
        else
            return false;
    }

    private void search(BoggleBoard board, int row, int col, StringBuilder word, HashSet<String> wordList, HashSet<Integer> dicesUsed) {
        //System.out.println("Word: " + word);
        //System.out.println("Row: " + row);
        //System.out.println("Col: " + col);
        // base cases
        // invalid position

        if (!validPos(board, row, col)) {
            return;
        }
        // check if the position is already used
        int numPos = posToNum(board, row, col);
        if (dicesUsed.contains(numPos)) {
            return;
        }
        // start process
        //System.out.println("Word 2: " + word);
        //StringBuilder nextWord = new StringBuilder(word);
        char nextChar = board.getLetter(row, col);
        if (nextChar == 'Q') {
            word.append('Q');
            word.append('U');
        }
        else
            word.append(nextChar);
        // check prefix
        int prefixVal = dict.isPrefix(word.toString());
        if (prefixVal == 0) {
            word.deleteCharAt(word.length() - 1);
            if (word.length() > 0) {
                if (word.charAt(word.length() - 1) == 'Q')
                    word.deleteCharAt(word.length() - 1);
            }
            return;
        }
        //System.out.println("Next Word: " + nextWord);
        HashSet<Integer> newDicesUsed = new HashSet<Integer>();
        Iterator<Integer> iter = dicesUsed.iterator();
        while (iter.hasNext()) {
            newDicesUsed.add(iter.next());
        }
        newDicesUsed.add(numPos);
        // check if the word is in the dict
        String newWord = word.toString();
        if (newWord.length() >= 3) {
            if (prefixVal == 2) {
                wordList.add(newWord);
            }
        }
        // recursive calls
        search(board, row - 1, col - 1, word, wordList, newDicesUsed);
        search(board, row - 1, col, word, wordList, newDicesUsed);
        search(board, row - 1, col + 1, word, wordList, newDicesUsed);
        search(board, row, col - 1, word, wordList, newDicesUsed);
        search(board, row, col + 1, word, wordList, newDicesUsed);
        search(board, row + 1, col - 1, word, wordList, newDicesUsed);
        search(board, row + 1, col, word, wordList, newDicesUsed);
        search(board, row + 1, col + 1, word, wordList, newDicesUsed);

        word.deleteCharAt(word.length() - 1);
        if (word.length() > 0) {
            if (word.charAt(word.length() - 1) == 'Q')
                word.deleteCharAt(word.length() - 1);
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word)
    {
        if (!dict.contains(word))
            return 0;
        int length = word.length();
        if (length <= 2) return 0;
        else if (length <= 4) return 1;
        else if (length == 5) return 2;
        else if (length == 6) return 3;
        else if (length == 7) return 5;
        else                  return 11;

    }

    public static void main (String[] args) {
        /*
        StringBuilder test = new StringBuilder();
        System.out.println(test.toString());
        test.append('a');
        test.append('b');
        System.out.println(test.toString());
        */
        //BoggleBoard test = new BoggleBoard("board-q.txt");
        //System.out.println(test.getLetter(2,1));
        //System.out.println(('Q' == 'Q'));

        // Stress test the solver
        
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        int count = 0;
        while (count < 1000000) {
            BoggleBoard board = new BoggleBoard(4, 4);
            solver.getAllValidWords(board);
            count++;
            if (count % 1000 == 0) {
                System.out.println(count);
            }
        }

        /*
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board))
        {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        */
    }
}
