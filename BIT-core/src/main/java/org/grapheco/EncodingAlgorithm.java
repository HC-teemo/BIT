package org.grapheco;

import org.roaringbitmap.BitSetUtil;
import org.roaringbitmap.RoaringBitmap;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Stream;

public class EncodingAlgorithm {
    enum ChotomicType {
        Dichotomic, Polychotomic
    }
    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        String filepath = "/Users/huchuan/Documents/GitHub/BIT/dataset/NCBI.csv";
        ArrayList<long []> data = IO.loadData(filepath);
//        ArrayList<int []> data = IO.exampleData();
        HashMap<Long, TreeNode> tree = getTree(data);
        TreeNode root = tree.get(1L);
        long t1 = System.currentTimeMillis();
        System.out.println("Load Data Time: " + (t1 - t0));
        chotomic(root, ChotomicType.Polychotomic);
        long t2 = System.currentTimeMillis();
        System.out.println("Chotomic Time: " + (t2 - t1) + ", Bit Size: " + root.getWeight());
//        show(root, 0);
        encode(root);
        long t3 = System.currentTimeMillis();
        System.out.println("Encoding Time: " + (t3 - t2));
//        map.values().forEach( n -> System.out.println(BitSet.valueOf(n.getCode()).toString()));
        System.out.println();
    }

    public static void chotomic(TreeNode root, ChotomicType type) {
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        TreeNode node;
        while (!stack.empty()) {
            node = stack.peek();
            if (node.getWeight() < 0){
                node.setWeight(0);
                if (node.getChildren().isEmpty())
                    stack.pop();
                else
                    node.getChildren().forEach(stack::push);
            } else { // pop out
                ArrayList<TreeNode> children = node.getChildren();
                int childNumber = children.size();
                if (childNumber == 1 ){
                    node.setWeight(1 + children.get(0).getWeight());
                    stack.pop();
                } else if (childNumber == 2) {
                    node.setWeight(2 + Math.max(children.get(0).getWeight(), children.get(1).getWeight()));
                    stack.pop();
                } else { // combined?
                    children.sort(Comparator.comparingInt(TreeNode::getWeight));
                    TreeNode c1 = children.get(0);
                    TreeNode c2 = children.get(1);
                    TreeNode cn = children.get(childNumber - 1);
                    if (type == ChotomicType.Polychotomic && c2.getWeight() + 2 > cn.getWeight()) {
                        // dont combined!
                        node.setWeight(cn.getWeight() + MathUtils.sp(childNumber));
                        stack.pop();
                    } else {
                        // combined!
                        TreeNode newNode = new TreeNode(node, -1);
                        children.set(1, newNode);
                        children.remove(0);
                        newNode.setWeight(0);
                        newNode.addChildren(c1);
                        newNode.addChildren(c2);
                        c1.setParent(newNode);
                        c2.setParent(newNode);
                        stack.push(newNode);
                    }
                }
            }
        }
    }

    public static void encode(TreeNode root){
        int size = root.getWeight();
        BitSet bitSet = new BitSet(size);
        bitSet.clear();
        root.setWeight(0);
        root.setCode(bitSet.toByteArray());

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        TreeNode node;

        while (!stack.isEmpty()) {
            node = stack.pop();
            ArrayList<TreeNode> children = node.getChildren();
            int freeBitIndex = node.getWeight();
            if (!children.isEmpty()){
                BitSet bits = BitSet.valueOf(node.getCode());
                if (children.size() <= 3) {
                    // set code
                    int nextFreeBitIndex = freeBitIndex + children.size();
                    for (int i = 0; i < children.size(); i++) {
                        TreeNode c = children.get(i);
                        c.setWeight(nextFreeBitIndex); // set nextFreeBitIndex
                        bits.set(freeBitIndex + i); // set code, only in DeChotomic
                        c.setCode(bits.toByteArray()); // gene code
                        bits.clear(freeBitIndex + i); // reset
                        stack.push(c);
                    }
                }else {
                    int needBit = MathUtils.sp(children.size());
                    // set code
                    int nextEndBitIndex = freeBitIndex + needBit;
                    int[][] getCode = MathUtils.getCombinations(needBit);
                    for (int i = 0; i < children.size(); i++) {
                        TreeNode c = children.get(i);
                        int[] code = getCode[i];
                        c.setWeight(nextEndBitIndex); // set nextFreeBitIndex
                        for (int k : code) {
                            bits.set(freeBitIndex + k - 1); // set code
                        }
                        c.setCode(bits.toByteArray()); // gene code
                        bits.clear(freeBitIndex, nextEndBitIndex); // reset
                        stack.push(c);
                    }
                }
            }
        }
    }

    public static HashMap<Long, TreeNode> getTree(List<long[]> d) { return getTree(d, 1);}
    public static HashMap<Long, TreeNode> getTree(List<long[]> d, long root){
        HashMap<Long, TreeNode> map = new HashMap<Long, TreeNode>(d.size() + 1);
        map.put(root, new TreeNode(null, root)); //push root
        long[][] data = d.toArray(new long[][]{});
        // insert Node
        for (long[] n : data) {
            map.put(n[0], new TreeNode(null, n[0]));
        }
        // create parents
        for (long[] n : data) {
            TreeNode c = map.get(n[0]);
            TreeNode p = map.get(n[1]);
            c.setParent(p);
            p.addChildren(c);
        }
        map.get(root).setParent(null);
        map.get(root).getChildren().remove(map.get(root));
        return map;
    }

    public static void show(TreeNode root, int tab){
        for (int i = 0; i < tab; i++) {
            System.out.print("--");
        }
        System.out.print("<" + root.getId() + ", " + root.getWeight() + ">\n");
        for (TreeNode c :
                root.getChildren()) {
            show(c, tab + 1);
        }
    }

    public static boolean isParentOf(byte[] p, byte[] c) {
        BitSet bp = BitSet.valueOf(p);
        BitSet bc = BitSet.valueOf(c);
        if (bp.length()>=bc.length()) return false;
        BitSet bp_ = (BitSet) bp.clone();
        bp.and(bc);
        return bp_.equals(bp);
    }


    private static byte[] mask = new byte[]{
            (byte)255,// 1111 1111
            (byte)254,// 1111 1110
            (byte)252,// 1111 1100
            (byte)248,// 1111 1000
            (byte)240,// 1111 0000
            (byte)224,// 1110 0000
            (byte)192,// 1100 0000
            (byte)64, // 1000 0000
    };
    public static byte[] commonPrefix(byte[] a, byte[] b) {
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            if (a[i]!=b[i]) {
                byte[] vs = Arrays.copyOf(a, i+1);
                byte diffA = a[i];
                byte diffB = b[i];
                byte maskA = (byte) ~((~ diffA) | mask[1]);
                byte maskB = (byte) ~((~ diffB) | mask[1]);
                if (maskA!=maskB) return Arrays.copyOf(vs, i);
                for (int j = 2; j < 7 && maskA==maskB; j++) {
                    vs[i] = maskA;
                    maskA = (byte) ~((~ diffA) | mask[j]);
                    maskB = (byte) ~((~ diffB) | mask[j]);
                }
                return vs[i]==0?Arrays.copyOf(vs, i):vs;
            }
        }
        return a.length >= b.length? b : a;
    }

    public static Stream<TreeNode> getChildren(TreeNode p, Stream<TreeNode> nodes){
        return nodes.filter(n->isParentOf(p.getCode(),n.getCode()));
    }


    public static void out(TreeNode[] nodes) throws IOException {
        ICsvListWriter listWriter = new CsvListWriter(new FileWriter("target/id-code.csv"),
                CsvPreference.STANDARD_PREFERENCE);
        listWriter.writeHeader("id","parent","vector");
        int count = 0;

            try {
                for (TreeNode treeNode : nodes) {
                    listWriter.write(Long.toString(treeNode.getId()), "",
    //                        Integer.toString(treeNode.getParent()==null?0:treeNode.getParent().getId()),
                            bytesToStr(treeNode.getCode()));
                    count++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                listWriter.close();
            }

        System.out.println(count);
    }

    public static String bytesToStr(byte[] bytes) {
        BitSet b = BitSet.valueOf(bytes);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length(); i++) {
            sb.append(b.get(i)?"1":"0");
        }
        return sb.toString();
    }

}
