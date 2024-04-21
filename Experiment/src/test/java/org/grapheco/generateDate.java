package org.grapheco;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.stream.Collectors;

public class generateDate {
    @Test
    void generate(){
//        TestDataGenerator.generateExp2(10000);
        TestDataGenerator.generate4Query2(10);
//        TestDataGenerator.generate4Query3(1);

    }



    @Test
    void encoding(){
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
        TreeNode root = tree.get(1L);
        System.out.println("loaded");
        EncodingAlgorithm.chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        System.out.println("chotomiced");
        EncodingAlgorithm.encode(root);
        System.out.println("encoded");
        String[][] code = tree.values().stream().map(t -> {
            return new String[]{String.valueOf(t.getId()), EncodingAlgorithm.bytesToStr(t.getCode())};
        }).toArray(String[][]::new);
        IO.toCSV("../dataset/codeout.csv", new String[]{"id","code"}, code);
    }
}
