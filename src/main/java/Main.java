import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    enum ChotomicType {
        Dichotomic, Polychotomic
    }
    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        String filepath = "/Users/huchuan/Documents/GitHub/TaxonomyTree/dataset/nodes.csv";
        ArrayList<int []> data = IO.loadData(filepath);
//        ArrayList<int []> data = IO.exampleData();
        HashMap<Integer, TreeNode> tree = getTree(data);
        TreeNode root = tree.get(1);
        long t1 = System.currentTimeMillis();
        System.out.println("Load Data Time: " + (t1 - t0));
        Chotomic(root, ChotomicType.Polychotomic);
        long t2 = System.currentTimeMillis();
        System.out.println("Chotomic Time: " + (t2 - t1) + ", Bit Size: " + root.getWeight());
//        show(root, 0);
        encode(root);
        long t3 = System.currentTimeMillis();
        System.out.println("Encoding Time: " + (t3 - t2));
//        map.values().forEach( n -> System.out.println(BitSet.valueOf(n.getCode()).toString()));
        System.out.println();
    }

    public static void Chotomic(TreeNode root, ChotomicType type) {
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
                        node.setWeight(cn.getWeight() + MathUtils.cHat(childNumber));
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
                    int needBit = MathUtils.cHat(children.size());
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

    public static HashMap<Integer, TreeNode> getTree(ArrayList<int[]> data){
        HashMap<Integer, TreeNode> map = new HashMap<Integer, TreeNode>(data.size() + 1);
        map.put(1, new TreeNode(null, 1)); //push root
        // insert Node
        for (int[] n : data) {
            map.put(n[0], new TreeNode(null, n[0]));
        }
        // create parents
        for (int[] n : data) {
            TreeNode c = map.get(n[0]);
            TreeNode p = map.get(n[1]);
            c.setParent(p);
            p.addChildren(c);
        }
        map.get(1).setParent(null);
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

    public static boolean isParentOf(TreeNode p, TreeNode c) {
        BitSet bp = BitSet.valueOf(p.getCode());
        BitSet bc = BitSet.valueOf(c.getCode());
        int ps = bp.size();
        int ps2 = bp.length();
        int cs = bc.size();
        int cs2 = bc.length();
        if (bp.length()>=bc.length()) return false;
        BitSet bp_ = (BitSet) bp.clone();
        bp.and(bc);
        return bp_.equals(bp);
    }

    public static Stream<TreeNode> getChildren(TreeNode p, Stream<TreeNode> nodes){
        return nodes.filter(n->isParentOf(p,n));
    }

}
