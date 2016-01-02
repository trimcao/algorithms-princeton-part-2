/**
 * Burrows Wheeler Data Processing
 * Name: Tri Minh Cao
 * Email: trimcao@gmail.com
 * Date: December 2015
 */
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import java.util.ArrayList;

public class BurrowsWheeler {
    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode() {
        while(!BinaryStdIn.isEmpty()) {
            String s = BinaryStdIn.readString();
            CircularSuffixArray csa = new CircularSuffixArray(s);
            int first = -1;
            // later: may not need to build char[] t here
            char[] t = new char[csa.length()];
            for (int i = 0; i < csa.length(); i++) {
                int next = (csa.index(i) - 1) % csa.length();
                if (next < 0) next += csa.length();
                t[i] = s.charAt(next);
                if (csa.index(i) == 0) {
                    first = i;
                }
            }
            BinaryStdOut.write(first);
            for (int i = 0; i < csa.length(); i++) {
                BinaryStdOut.write(t[i]);
            }
        }
        BinaryStdOut.flush();

    }

    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode() {
        // build the next[] array
        int first = BinaryStdIn.readInt();
        //System.out.println(first);
        ArrayList<Character> t = new ArrayList<Character>();
        while (!BinaryStdIn.isEmpty()) {
            t.add(BinaryStdIn.readChar());
        }
        int N = t.size();
        int[] count = new int[256 + 1];
        int[] next = new int[N];
        int[] sorted = new int[N];
        // count frequencies of each letter using key as index
        for (int i = 0; i < N; i++) {
            count[t.get(i) + 1]++;
        }
        // compute frequency cumulates which specify destinations
        for (int r = 0; r < 256; r++) {
            count[r + 1] += count[r];
        }
        // Access cumulates using key as index to move item
        for (int i = 0; i < N; i++) {
            int index = count[t.get(i)];
            next[index] = i;
            sorted[index] = t.get(i);
            count[t.get(i)]++;
        }

        int index = first;
        for (int i = 0; i < N; i++) {
            BinaryStdOut.write((char) sorted[index]);
            index = next[index];
        }
        BinaryStdOut.flush();
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
        // write some draft code here
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
    }
}
