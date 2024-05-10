package org.grapheco;

import com.carrotsearch.sizeof.RamUsageEstimator;
import org.junit.jupiter.api.Test;
import org.roaringbitmap.RoaringBitmap;

import java.util.*;

import static org.grapheco.EncodingAlgorithm.*;
import static org.grapheco.IO.writeLog;
import static org.junit.jupiter.api.Assertions.*;

class EncodingAlgorithmTest {
    int[][] testArr = new int[][]{
            new int[]{1514144, 1925793},
            new int[]{1, 2493305},
            new int[]{1607673, 1987063},
            new int[]{1, 2751337},
            new int[]{2289145, 1446832},
            new int[]{991150, 2822404},
            new int[]{1925161, 155548},
            new int[]{2686727, 572246},
            new int[]{866809, 434253},
            new int[]{1527986, 2541676}
    };

    @Test
    void exp1(){
        String log = "../log/BIT-exp1.txt";
        String filepath = "../dataset/NCBI414.csv";
        String filepath1 = "../dataset/exp1/h3.csv";
        String filepath2 = "../dataset/exp1/h4.csv";
        String filepath3 = "../dataset/exp1/h5.csv";
        String filepath4 = "../dataset/exp1/h6.csv";
        String filepath5 = "../dataset/exp1/h10.csv";
        String filepath6 = "../dataset/exp1/h12.csv";
        String filepath7 = "../dataset/exp1/h14.csv";
        String filepath8 = "../dataset/exp1/h20.csv";

        String[] All_filepath = {filepath,filepath1, filepath2, filepath3, filepath4, filepath5, filepath6, filepath7, filepath8};

        for (String file:All_filepath
        ) {
            writeLog(log,file);
            for (int i = 0; i < 1; i++) {
                long t1 = System.nanoTime();
                ArrayList<long []> data = IO.loadData(file);
                //        ArrayList<int []> data = IO.exampleData();
                HashMap<Long, TreeNode> tree = getTree(data);
                TreeNode root = tree.get(1L);
                double milliseconds1 = (System.nanoTime() - t1) / 1e6;
                writeLog(log,"Time1:" + milliseconds1);

                long t2 = System.nanoTime();
                chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
                double milliseconds2 = (System.nanoTime() - t2) / 1e6;

                writeLog(log,"Time2:" + milliseconds2 + ",Bit2:" + root.getWeight());

                long t3 = System.nanoTime();
                //        show(root, 0);
                encode(root);
                double milliseconds3 = (System.nanoTime() - t3) / 1e6;

                writeLog(log,"Time3:" + milliseconds3);
            }
        }
    }


    @Test
    void verify() {
        String filepath = "../dataset/olddata/NCBI.csv";
        ArrayList<long []> data = IO.loadData(filepath);
        HashMap<Long, TreeNode> tree = getTree(data);
        TreeNode root = tree.get(1);
        chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        EncodingAlgorithm.encode(root);
        long t0 = System.currentTimeMillis();
        for(int[] test : testArr){
            System.out.println(EncodingAlgorithm.isParentOf(tree.get(test[0]).getCode(), tree.get(test[1]).getCode()));
        }
        long t1 = System.currentTimeMillis();
        System.out.println("verify time: "+ (t1 - t0));
    }


