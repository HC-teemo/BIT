package org.grapheco.bit;

import java.util.*;

public class TestDataGenerator {


    public static void generate4Query1(int size){
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
//        HashMap<Long, TreeNode> h3 = DataSet.treeData(DataSet.NCBI_h3);
        TreeNode[] data = tree.values().toArray(new TreeNode[]{});
//        TreeNode[] parents = sample(h3.values().toArray(new TreeNode[0]), size);
        TreeNode[] parents = sample(data, size, 10000);
        TreeNode[] children = sample(data, size);
        String[][] sampleText = new String[size][3];
        for (int i = 0; i < size/2; i++) { // random
            TreeNode p = tree.get(parents[i].getId());
            TreeNode c = children[i];
            sampleText[i] = new String[]{
                    String.valueOf(p.getId()),
                    String.valueOf(c.getId()),
                    String.valueOf(check(p, c))
            };
        }
        Random random = new Random();
        for (int i = size/2; i < size; i++) {
            TreeNode p = tree.get(parents[i].getId());
            TreeNode c = p;
            while (!c.getChildren().isEmpty()){
                c = c.getChildren().get(random.nextInt(c.getChildren().size()));
            }
            sampleText[i] = new String[]{
                    String.valueOf(p.getId()),
                    String.valueOf(c.getId()),
                    String.valueOf(check(p, c))
            };
        }
        IO.toCSV(DataSet.query1File.getAbsolutePath(), new String[]{"parents", "children", "result"}, sampleText);
    }

    public static void generate4Query2(int size){
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
        TreeNode[] data = tree.values().toArray(new TreeNode[]{});
        TreeNode[] first = sample(data, size);
        TreeNode[] second = sample(data, size);
        String[][] sampleText = new String[size][3];
        for (int i = 0; i < size; i++) {
            TreeNode a = first[i];
            TreeNode b = second[i];
            sampleText[i] = new String[]{
                    String.valueOf(a.getId()),
                    String.valueOf(b.getId()),
                    String.valueOf(commonAnc(a, b))
            };
        }
        IO.toCSV(DataSet.query2File.getAbsolutePath(), new String[]{"nodeA", "nodeB", "result"}, sampleText);
    }

    public static void generateExp2(int size){
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI414_h6);

        TreeNode[] data = tree.values().toArray(new TreeNode[]{});
        TreeNode[] sample = sample(data, size, 100); // sample in h4
        String[][] sampleText = new String[size][2];

        for (int i = 0; i < size; i++) {
//            TreeNode n = tree.get(sample[i].getId()); // get node in full data
            TreeNode n = sample[i];
            sampleText[i] = new String[]{
                    String.valueOf(n.getId()),
                    String.valueOf(desNum(n))
            };
        }
        IO.toCSV(DataSet.exp2File10000.getAbsolutePath(), new String[]{"node", "descendantsNum"}, sampleText);
    }

    private static TreeNode[] sample(TreeNode[] nodes, int num){return sample(nodes, num, 0);}
    private static TreeNode[] sample(TreeNode[] nodes, int num, int minChildren){
        Random random = new Random();
        int size = nodes.length;
        TreeNode[] sample = new TreeNode[num];
        for (int i = 0; i < num;) {
            sample[i] =  nodes[random.nextInt(size)];
            if (minChildren==0 || sample[i].getChildren().size() > minChildren) i++;
        }
        return sample;
    }

    private static boolean check(TreeNode parent, TreeNode child){
        TreeNode temp = child.getParent();
        while (temp!=null && temp!= parent) temp = temp.getParent();
        return temp!=null;
    }

    private static long commonAnc(TreeNode treeNodeA, TreeNode treeNodeB){
        HashSet<TreeNode> ancsOfA = new HashSet<>();
        TreeNode temp = treeNodeA.getParent();
        while (temp!=null){
            ancsOfA.add(temp);
            temp = temp.getParent();
        }
        temp = treeNodeB;
        while (!ancsOfA.contains(temp)) temp = temp.getParent();
        return temp.getId();
    }

    private static int desNum(TreeNode node) {
        Stack<TreeNode> stack = new Stack<>();
        int count = 0;
        stack.push(node);
        while (!stack.isEmpty()) {
            TreeNode t = stack.pop();
            count += t.getChildren().size();
            stack.addAll(t.getChildren());
        }
        return count;
    }
}
