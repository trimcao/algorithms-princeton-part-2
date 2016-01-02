public class TestMSD {
    public static void main (String args[]) {
        String s = "AAAAAAAAAAAAAAAAAAAAAAAA";
        //String s = "AAAAAAA";
        //String s = "ABRACADABRA!";
        int[] index = new int[s.length()];
        for (int i = 0; i < index.length; i++) {
            index[i] = i;
        }
        MSDSuffix.sort(s, index);
        for (int i = 0; i < index.length; i++) {
            System.out.println(index[i]);
        }
    }
}
