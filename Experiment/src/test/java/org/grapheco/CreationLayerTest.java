package org.grapheco;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;


import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author cai584770
 * @date 2024/4/16 21:57
 * @Version
 */
public class CreationLayerTest {
    private static int currentIndex = 0;
    private static String[][] result;
    private static int index = 0;

    @Test
    void getHighLayer(){
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
        TreeNode root = tree.get(1L);

        ArrayList<TreeNode> nodesWithinThreeLevels = getNodesWithinLevels(root, 20);

        saveNodeIdsAndParentIds(nodesWithinThreeLevels);
        StringBuilder stringBuffer = new StringBuilder();

        for (String[] strings : result) {
            stringBuffer.append(strings[0]).append(",").append(strings[1]).append("\n");
        }
        String filepath = "D:\\GithubRepository\\BIT\\dataset\\h20.csv";
        File file = new File(filepath);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(stringBuffer.toString());
            writer.close();
            System.out.println("Result has been written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void getLayers() {
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
        TreeNode root = tree.get(1L);
        ArrayList<TreeNode> nodesAtLevelSix = getNodesAtLevel(root, 10);

        StringBuilder stringBuffer = new StringBuilder();
        for (TreeNode node : nodesAtLevelSix) {
            stringBuffer.append(String.valueOf(node.getId())).append(",").append(String.valueOf(node.getParent().getId())).append("\n");
        }

        String filepath = "D:\\GithubRepository\\BIT\\dataset\\exp2\\layer10.csv";
        File file = new File(filepath);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(stringBuffer.toString());
            writer.close();
            System.out.println("Result has been written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void exp3data_4_32() {
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
        TreeNode root = tree.get(1L);
        ArrayList<TreeNode> tnList = new ArrayList<>();


        for (Map.Entry<Long, TreeNode> entry : tree.entrySet()) {
            Long key = entry.getKey();
            TreeNode value = entry.getValue();

            if (value.getChildren().size() == 0){
                tnList.add(value);
            }
        }

        int sizetn = tnList.size();
        TreeNode[] tn = new TreeNode[sizetn];
        for (int i = 0; i < sizetn; i++) {
            tn[i] = tnList.get(i);
        }

//        ArrayList<TreeNode> nodesAtLevelSix = getNodesAtLevel(root, 4);
//
//        int size = nodesAtLevelSix.size();
//        TreeNode[] tn = new TreeNode[size];
//        for (int i = 0; i < size; i++) {
//            tn[i] = nodesAtLevelSix.get(i);
//        }

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("node\n");
        for (int i = 0; i < 10; i++) {
            TreeNode[] sampling = sample(tn,32);
//            TreeNode[] layer4 = sample(tn,32);
            for (TreeNode t: sampling
            ) {
                stringBuffer.append(String.valueOf(t.getId())).append(",");
            }
            stringBuffer.append("\n");
            String filepath = "D:\\GithubRepository\\BIT\\dataset\\exp3-addition\\exp3-addition2.csv";
            File file = new File(filepath);
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(stringBuffer.toString());
                writer.close();
                System.out.println("Result has been written to file successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    void exp3data() {
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
        TreeNode root = tree.get(1L);
        ArrayList<TreeNode> nodesAtLevelSix = getNodesAtLevel(root, 10);

        int size = nodesAtLevelSix.size();
        TreeNode[] tn = new TreeNode[size];
        for (int i = 0; i < size; i++) {
            tn[i] = nodesAtLevelSix.get(i);
        }

        TreeNode[] layer10 = sample(tn,3,1000);
        ArrayList<TreeNode> allDescendants = new ArrayList<>();
        for (TreeNode tr : layer10) {
            getAllDescendants(tr, allDescendants);
        }

        int size1 = allDescendants.size();
        TreeNode[] tn1 = new TreeNode[size1];
        for (int i = 0; i < size1; i++) {
            tn1[i] = allDescendants.get(i);
        }

        TreeNode[] layer10Descendants = sample(tn1,60,0);

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("node1,node2\n");
        for (int i = 0; i < layer10Descendants.length; i++) {
            String layer10Descendantsid = String.valueOf(layer10Descendants[i].getId());
            stringBuffer.append(layer10Descendantsid).append(",").append(String.valueOf(layer10Descendants[i].getParent().getId())).append("\n");
        }

        String filepath = "D:\\GithubRepository\\BIT\\dataset\\exp3\\exp3.csv";
        File file = new File(filepath);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(stringBuffer.toString());
            writer.close();
            System.out.println("Result has been written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void exp4data_(){
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
        TreeNode root = tree.get(1L);
        ArrayList<TreeNode> layer3 = getNodesAtLevel(root, 3);
        ArrayList<TreeNode> tnList = new ArrayList<>();

        for (Map.Entry<Long, TreeNode> entry : tree.entrySet()) {
            Long key = entry.getKey();
            TreeNode value = entry.getValue();

            if (value.getChildren().size() == 0){
                tnList.add(value);
            }
        }

        int sizetn = tnList.size();
        TreeNode[] tn = new TreeNode[sizetn];
        for (int i = 0; i < sizetn; i++) {
            tn[i] = tnList.get(i);
        }

        TreeNode[] sampling = sample(tn,10);

        int size3 = layer3.size();
        TreeNode[] tn3 = new TreeNode[size3];
        for (int i = 0; i < size3; i++) {
            tn3[i] = layer3.get(i);
        }
        TreeNode[] sampling3 = sample(tn3,10);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("node1,node2\n");
        int size = Math.min(sampling3.length, sampling.length);
        System.out.println(size);
        for (int i = 0; i < size; i++) {
            stringBuilder.append(String.valueOf(sampling3[i].getId())).append(",").append(String.valueOf(sampling[i].getId())).append("\n");
        }

        String filepath = "D:\\GithubRepository\\BIT\\dataset\\exp4\\exp4-3-nochild.csv";
        File file = new File(filepath);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(stringBuilder.toString());
            writer.close();
            System.out.println("Result has been written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Test
    void exp4data(){
        HashMap<Long, TreeNode> tree = DataSet.treeData(DataSet.NCBI);
        TreeNode root = tree.get(1L);
        ArrayList<TreeNode> layer3 = getNodesAtLevel(root, 3);
        ArrayList<TreeNode> layer20 = getNodesAtLevel(root, 10);

        int size3 = layer3.size();
        TreeNode[] tn3 = new TreeNode[size3];
        for (int i = 0; i < size3; i++) {
            tn3[i] = layer3.get(i);
        }

        int size20 = layer20.size();
        TreeNode[] tn20 = new TreeNode[size20];
        for (int i = 0; i < size20; i++) {
            tn20[i] = layer20.get(i);
        }

        TreeNode[] sampling3 = sample(tn3,10);
        TreeNode[] sampling20 = sample(tn20,10);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("node1,node2\n");
        int size = Math.min(sampling3.length, sampling20.length);
        System.out.println(size);
        for (int i = 0; i < size; i++) {
            stringBuilder.append(String.valueOf(sampling3[i].getId())).append(",").append(String.valueOf(sampling20[i].getId())).append("\n");
        }

        String filepath = "D:\\GithubRepository\\BIT\\dataset\\exp4\\exp4-3-10.csv";
        File file = new File(filepath);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(stringBuilder.toString());
            writer.close();
            System.out.println("Result has been written to file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public static void getAllDescendants(TreeNode node, ArrayList<TreeNode> descendants) {
        ArrayList<TreeNode> children = node.getChildren();

        for (TreeNode child : children) {
            descendants.add(child);
            getAllDescendants(child, descendants);
        }
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

    public static ArrayList<TreeNode> getNodesAtLevel(TreeNode node, int targetLevel) {
        ArrayList<TreeNode> nodes = new ArrayList<>();
        getNodesAtLevelHelper(node, 1, targetLevel, nodes);
        return nodes;
    }

    private static void getNodesAtLevelHelper(TreeNode node, int currentLevel, int targetLevel, ArrayList<TreeNode> nodes) {
        if (node == null) {
            return;
        }

        if (currentLevel == targetLevel) {
            nodes.add(node);

            return;
        }

        for (TreeNode child : node.getChildren()) {
            getNodesAtLevelHelper(child, currentLevel + 1, targetLevel, nodes);
        }
    }

    public static ArrayList<TreeNode> getNodesWithinLevels(TreeNode node, int maxDepth) {
        ArrayList<TreeNode> nodes = new ArrayList<>();
        getNodesWithinLevels(node, 1, maxDepth, nodes);
        return nodes;
    }

    private static void getNodesWithinLevels(TreeNode node, int currentDepth, int maxDepth, ArrayList<TreeNode> nodes) {
        if (currentDepth <= maxDepth) {
            nodes.add(node);
            if (currentDepth < maxDepth) {
                for (TreeNode child : node.getChildren()) {
                    getNodesWithinLevels(child, currentDepth + 1, maxDepth, nodes);
                }
            }
        }
    }

    public static void saveNodeIdsAndParentIds(ArrayList<TreeNode> nodes) {
        result = new String[nodes.size()][2];
        currentIndex = 0;
        for (TreeNode node : nodes) {
            long parentId = (node.getParent() != null) ? node.getParent().getId() : -1;
            result[currentIndex][0] = Long.toString(node.getId());
            result[currentIndex][1] = Long.toString(parentId);
            currentIndex++;
        }
    }
}
