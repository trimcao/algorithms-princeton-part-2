/**
 * Circular Suffix Array class for Burrows-Wheeler Algo
 * Name: Tri Minh Cao
 * Email: trimcao@gmail.com
 * Date: December 2015
 */

import java.util.Arrays;
public class CircularSuffixArray {
    private String s;
    private int[] indices;
    private int sufLength;

    public CircularSuffixArray(String s)  // circular suffix array of s
    {
        this.s = s;
        this.sufLength = s.length();
        indices = new int[sufLength];
        for (int i = 0; i < sufLength; i++) {
            indices[i] = i;
        }
        MSDSuffix.sort(s, indices);
        this.indices = indices;
    }

    public int length()                   // length of s
    {
        return this.sufLength;
    }

    public int index(int i)               // returns index of ith sorted suffix
    {
        return indices[i];
    }

    public static void main(String[] args)// unit testing of the methods (optional)
    {
        String s = "ABRACADABRA!";
        CircularSuffixArray test = new CircularSuffixArray(s);

        for (int i = 0; i < test.length(); i++) {
            System.out.println(test.index(i));
        }

    }
}
