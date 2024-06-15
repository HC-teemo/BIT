package org.grapheco.bit;

import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class EncodingAlgorithm {
    enum ChotomicType {
        Dichotomic, Polychotomic
    }
    public static void main(String[] args) {
        String log = "/Users/huchuan/Documents/GitHub/BIT/dataset/log-exp1.txt";
        String filepath = "/Users/huchuan/Documents/GitHub/BIT/dataset/NCBI414.csv";
        String filepath1 = "/Users/huchuan/Documents/GitHub/BIT/dataset/exp1/h3.csv";
        String filepath2 = "/Users/huchuan/Documents/GitHub/BIT/dataset/exp1/h4.csv";
        String filepath3 = "/Users/huchuan/Documents/GitHub/BIT/dataset/exp1/h5.csv";
        String filepath4 = "/Users/huchuan/Documents/GitHub/BIT/dataset/exp1/h6.csv";
        String filepath5 = "/Users/huchuan/Documents/GitHub/BIT/dataset/exp1/h10.csv";
        String filepath6 = "/Users/huchuan/Documents/GitHub/BIT/dataset/exp1/h12.csv";
        String filepath7 = "/Users/huchuan/Documents/GitHub/BIT/dataset/exp1/h14.csv";
        String filepath8 = "/Users/huchuan/Documents/GitHub/BIT/dataset/exp1/h20.csv";

//        String[] All_filepath = {
////                filepath,
//                filepath1, filepath2,
//                filepath3,
//                filepath4,
//                filepath5,
//                filepath6, filepath7, filepath8};
        String[] All_filepath = {
                filepath4,filepath3,filepath4,filepath3,filepath4,filepath3,filepath4,filepath3
        };

        for (String file:All_filepath
             ) {
            writeLog(log,file);
            for (int i = 0; i < 1; i++) {
//                long t1 = System.nanoTime();
                ArrayList<long []> data = IO.loadData(file);
                //        ArrayList<int []> data = IO.exampleData();
                HashMap<Long, TreeNode> tree = getTree(data);
                TreeNode root = tree.get(1L);
//                double milliseconds1 = (System.nanoTime() - t1) / 1e6;
//                System.out.println();
//                writeLog(log,"Time1:" + milliseconds1);

                long t2 = System.currentTimeMillis();
                chotomic(root, ChotomicType.Polychotomic);
//                double milliseconds2 = (System.nanoTime() - t2) / 1e6;

//                writeLog(log,"Time2:" + milliseconds2 + ",Bit2:" + root.getWeight());

//                long t3 = System.nanoTime();
                //        show(root, 0);
                encode(root);
//                double milliseconds3 = (System.nanoTime() - t3) / 1e6;
                System.out.println(System.currentTimeMillis() - t2);
//                writeLog(log,"Time3:" + milliseconds3);
            }
        }


//        map.values().forEach( n -> System.out.println(BitSet.valueOf(n.getCode()).toString()));
    }

    public static void writeLog(String filename,String content){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(content);
            writer.newLine();
            System.out.println("Content appended to file successfully.");
        } catch (IOException e) {
            System.err.println("Error appending content to file: " + e.getMessage());
        }
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


    public static byte[] commonPrefix(byte[][] arrays) {
        if (arrays == null || arrays.length == 0) {
            return new byte[0];
        }

        int minLength = Integer.MAX_VALUE;
        for (byte[] array : arrays) {
            if (array.length < minLength) {
                minLength = array.length;
            }
        }

        int prefixLengthInBytes = 0;
        boolean prefixMismatchFound = false;

        outerLoop:
        for (int i = 0; i < minLength; i++) {
            byte currentByte = arrays[0][i];
            for (int j = 1; j < arrays.length; j++) {
                if (arrays[j][i] != currentByte) {
                    prefixMismatchFound = true;
                    break outerLoop;
                }
            }
            prefixLengthInBytes++;
        }

        if (prefixMismatchFound && prefixLengthInBytes < minLength) {
            // Find the exact bit where mismatch occurs in the last matched byte
            byte lastMatchedByte = arrays[0][prefixLengthInBytes];
            int bitPosition = 0; // Start from the most significant bit
            outerLoop:
            while (bitPosition <= 7) {
                int mask = 1 << bitPosition;
                for (int j = 1; j < arrays.length; j++) {
                    if ((arrays[j][prefixLengthInBytes] & mask) != (lastMatchedByte & mask)) {
                        break outerLoop;
                    }
                }
                bitPosition++;
            }
            lastMatchedByte = (byte) ~((~ lastMatchedByte) | mask[bitPosition]);
            if (lastMatchedByte!=0) {
                arrays[0][prefixLengthInBytes] = lastMatchedByte;
                prefixLengthInBytes++;
            }
        }

        byte[] prefix = new byte[prefixLengthInBytes];
        System.arraycopy(arrays[0], 0, prefix, 0, prefixLengthInBytes);
        return prefix;
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
