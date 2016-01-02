/**
 *  The <tt>MSD</tt> class provides static methods for sorting an
 *  array of extended ASCII strings or integers using MSD radix sort.
 *  <p>
 *  For additional documentation,
 *  see <a href="http://algs4.cs.princeton.edu/51radix">Section 5.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  
 *  Modified by Tri Minh Cao
 *  Email: trimcao@gmail.com
 */
public class MSDSuffix {
    private static final int BITS_PER_BYTE =   8;
    private static final int BITS_PER_INT  =  32;   // each Java int is 32 bits
    private static final int R             = 256;   // extended ASCII alphabet size
    private static final int CUTOFF        =  15;   // cutoff to insertion sort

    // do not instantiate
    private MSDSuffix() { }

   /**
     * Rearranges the array of extended ASCII strings in ascending order.
     *
     * @param a the array to be sorted
     */
    public static void sort(String s, int[] index) {
        int N = s.length();
        int[] aux = new int[N];
        sort(s, index, N, 0, N-1, 0, aux);
    }

    // return dth character of s, -1 if d = length of string
    private static int charAt(String s, int d) {
        assert d >= 0 && d <= s.length();
        if (d == s.length()) return -1;
        return s.charAt(d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private static void sort(String s, int[] index, int length, int lo, int hi, int d, int[] aux) {

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(s, index, lo, hi, d, length);
            return;
        }

        // base case: if d reaches the length, return
        if (d == length) {
            return;
        }

        // compute frequency counts
        int[] count = new int[R+2];
        for (int i = lo; i <= hi; i++) {
            int c = s.charAt((index[i] + d) % length);
            count[c+2]++;
        }

        // transform counts to indicies
        for (int r = 0; r < R+1; r++)
            count[r+1] += count[r];

        // distribute
        for (int i = lo; i <= hi; i++) {
            int c = s.charAt((index[i] + d) % length);
            aux[count[c+1]++] = index[i];
        }

        // copy back
        for (int i = lo; i <= hi; i++)
            index[i] = aux[i - lo];

        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < R; r++)
            sort(s, index, length, lo + count[r], lo + count[r+1] - 1, d+1, aux);
    }


    // insertion sort a[lo..hi], starting at dth character
    private static void insertion(String s, int[] index, int lo, int hi, int d, int length) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(s, index, j, j-1, d, length); j--)
                exch(index, j, j-1);
    }

    // exchange a[i] and a[j]
    private static void exch(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // is suffix in index x less than suffix in index y, starting at character d
    private static boolean less(String s, int[] index, int x, int y, int d, int length) {
        for (int i = d; i < length; i++) {
            char xChar = s.charAt((index[x] + i) % length);
            char yChar = s.charAt((index[y] + i) % length);
            if (xChar < yChar) return true;
            if (xChar > yChar) return false;
        }
        return false;
    }
}
