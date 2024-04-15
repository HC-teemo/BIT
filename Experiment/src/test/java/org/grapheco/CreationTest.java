package org.grapheco;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CreationTest {
    @Test
    void creationTest(){
        HashMap<Long, TreeNode> data = DataSet.treeData(DataSet.NCBI_h3);
        TreeNode root = data.get(1L);
        long t0 = System.currentTimeMillis();
        EncodingAlgorithm.chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        long t1 = System.currentTimeMillis();
        EncodingAlgorithm.encode(root);
        long t2 = System.currentTimeMillis();
        System.out.println("Restructuring time: " + (t1 - t0));
        System.out.println("Encoding time: " + (t2 - t1));
    }
}
