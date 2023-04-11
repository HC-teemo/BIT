package org.grapheco;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

class MathUtilsTest {

    @Test
    void c() {
        Assertions.assertEquals(1, MathUtils.c(1, 0));
        Assertions.assertEquals(2, MathUtils.c(2, 1));
        Assertions.assertEquals(3, MathUtils.c(3, 1));
        Assertions.assertEquals(6, MathUtils.c(4, 2));
        Assertions.assertEquals(10, MathUtils.c(5, 2));
        Assertions.assertEquals(20, MathUtils.c(6, 3));
        Assertions.assertEquals(35, MathUtils.c(7, 3));
        Assertions.assertEquals(70, MathUtils.c(8, 4));
        Assertions.assertEquals(126, MathUtils.c(9, 4));
        Assertions.assertEquals(252, MathUtils.c(10, 5));
    }

    @Test
    void CHat() {
        Assertions.assertEquals(2, MathUtils.sp(2));
        Assertions.assertEquals(3, MathUtils.sp(3));
        Assertions.assertEquals(4, MathUtils.sp(4));
        Assertions.assertEquals(4, MathUtils.sp(5));
        Assertions.assertEquals(4, MathUtils.sp(6));
        Assertions.assertEquals(5, MathUtils.sp(7));
        Assertions.assertEquals(5, MathUtils.sp(9));
        Assertions.assertEquals(5, MathUtils.sp(10));
        Assertions.assertEquals(6, MathUtils.sp(11));
        Assertions.assertEquals(6, MathUtils.sp(20));
        Assertions.assertEquals(11, MathUtils.sp(253));
    }

    public static double log2nlz( double bits )
    {
        return Math.log10(bits) / Math.log10(2);
    }

    @Test
    void CHatWithLog() {
        Random r = new Random();
        int MAX = 184756;
        int testSize = 100000000;
        int [] ns = new int[testSize];
        int [] r1 = new int[testSize];
        int [] r2 = new int[testSize];
        int [] r3 = new int[testSize];
        // set
        for (int i = 0; i < testSize; i++) {
            ns[i] = r.nextInt(MAX);
        }
        // by log
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < testSize; i++) {
            int n = ns[i];
            double k = log2nlz(n) + log2nlz(log2nlz(n))/2 + 1;
            r1[i] = (int) k;
        }
        // by search
        long t2 = System.currentTimeMillis();
        for (int i = 0; i < testSize; i++) {
            r2[i] = MathUtils.sp(ns[i]);
        }
        // by bi-search
        long t3 = System.currentTimeMillis();
        for (int i = 0; i < testSize; i++) {
            r3[i] = MathUtils.spBS(ns[i]);
        }
        long t9 = System.currentTimeMillis();

        System.out.println(t2 - t1);
        System.out.println(t3 - t2);
        System.out.println(t9 - t3);

        for (int i = 0; i < 100; i++) {
            System.out.print(r1[i]);
            System.out.print(r2[i]);
            System.out.print(r3[i]);
            System.out.println();
        }
    }

    @Test
    void getCombinations() {
        Assertions.assertEquals(2, MathUtils.getCombinations(2).length);
        Assertions.assertEquals(3, MathUtils.getCombinations(3).length);
        Assertions.assertEquals(6, MathUtils.getCombinations(4).length);
        Assertions.assertEquals(10, MathUtils.getCombinations(5).length);
        Assertions.assertEquals(20, MathUtils.getCombinations(6).length);
        Assertions.assertEquals(35, MathUtils.getCombinations(7).length);
        Assertions.assertEquals(70, MathUtils.getCombinations(8).length);
        Assertions.assertEquals(126, MathUtils.getCombinations(9).length);
        Assertions.assertEquals(252, MathUtils.getCombinations(10).length);
    }

    @Test
    void com(){
        int[][] a = MathUtils.getCombinations(3);
        System.out.print(Arrays.deepToString(a));
    }

    @Test
    void bitSetTest() {
        BitSet bitSet = new BitSet();
        bitSet = BitSet.valueOf(new long[]{0L});
        System.out.println(bitSet);
        bitSet = BitSet.valueOf(new long[]{1L});
        System.out.println(bitSet);
    }
}