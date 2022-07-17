import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        String filepath = "H:\\TaxonomyTree\\dataset\\node_h4.csv";
        ArrayList<int []> data = loadData(filepath);
//        ArrayList<int []> data = exampleData();
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
        TreeNode root = map.get(1);
        root.setParent(null);
        polyChotomic(root);
        System.out.println();
    }

    public static void polyChotomic(TreeNode root){
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        TreeNode node;
//        show(root, 0);
        while (!stack.empty()) {
            node = stack.peek();
            // leaf
//            if (node.getChildren().isEmpty()) {
//                node.setWeight(0);
//                stack.pop();
//            } else {
                // put in
                if (node.getWeight() < 0){
                    node.setWeight(0);
                    if (node.getChildren().isEmpty())
                        stack.pop();
                    else
                        node.getChildren().forEach(stack::push);
                } else { // put out
                    ArrayList<TreeNode> children = node.getChildren();
                    int childNumber = children.size();
                    if (childNumber == 1 ){
                        node.setWeight(1 + children.get(0).getWeight());
                        stack.pop();
                    } else if (childNumber == 2) {
                        node.setWeight(2 + Math.max(children.get(0).getWeight(), children.get(1).getWeight()));
                        stack.pop();
                    } else { // combined
                        children.sort((a,b) -> a.getWeight() - b.getWeight());
                        TreeNode c1 = children.get(0);
                        TreeNode c2 = children.get(1);
                        TreeNode newNode = new TreeNode(node, -1);
                        children.set(1, newNode);
                        children.remove(0);
                        // TODO combined
                        newNode.setWeight(0);
                        newNode.addChildren(c1);
                        newNode.addChildren(c2);
                        c1.setParent(newNode);
                        c2.setParent(newNode);
                        stack.push(newNode);
                    }
                }
//            System.out.println("__________________________");
//                show(root, 0);

//            }
        }
    }

    public static void encode(TreeNode tree){
        TreeNode root = tree;
        int codeLen = root.getWeight();

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

    public static ArrayList<int[]> loadData (String filepath){
        File csv = new File(filepath);
//        csv.setReadable(true);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        ArrayList<int[]> data = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) // 读取到的内容给line变量
            {
                everyLine = line;
                String[] split = everyLine.split(",");
//                System.out.println(everyLine);
                data.add(new int[]{Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim())});
            }
            System.out.println("relationships: " + data.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static ArrayList<int[]> exampleData (){
        ArrayList<int []> example = new ArrayList<int[]>(11);
//        example.add(new int[]{1,1});
        example.add(new int[]{2,1});
        example.add(new int[]{3,1});
        example.add(new int[]{4,1});
        example.add(new int[]{5,1});
        example.add(new int[]{6,1});
        example.add(new int[]{7,1});
        example.add(new int[]{8,1});
        example.add(new int[]{9,8});
        example.add(new int[]{10,8});
        example.add(new int[]{11,8});
        return example;
    }
}
