package org.grapheco;

import java.util.ArrayList;

public class MathUtils {
    public static final int CHAT_LIST_SIZE = 30;
    public static final int COM_LIST_SIZE = 20;
    public static int[] cList = new int[CHAT_LIST_SIZE];
    public static int[][][] comList = new int[COM_LIST_SIZE][][];

    static {
        for (int i = 0; i < CHAT_LIST_SIZE; i++) cList[i] = c(i + 1, Math.floorDiv(i + 1, 2));
        for (int i = 0; i < COM_LIST_SIZE; i++) {
            comList[i] = code(i + 1, Math.floorDiv(i + 1, 2), 1).toArray(new int[][]{});
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

    public static int sp(int n) {
        int k = 0;
        while (k < CHAT_LIST_SIZE && cList[k] < n) k++;
        return k + 1;
    }

    public static int spBS(int n) {
        int low = 0;
        int high = cList.length - 1;
        while (low < high) {
            int mid = (low + high) / 2;
            if (n < cList[mid]) {
                high = mid - 1;
            } else if (n == cList[mid]) {
                return mid;
            } else {
                low = mid + 1;
            }
        }
        return high + 1;
    }

    /**
     *
     * @param n bit number
     * @return combinations of the bit
     */
    public static int[][] getCombinations(int n) {
        return n <= COM_LIST_SIZE ?
                comList[n-1] :
                code(n, Math.floorDiv(n, 2), 1).toArray(new int[][]{});
    }

    private static ArrayList<int[]> code(int n, int k, int s) {
        if (k > n || s > n) return new ArrayList<>();
        ArrayList<int[]> coms = new ArrayList<>(c(n-s, k));
        if (k == 1) {
            for (int i = s; i <= n; i++) {
                coms.add(new int[]{i});
            }
        } else {
            ArrayList<int[]> sub1 = code(n, k - 1, s + 1);
            sub1.forEach(i->{
                int[] c = new int[k];
                c[0] = s;
                System.arraycopy(i, 0, c, 1, k-1);
                coms.add(c);
            });
            ArrayList<int[]> sub0 = code(n, k, s + 1);
            coms.addAll(sub0);
        }
        return coms;
    }

}
