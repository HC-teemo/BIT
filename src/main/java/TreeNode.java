import java.util.ArrayList;

public class TreeNode {
    private TreeNode parent;
    private final int id;
    private byte[] code;
    private int weight = -1;
    private ArrayList<TreeNode> children = new ArrayList<>();

    public TreeNode(TreeNode parent, int id) {
        this.parent = parent;
        this.id = id;
    }

    public byte[] getCode() {
        return code;
    }

    public void setCode(byte[] code) {
        this.code = code;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public void addChildren(TreeNode c) {
        this.children.add(c);
    }

    public int getId() {
        return id;
    }
}
