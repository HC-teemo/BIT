import scala.Array;

import java.util.ArrayList;

public class MathUtils {
    public static final int CHAT_LIST_SIZE = 20; // C(20, 10) = 184756
    public static final int COM_LIST_SIZE = 15; // C(15, 7) = 6453
    public static int[] cList = new int[CHAT_LIST_SIZE];
    public static int[][][] comList = new int[COM_LIST_SIZE][][];

    static {
        for (int i = 0; i < CHAT_LIST_SIZE; i++) cList[i] = c(i + 1, Math.floorDiv(i + 1, 2));
        for (int i = 0; i < COM_LIST_SIZE; i++) {
            comList[i] = combinations(i + 1, Math.floorDiv(i + 1, 2), 1).toArray(new int[][]{});
        }
    }

    /*
        r = ⌊n / 2⌋
        c(n) = n! / r!(n-r)!
     */
    public static int c(int n, int r) {
        long nu = 1;
        long de = 1;
        for (int i = r + 1; i <= n; i++) nu *= i;
        for (int i = 2; i <= n - r; i++) de *= i;
        return (int) (nu / de);
    }

    public static int cHat(int n) {
        int k = 0;
        while (k < CHAT_LIST_SIZE && cList[k] < n) k++;
        return k + 1;
    }

    /**
     *
     * @param n bit number
     * @return combinations of the bit
     */
    public static int[][] getCombinations(int n) {
        return n <= COM_LIST_SIZE ?
                comList[n-1] :
                combinations(n, Math.floorDiv(n, 2), 1).toArray(new int[][]{});
    }

    private static ArrayList<int[]> combinations(int n, int k, int s) {
        if (k > n || s > n) return new ArrayList<>();
        ArrayList<int[]> coms = new ArrayList<>(c(n-s, k));
        if (k == 1) {
            for (int i = s; i <= n; i++) {
                coms.add(new int[]{i});
            }
        } else {
            ArrayList<int[]> sub1 = combinations(n, k - 1, s + 1);
            sub1.forEach(i->{
                int[] c = new int[k];
                c[0] = s;
                Array.copy(i, 0, c, 1, k-1);
                coms.add(c);
            });
            ArrayList<int[]> sub0 = combinations(n, k, s + 1);
            coms.addAll(sub0);
        }
        return coms;
    }

}
