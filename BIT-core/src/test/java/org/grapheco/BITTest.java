package org.grapheco;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BITTest {

    @Test
    void sizeTest() throws Exception {
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        System.out.println(bit.printSize());
    }

    @Test
    void list() throws Exception {
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String pathname = "../dataset/exp2/h6_1000.csv"; //
        String logpath = "../log/exp2/BIT-list.txt";
        long[][] test = IO.read(new File(pathname)).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        for (int j = 0; j < 20; j++) {
            appendToFile(logpath, pathname + "\n");

            for (int i = 0; i < 10; i++) {
                long t0 = System.nanoTime();

                for (long[] t : test) {
                    int size = bit.geneFilter(BitSet.valueOf(bit.getCodeById(t[0]))).length;
                }
                double milliseconds = (System.nanoTime() - t0) / 1e6;
                appendToFile(logpath, "round" + i + ":" + milliseconds + "\n");
                System.out.println("round" + i + ":" + milliseconds);
            }
        }
    }

    @Test //2496434 is a leaf node
    void list2496434() throws Exception {
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String logpath = "../log/exp2/BIT-list-2496434.txt";

        appendToFile(logpath,"2496434\n");

        for (int i = 0; i < 100; i++) {
            long t0 = System.nanoTime();
            int size = bit.geneFilter(BitSet.valueOf(bit.getCodeById(2496434L))).length;
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            appendToFile(logpath,"round"+i+":" + milliseconds +"\n");
            System.out.println("round"+i+":" + milliseconds );
        }
    }

    @Test
    void lca() throws Exception{
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);

        long[][] test = IO.read(new File("../dataset/test/query2.csv")).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);


        for (long[] t: test){
            long t0 = System.nanoTime();
            long ancestor = bit.commonAncestor(t);
            System.out.println(System.nanoTime() - t0 + "," + t[0] + "," + t[1]+","+ancestor);
        }
    }

    @Test // BIT exp3 -> lca
    void lcaList() throws Exception{
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String logfile = "../log/BIT-exp3.txt";
        String filepath2 = "../dataset/exp3/lca2.csv";
        String filepath4 = "../dataset/exp3/lca4.csv";
        String filepath8 = "../dataset/exp3/lca8.csv";
        String filepath16 = "../dataset/exp3/lca16.csv";
        String filepath32 = "../dataset/exp3/lca32.csv";
        String addition1 = "../dataset/exp3/exp3-addition1.csv";
        String addition2 = "../dataset/exp3/exp3-addition2.csv";

        String[] all_file = {filepath2,filepath4,filepath8,filepath16,filepath32,addition1,addition2};

        for (String file: all_file
             ) {
            long[][] test = readCSV(new File(file));

            long ancestor = 0L;

            appendToFile(logfile,file+"\n");

            for (int j =0;j<120;j++){
                long t0 = System.nanoTime();


                for (long[] row : test) {
                    ancestor = bit.commonAncestor(row);
                }
                double milliseconds = (System.nanoTime() - t0) / 1e6;
                String content = "round"+j+":"+milliseconds+ "\n";
                appendToFile(logfile,content);
            }
        }

    }



    @Test // BIT exp4 -> is ancestor
    void isAncestor_() throws Exception{
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String inputfile = "../dataset/exp4/exp4-3-nochild.csv";
        String logfile = "../log/BIT-exp4.txt";

        long[][] test = IO.read(new File(inputfile)).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        appendToFile(logfile,inputfile+"\n");

        for (int i = 0; i < 120; i++) {
            long t0 = System.nanoTime();
            for (long[] t: test){
                boolean isancestor = bit.isAncestor(t[0],t[1]);
            }
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            String content = "round"+i+":"+milliseconds+ "\n";
            appendToFile(logfile,content);
        }

    }

    public static long[][] readCSV(File file) throws IOException {
        List<long[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean skipFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (skipFirstLine) {
                    skipFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                long[] row = new long[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Long.parseLong(values[i]);
                }
                data.add(row);
            }
        }
        return data.toArray(new long[0][]);
    }

    public static void appendToFile(String filePath, String content) {
        Path path = Paths.get(filePath);
        try {
            Files.write(path, content.getBytes(), java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}