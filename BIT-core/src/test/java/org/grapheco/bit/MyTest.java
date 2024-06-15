package org.grapheco.bit;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.BitSet;

public class MyTest {
    @Test
    public void bitmapSizeTest() throws Exception {
        BITwithBitset bit = BITwithBitset.fromCSV("../dataset/NCBI414-coded.csv");
    }

    @Test
    public void bytesSizeTest() throws Exception {
        BITwithBytes bit = BITwithBytes.fromCSV("../dataset/NCBI414-coded.csv");
        bit.toCSV("../dataset/NCBI414-coded.csv");
    }

    @Test
    void roaringBitmapSizeTest() throws Exception {
        BIT bit = BIT.fromCSV("../dataset/NCBI414-coded.csv");
    }

    @Test
    void BITwithByteslist2() throws Exception {
        BITwithBytes bit = BITwithBytes.fromCSV("../dataset/NCBI414-coded.csv");

        String pathname = "../dataset/exp2/h6_1.csv"; //
        long[][] test = IO.read(new File(pathname)).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        for (int i = 0; i < 10; i++) {
            System.out.println("Running Task Times: "+test.length);
            int pass = 0;
            long t0 = System.nanoTime();
            for (long[] t : test) {
                int size = bit.geneFilter(BitSet.valueOf(bit.getCodeById(t[0]))).length;
                System.out.println(size);
                if (size == t[1]) pass++;
            }
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            System.out.println("parallel" + i + ":" + milliseconds + " acc: " + pass/test.length);
        }
    }
}
