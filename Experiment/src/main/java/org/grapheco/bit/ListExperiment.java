package org.grapheco.bit;

import java.io.File;
import java.util.BitSet;

public class ListExperiment {
    public static void main(String[] args) throws Exception {
        list();
        listParallel();
    }

    static void list() throws Exception {
        String path = System.getProperty("user.dir") + "/dataset/out/";
        BITwithBytes bit = BITwithBytes.deserialize(path);

        long[][] test = IO.read(DataSet.list4).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        for (int i = 0; i < 1; i++) {
            System.out.println("Running Task Times: "+test.length);
            long t0 = System.nanoTime();
            for (long[] t : test) {
                int size = bit.geneFilter2(bit.getCodeById(t[0])).length;
//                int size = BitSet.valueOf(bit.filter2(BitSet.valueOf(bit.getCodeById(t[0])).stream().toArray())).cardinality();
//                System.out.println(size);
            }
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            System.out.println("no parallel" + i + ":" + milliseconds);
        }
    }

    static void listParallel() throws Exception {
        String path = System.getProperty("user.dir") + "/dataset/out/";
        BITwithBytes bit = BITwithBytes.deserialize(path);

        long[][] test = IO.read(DataSet.list4).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        for (int i = 0; i < 1; i++) {
            System.out.println("Running Task Times: "+test.length);
            long t0 = System.nanoTime();
            for (long[] t : test) {
                int size = bit.geneFilter(bit.getCodeById(t[0])).length;
//                int size = BitSet.valueOf(bit.filter2(BitSet.valueOf(bit.getCodeById(t[0])).stream().toArray())).cardinality();
//                System.out.println(size);
            }
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            System.out.println("parallel" + i + ":" + milliseconds);
        }
    }

}
