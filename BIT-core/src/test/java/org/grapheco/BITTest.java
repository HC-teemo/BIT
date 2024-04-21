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
        String pathname = "D:\\GithubRepository\\BIT\\dataset\\exp2\\h6_1000.csv";
        String logpath = "C:\\Users\\MSI-NB\\Desktop\\BIT\\log\\exp2\\BIT-list.txt";
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

    @Test
    void list2496434() throws Exception {
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String logpath = "C:\\Users\\MSI-NB\\Desktop\\taxonkit\\exp2\\log\\BIT-list-2496434.txt";

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
            long ancestor = bit.commonAncestor(t[0],t[1]);
            System.out.println(System.nanoTime() - t0 + "," + t[0] + "," + t[1]+","+ancestor);
        }
    }

    @Test
    void lcaList() throws Exception{
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String logfile = "D:\\GithubRepository\\BIT\\dataset\\log-exp3.txt";
        String filepath = "D:\\GithubRepository\\BIT\\dataset\\exp3\\lca2.csv";
        long[][] test = readCSV(new File(filepath));

        long ancestor = 0L;

        appendToFile(logfile,filepath+"\n");

        for (int j =0;j<60;j++){
            long t0 = System.nanoTime();


            for (long[] row : test) {
                ancestor = bit.commonAncestor(row);
            }
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            String content = "round"+j+":"+milliseconds+ "\n";
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
                    // 跳过第一行
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

    @Test
    void isAncestor_() throws Exception{
        BIT bit = BIT.createIndex(IO.loadData("../dataset/NCBI414.csv"), 1L);
        String inputfile = "D:\\GithubRepository\\BIT\\dataset\\exp4\\exp4-3-nochild.csv";
        String logfile = "D:\\GithubRepository\\BIT\\dataset\\log-exp4.txt";

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

    public static void appendToFile(String filePath, String content) {
        Path path = Paths.get(filePath);
        try {
            Files.write(path, content.getBytes(), java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}