    @Test
    void isParentOf() {
        ArrayList<long []> data = IO.exampleData();
        HashMap<Long, TreeNode> tree = getTree(data);
        TreeNode root = tree.get(1);
        chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
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
        HashMap<Long, TreeNode> tree = getTree(data);
        TreeNode root = tree.get(1);
        chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
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
    void findChildrenExpr() throws Exception {
        String filepath = "/Users/huchuan/Documents/GitHub/TaxonomyTree/dataset/NCBI.csv";
        ArrayList<long []> data = IO.loadData(filepath);
        HashMap<Long, TreeNode> tree = getTree(data);
        TreeNode root = tree.get(1);
        chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        int size = root.getWeight();
        EncodingAlgorithm.encode(root);

        long ta = System.currentTimeMillis();
        TreeNode[] nodes = tree.values().toArray(new TreeNode[0]);
        BIT bit = new BIT(size);

        for (int i = 0; i < nodes.length; i++) {
            bit.insert(i, BitSet.valueOf(nodes[i].getCode()));
        }
        System.out.println("BIT Time: " + (System.currentTimeMillis() - ta));

        int s = 10000;
        long[] tex_ids = new long[s];
        Random random = new Random();
        for (int i = 0; i < s; i++) {
            tex_ids[i] = nodes[random.nextInt(nodes.length - 1)].getId();
        }
        HashMap<Long, TreeNode> originTree = getTree(data);

        //________TEST_________
        // Method 1
        long t0 = System.currentTimeMillis();
        for(long tex_id: tex_ids) {
            TreeNode originNode = originTree.get(tex_id);
            Stack<TreeNode> stack = new Stack<>();
            ArrayList<TreeNode> result1 = new ArrayList<>();

            result1.add(originNode);
            stack.push(originNode);

            while (!stack.isEmpty()) {
                TreeNode t = stack.pop();
                result1.addAll(t.getChildren());
                stack.addAll(t.getChildren());
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println("m1 time: " + (t1 - t0));

        // Method 2
//        ArrayList<TreeNode> result2 = new ArrayList<>();
//        tree.values().forEach(treeNode -> {
//            if (EncodingAlgorithm.isParentOf(thenode.getCode(), treeNode.getCode()))
//                result2.add(treeNode);
//        });
        long t2 = System.currentTimeMillis();
//        System.out.println("m2: " + result2.size() + ", time: " + (t2 - t1));

        // Method 3
        for (long tex_id : tex_ids) {
            TreeNode theNode = tree.get(tex_id);
            RoaringBitmap result = bit.filter(BitSet.valueOf(theNode.getCode()));
        }
        long t3 = System.currentTimeMillis();
        System.out.println("m3 time: " + (t3 - t2));

    }

    @Test
    void isChildrenExpr() throws Exception {
        String filepath = "/Users/huchuan/Documents/GitHub/TaxonomyTree/dataset/NCBI.csv";
        ArrayList<long []> data = IO.loadData(filepath);
        HashMap<Long, TreeNode> tree = getTree(data);
        TreeNode root = tree.get(1);
        chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        int size = root.getWeight();
        EncodingAlgorithm.encode(root);

        long ta = System.currentTimeMillis();
        TreeNode[] nodes = tree.values().toArray(new TreeNode[0]);
        BIT bit = new BIT(size);

        for (int i = 0; i < nodes.length; i++) {
            bit.insert(i, BitSet.valueOf(nodes[i].getCode()));
        }
        System.out.println("BIT Time: " + (System.currentTimeMillis() - ta));
        HashMap<Long, TreeNode> originTree = getTree(data);
        int s = 100000;
        long[][] tests = new long[s][2];
        Random random = new Random();

        for (int i = 0; i < s; i++) {
            tests[i] = new long[]{
                    nodes[random.nextInt(nodes.length - 1)].getId(),
                    nodes[random.nextInt(nodes.length - 1)].getId()};
        }
//        ______native
        long t0 = System.currentTimeMillis();
        for(long[] pc: tests) {
            TreeNode p = originTree.get(pc[0]);
            TreeNode c = originTree.get(pc[1]);
            while (p!=c && c.getParent()!=null) c = c.getParent();
            boolean res = p==c;
//            System.out.println(p==c);
        }
        long t1 = System.currentTimeMillis();
        System.out.println("m1 time: " + (t1 - t0));
        // ___BIT
        for(long[] pc: tests) {
            byte[] p = tree.get(pc[0]).getCode();
            byte[] c = tree.get(pc[1]).getCode();
            EncodingAlgorithm.isParentOf(p,c);
//            System.out.println(EncodingAlgorithm.isParentOf(p, c));
        }
        long t2 = System.currentTimeMillis();
        System.out.println("m1 time: " + (t2 - t1));
    }


    @Test
    void sizeExp() throws Exception {
        String filepath = "/Users/huchuan/Documents/GitHub/TaxonomyTree/dataset/NCBI.csv";
        ArrayList<long []> data = IO.loadData(filepath);
        HashMap<Long, TreeNode> tree = getTree(data);
        TreeNode root = tree.get(1);
        chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        int size = root.getWeight();
        EncodingAlgorithm.encode(root);

        TreeNode[] nodes = tree.values().toArray(new TreeNode[0]);
        BIT bit = new BIT(size);
        for (int i = 0; i < nodes.length; i++) {
            bit.insert(i, BitSet.valueOf(nodes[i].getCode()));
        }
        long[] ids = Arrays.stream(nodes).mapToLong(TreeNode::getId).toArray();
        HashMap<Long, TreeNode> originTree = getTree(data);
        System.out.println("tree Size: " + RamUsageEstimator.sizeOf(originTree) / RamUsageEstimator.ONE_MB + "MB");
        System.out.println("tree value Size: " + RamUsageEstimator.sizeOf(originTree.values()) / RamUsageEstimator.ONE_MB + "MB");
        System.out.println("tree root Size: " + RamUsageEstimator.sizeOf(originTree.get(1)) / RamUsageEstimator.ONE_MB + "MB");
        System.out.println("ids Size: " + RamUsageEstimator.sizeOf(ids) / RamUsageEstimator.ONE_MB + "MB");
        System.out.println("BIT Size: " + RamUsageEstimator.sizeOf(bit) / RamUsageEstimator.ONE_MB + "MB");
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