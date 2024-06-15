package org.grapheco.bit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TreeGenerator {
    private static final Random random = new Random();
    private static long nodeId = 1; // 节点ID起始值

    public static void main(String[] args) {
        int depth = 30; // 指定树的深度
        int maxNumber = 5000000;
        int minChildren = 3; // 每个节点最少孩子数量
        int maxChildren = 5; // 每个节点最多孩子数量

        TreeNode root = generateTree(depth, minChildren, maxChildren, maxNumber);
        saveTreeToCsv(root, "/Users/huchuan/Documents/GitHub/BIT/dataset/generated/");
    }

    // 非递归生成树的方法
    public static TreeNode generateTree(int maxDepth, int minChildren, int maxChildren, int maxNumber) {
        TreeNode root = new TreeNode(null, nodeId++); // 创建根节点
        Queue<TreeNode> queue = new LinkedList<>();
        Queue<Integer> depthQueue = new LinkedList<>();
        queue.add(root);
        depthQueue.add(1);
        int count=1;

        while (!queue.isEmpty() && nodeId<maxNumber) {
            TreeNode current = queue.poll();
            int currentDepth = depthQueue.poll();

            if (currentDepth < maxDepth) {
                int childrenCount = random.nextInt(maxChildren - minChildren + 1) + minChildren;
                for (int i = 0; i < childrenCount && nodeId<=maxNumber; i++) {
                    TreeNode child = new TreeNode(current,nodeId++);
                    current.addChildren(child);
                    queue.add(child);
                    depthQueue.add(currentDepth + 1);
                }
            }
        }

        return root;
    }

    // 保存树结构为CSV文件的方法
    public static void saveTreeToCsv(TreeNode root, String filePath) {
        List<String[]> csvData = new ArrayList<>();
//        csvData.add(new String[] { "NodeID", "ParentID" });

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();
            for (TreeNode child : current.getChildren()) {
                csvData.add(new String[] { child.getId()+"", current.getId()+""});
                queue.add(child);
            }
        }
        File file = new File(filePath+"size"+(csvData.size()+1)+".csv");
        file.getParentFile().mkdirs(); // 确保所有必要的目录都已创建
        try (FileWriter writer = new FileWriter(file)) {
            for (String[] line : csvData) {
                writer.append(String.join(",", line));
                writer.append("\n");
            }
            System.out.println("CSV file has been saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}