package org.grapheco;

import java.util.*;

public class TransMethod {
    private HashMap<Long, TreeNode> tree;

    public TransMethod(HashMap<Long, TreeNode> tree) {
        this.tree = tree;
    }

    public static TransMethod createIndex(List<long[]> data, long rootId) throws Exception {
        return new TransMethod(EncodingAlgorithm.getTree(data, rootId));
    }

    public TreeNode lca(TreeNode node, TreeNode node2) {
        ArrayList<TreeNode> parents = new ArrayList<>();
        parents.add(node);
        while (node.getParent() != null) {
            parents.add(node.getParent());
            node = node.getParent();
        }
        while (node2 != null) {
            if (parents.contains(node2)) break;
            else node2 = node2.getParent();
        }
        return node2;
    }

    public TreeNode lca(long[] ids) {
        return Arrays.stream(ids)
                .mapToObj(id -> tree.get(id))
                .reduce(this::lca).get();
    }

    public ArrayList<TreeNode> list(long id) {
        TreeNode node = tree.get(id);
        ArrayList<TreeNode> list = new ArrayList<>();

        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();
            list.add(current);
            queue.addAll(current.getChildren());
        }
        return list;
    }

    public boolean judge(long desenId, long asenId) {
        TreeNode desen = tree.get(desenId);
        TreeNode asen = tree.get(asenId);
        while (desen != null) {
            if (asen.equals(desen)) return true;
            desen = desen.getParent();
        }
        return false;
    }

}
