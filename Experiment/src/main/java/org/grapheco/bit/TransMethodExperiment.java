package org.grapheco.bit;
import java.io.File;
import java.util.ArrayList;

public class TransMethodExperiment {

    public static void main(String[] args) throws Exception {
        list(DataSet.list3);
    }

    static void list(File dataset) throws Exception {
        TransMethod transMethod = TransMethod.createIndex(IO.loadData(DataSet.NCBI), 1L);

        long[][] test = IO.read(dataset).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);

        int TIMEs = 10;
        int ALLTIME = 0;
        for (int i = 0; i < TIMEs; i++) {
            long t0 = System.nanoTime();
            for (long[] t : test) {
                int size = transMethod.list(t[0]).size();
//                System.out.println(size);
            }
            double milliseconds = (System.nanoTime() - t0) / 1e6;
            ALLTIME+= (int) milliseconds;
            System.out.println("round" + i + ":" + milliseconds);
        }
        System.out.println("avg: "+ALLTIME/TIMEs);
    }

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

