import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void verify() {
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
        String filepath = "/Users/huchuan/Documents/GitHub/TaxonomyTree/dataset/nodes.csv";
        ArrayList<int []> data = IO.loadData(filepath);
        HashMap<Integer, TreeNode> tree = Main.getTree(data);
        TreeNode root = tree.get(1);
        Main.Chotomic(root, Main.ChotomicType.Polychotomic);
        Main.encode(root);
        long t0 = System.currentTimeMillis();
        for(int[] test : testArr){
            System.out.println(Main.isParentOf(tree.get(test[0]), tree.get(test[1])));
        }
        long t1 = System.currentTimeMillis();
        System.out.println("verify time: "+ (t1 - t0));
    }

    @Test
    void isParentOf() {
        ArrayList<int []> data = IO.exampleData();
        HashMap<Integer, TreeNode> tree = Main.getTree(data);
        TreeNode root = tree.get(1);
        Main.Chotomic(root, Main.ChotomicType.Polychotomic);
        Main.encode(root);

        /*
            1 -- 4
              -- 8 -- 10
         */
        assertTrue(Main.isParentOf(tree.get(1), tree.get(4)));
        assertTrue(Main.isParentOf(tree.get(1), tree.get(8)));
        assertTrue(Main.isParentOf(tree.get(1), tree.get(10)));
        assertTrue(Main.isParentOf(tree.get(8), tree.get(10)));
        assertFalse(Main.isParentOf(tree.get(4), tree.get(1)));
        assertFalse(Main.isParentOf(tree.get(8), tree.get(1)));
        assertFalse(Main.isParentOf(tree.get(10), tree.get(1)));
        assertFalse(Main.isParentOf(tree.get(8), tree.get(4)));
        assertFalse(Main.isParentOf(tree.get(10), tree.get(8)));
    }

    @Test
    void chotomic() {
    }

    @Test
    void encode() {
    }

    @Test
    void show() {
    }

    @Test
    void getChildren() {
        String filepath = "/Users/huchuan/Documents/GitHub/TaxonomyTree/dataset/nodes.csv";
        ArrayList<int []> data = IO.loadData(filepath);
        HashMap<Integer, TreeNode> tree = Main.getTree(data);
        TreeNode root = tree.get(1);
        Main.Chotomic(root, Main.ChotomicType.Polychotomic);
        Main.encode(root);
        int testNode = 2731342;
        long t0 = System.currentTimeMillis();
        // by code
        System.out.println(Main.getChildren(tree.get(testNode), tree.values().stream()).count());
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
}