package org.grapheco;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.BitSet;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BITTest {

    @Test
    void sizeTest() throws Exception {
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI.csv"), 1L);
        System.out.println(bit.printSize());
    }

    @Test
    void geneFilter() throws Exception {
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI_bak.csv"), 1L);

        long[][] test = IO.read(new File("../dataset/test/query3.csv")).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        for (long[] t: test){
            long t0 = System.currentTimeMillis();
            int size = bit.geneFilter(BitSet.valueOf(bit.getCodeById(t[0]))).length;
            System.out.println(System.currentTimeMillis() - t0 + "," + size + "," + t[1]);
        }
    }
}