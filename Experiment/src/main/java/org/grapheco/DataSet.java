package org.grapheco;

import java.io.File;
import java.util.HashMap;

public class DataSet {
    public static final String NCBI = "../dataset/NCBI_bak.csv";

    public static final String NCBI_h3 = "../dataset/NCBI_h3.csv";
    public static final String NCBI_h4 = "../dataset/NCBI_h4.csv";

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
