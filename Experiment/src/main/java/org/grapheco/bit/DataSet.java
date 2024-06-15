package org.grapheco.bit;

import java.io.File;
import java.util.HashMap;

public class DataSet {
    public static final String NCBI = "../dataset/NCBI414.csv";
    public static final String NCBI414_h3 = "../dataset/exp1/h3.csv";
    public static final String NCBI414_h4 = "../dataset/exp1/h4.csv";
    public static final String NCBI414_h5 = "../dataset/exp1/h5.csv";
    public static final String NCBI414_h6 = "../dataset/exp1/h6.csv";
    public static final String NCBI414_h10 = "../dataset/exp1/h10.csv";
    public static final String NCBI414_h12 = "../dataset/exp1/h12.csv";
    public static final String NCBI414_h14 = "../dataset/exp1/h14.csv";
    public static final String NCBI414_h20 = "../dataset/exp1/h20.csv";

    public static final String NCBI_h3 = "../dataset/NCBI_h3.csv";
    public static final String NCBI_h4 = "../dataset/NCBI_h4.csv";
    public static final String NCBI_h5 = "../dataset/NCBI_h5.csv";
    public static final String NCBI_h6 = "../dataset/NCBI_h6.csv";
    public static final String NCBI_h10 = "../dataset/NCBI_h10.csv";

    public static final File exp2File10 = new File("../dataset/exp2/h6_10.csv");
    public static final File exp2File100 = new File("../dataset/exp2/h6_100.csv");
    public static final File exp2File1000 = new File("../dataset/exp2/h6_1000.csv");
    public static final File exp2File10000 = new File("../dataset/exp2/h6_10000.csv");

    public static final File query1File = new File("../dataset/test/query1.csv");
    public static final File query2File = new File("../dataset/test/query2.csv");
    public static final File query3File = new File("../dataset/test/query3.csv");

    public static HashMap<Long, TreeNode> treeData(String path) {
        return EncodingAlgorithm.getTree(IO.loadData(path));
    }

    public static long[][] query1(){
        return IO.read(query1File).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1]),
                    Boolean.parseBoolean(line[2]) ? 1 : 0
            };
        }).toArray(long[][]::new);
    }

    public static long[][] query2(){
        return IO.read(query2File).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1]),
                    Long.parseLong(line[2])
            };
        }).toArray(long[][] :: new);
    }

    public static long[][] query3(){
        return IO.read(query3File).stream().map(line -> {
            return new long[]{
                    Long.parseLong(line[0]),
                    Long.parseLong(line[1])
            };
        }).toArray(long[][] :: new);
    }
}
