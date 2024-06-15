package org.grapheco.bit;

import com.carrotsearch.sizeof.RamUsageEstimator;
import org.junit.jupiter.api.Test;
import org.roaringbitmap.RoaringBitmap;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EncodingAlgorithmTest {

    @Test
    void isParentOf() {
        ArrayList<long []> data = IO.exampleData();
        HashMap<Long, TreeNode> tree = EncodingAlgorithm.getTree(data);
        TreeNode root = tree.get(1);
        EncodingAlgorithm.chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        EncodingAlgorithm.encode(root);

        /*
            1 -- 4
              -- 8 -- 10
         */
        assertTrue(EncodingAlgorithm.isParentOf(tree.get(1).getCode(), tree.get(4).getCode()));
        assertTrue(EncodingAlgorithm.isParentOf(tree.get(1).getCode(), tree.get(8).getCode()));
        assertTrue(EncodingAlgorithm.isParentOf(tree.get(1).getCode(), tree.get(10).getCode()));
        assertTrue(EncodingAlgorithm.isParentOf(tree.get(8).getCode(), tree.get(10).getCode()));
        assertFalse(EncodingAlgorithm.isParentOf(tree.get(4).getCode(), tree.get(1).getCode()));
        assertFalse(EncodingAlgorithm.isParentOf(tree.get(8).getCode(), tree.get(1).getCode()));
        assertFalse(EncodingAlgorithm.isParentOf(tree.get(10).getCode(), tree.get(1).getCode()));
        assertFalse(EncodingAlgorithm.isParentOf(tree.get(8).getCode(), tree.get(4).getCode()));
        assertFalse(EncodingAlgorithm.isParentOf(tree.get(10).getCode(), tree.get(8).getCode()));
    }

    @Test
    void getChildren() {
        String filepath = "/Users/huchuan/Documents/GitHub/TaxonomyTree/dataset/NCBI.csv";
        ArrayList<long[]> data = IO.loadData(filepath);
        HashMap<Long, TreeNode> tree = EncodingAlgorithm.getTree(data);
        TreeNode root = tree.get(1);
        EncodingAlgorithm.chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        EncodingAlgorithm.encode(root);
        int testNode = 2731342;
        long t0 = System.currentTimeMillis();
        // by code
        System.out.println(EncodingAlgorithm.getChildren(tree.get(testNode), tree.values().stream()).count());
        long t1 = System.currentTimeMillis();
        System.out.println("by code time: "+ (t1 - t0));
        // by travel
        Stack<TreeNode> stack = new Stack<>();
        stack.push(tree.get(testNode));
        ArrayList<TreeNode> nodes = new ArrayList<>();
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            node.getChildren().forEach(c->{
                stack.push(c);
                if (c.getId()!= -1) nodes.add(c);
            });
        }
        System.out.println(nodes.size());
        long t2 = System.currentTimeMillis();
        System.out.println("by travel time: "+ (t2 - t1));
    }


    @Test
    void commonPrefix() {
        BitSet a = new BitSet(); // 1
        a.set(0);
        BitSet b = new BitSet(); // 11
        b.set(0);
        b.set(1);
        assertArrayEquals(a.toByteArray(), EncodingAlgorithm.commonPrefix(a.toByteArray(), b.toByteArray()));
        assertArrayEquals(a.toByteArray(), EncodingAlgorithm.commonPrefix(new byte[][]{a.toByteArray(), b.toByteArray()}));

        a = new BitSet(); // 1000 0100 001
        a.set(0);
        a.set(5);
        a.set(10);
        b = new BitSet(); // 1000 0100 011
        b.set(0);
        b.set(5);
        b.set(9);
        b.set(10);
        BitSet c = (BitSet) a.clone(); // 1000 0100 0
        c.clear(10);
        assertArrayEquals(c.toByteArray(), EncodingAlgorithm.commonPrefix(a.toByteArray(), b.toByteArray()));
        assertArrayEquals(c.toByteArray(), EncodingAlgorithm.commonPrefix(new byte[][]{a.toByteArray(), b.toByteArray()}));

        a = new BitSet(); // 1000 0100 101
        a.set(0);
        a.set(5);
        a.set(8);
        a.set(10);
        b = new BitSet(); // 1000 0100 011
        b.set(0);
        b.set(5);
        b.set(9);
        b.set(10);
        c = (BitSet) a.clone(); // 1000 0100
        c.clear(10);
        c.clear(8);
        assertArrayEquals(c.toByteArray(), EncodingAlgorithm.commonPrefix(a.toByteArray(), b.toByteArray()));
        assertArrayEquals(c.toByteArray(), EncodingAlgorithm.commonPrefix(new byte[][]{a.toByteArray(), b.toByteArray()}));
    }

    @Test
    void bitsetTest(){
        BitSet bitSet = new BitSet();
        bitSet.set(1);
        bitSet.set(0);
        bitSet.set(100);
//        bitSet.stream().forEach(System.out::println);

        RoaringBitmap bitmap = RoaringBitmap.bitmapOf(1,0,100);
//        bitmap.iterator().forEachRemaining(System.out::println);

        bitSet.clear(bitSet.length()-1);
        bitSet.clear(bitSet.length()-1);
        bitSet.clear(bitSet.length()-1);

        RoaringBitmap a = RoaringBitmap.bitmapOf(1,3); // 0101
        RoaringBitmap b = RoaringBitmap.bitmapOf(0,2); // 1010
        a.clone().and(b);
//        System.out.println(a.toString());
        String key = Base64.getEncoder().encodeToString(bitSet.toByteArray());
        System.out.println(key);
    }
}