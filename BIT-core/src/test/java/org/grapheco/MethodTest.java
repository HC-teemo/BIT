package org.grapheco;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import static org.grapheco.BITTest.appendToFile;
import static org.grapheco.BITTest.readCSV;

public class MethodTest {
    @Test
    void lcaList() throws Exception {
        TransMethod transMethod = TransMethod.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String logfile = "../log/TR-exp3.txt";
        String filepath2 = "../dataset/exp3/lca2.csv";
        String filepath4 = "../dataset/exp3/lca4.csv";
        String filepath8 = "../dataset/exp3/lca8.csv";
        String filepath16 = "../dataset/exp3/lca16.csv";
        String filepath32 = "../dataset/exp3/lca32.csv";
        String addition1 = "../dataset/exp3/exp3-addition1.csv";
        String addition2 = "../dataset/exp3/exp3-addition2.csv";

        String[] all_file = {filepath2,
                filepath4, filepath4, filepath8, filepath16, filepath32, addition1, addition2
        };

            for (String file : all_file) {
                long[][] test = readCSV(new File(file));

                ArrayList<Long> acestors = new ArrayList<>();

                appendToFile(logfile, file + "\n");
                int TIMES=10;

                double time = 0;
            for (int j = 0; j < TIMES; j++) {
                long t0 = System.nanoTime();
                for (long[] row : test) {
                    acestors.add(transMethod.lca(row).getId());
                }
                time += (System.nanoTime() - t0) / 1e6;
            }
//                appendToFile(logfile, acestors.toString());
                appendToFile(logfile, String.valueOf(time/TIMES) + "\n");

        }
    }

    @Test
    void list() throws Exception {
        TransMethod transMethod = TransMethod.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String pathname = "../dataset/exp2/h6_100.csv";
        long[][] test = IO.read(new File(pathname)).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        int TIME = 1;

        for (int i = 0; i < TIME; i++) {
            long t0 = System.nanoTime();
            for (long[] t : test) {
                int size = transMethod.list(t[0]).size();
//                    System.out.println(size);
            }
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            System.out.println("round" + i + ":" + milliseconds);
        }
    }

    @Test // BIT exp4 -> is ancestor
    void isAncestor_() throws Exception{
        TransMethod transMethod = TransMethod.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String inputfile = "../dataset/exp4/exp4-3-nochild.csv";

        long[][] test = IO.read(new File(inputfile)).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        for (int i = 0; i < 10; i++) {
            long t0 = System.nanoTime();
            for (long[] t: test){
                boolean isancestor = transMethod.judge(t[1],t[0]);
//                System.out.println(isancestor);
            }
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            System.out.println("round"+i+":"+milliseconds+ "\n");
        }

    }

}

