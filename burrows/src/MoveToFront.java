/**
 * Move to Front Class
 * Name: Tri Minh Cao
 * Email: trimcao@gmail.com
 * Date: December 2015
 */
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        // initialize the ordered sequence of extended ASCII chars
        char[] orderedChar = new char[256];
        for (int i = 0; i < 256; i++) {
            char c = (char) i;
            orderedChar[i] = c;
        }
        while(!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = -1;
            for (int i = 0; i < orderedChar.length; i++) {
                if (orderedChar[i] == c) {
                    index = i;
                    break;
                }
            }
            BinaryStdOut.write(index, 8);
            for (int i = index - 1; i >= 0; i--) {
                orderedChar[i + 1] = orderedChar[i];
            }
            orderedChar[0] = c;
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        // initialize the ordered sequence of extended ASCII chars
        char[] orderedChar = new char[256];
        for (int i = 0; i < 256; i++) {
            char c = (char) i;
            orderedChar[i] = c;
        }
        while(!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readInt(8);
            char c = orderedChar[index];
            BinaryStdOut.write(c);
            for (int i = index - 1; i >= 0; i--) {
                orderedChar[i + 1] = orderedChar[i];
            }
            orderedChar[0] = c;
        }
        BinaryStdOut.flush();
    }
    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }

    }
}
