package org.grapheco;

import com.carrotsearch.sizeof.RamUsageEstimator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.util.*;
import java.util.stream.Collectors;


public class NativeTreeIndex {
//    public static HashMap<String, BIT> treeIndexs = new HashMap<>();

    public static HashMap<Long, TreeNode> treeIndex;
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    @Procedure(value = "Native.createTreeIndex", mode = Mode.SCHEMA)
    public void createIndex(@Name("rootId") long root,
                                                  @Name("relationshipType") String type,
                                                  @Name(value = "maxDepth", defaultValue = "-1") long maxDepth,
                                                  @Name(value = "out", defaultValue = "true") boolean out) {

        String edges = "[r:"+ type+ "*.." + (maxDepth>0?maxDepth:"") +"]";
        String return_ = "RETURN " + (out?
                "id(startNode(last(r))) as parent, id(endNode(last(r))) as child"
                :"id(startNode(last(r))) as child, id(endNode(last(r))) as parent");
        String query = "MATCH (root)" + (out?"-"+edges+"->":"<-"+edges+"-") + "(children) " +
                "WHERE id(root) = " + root + " " + return_;

        log.debug("Start create tree index, read data by: %s", query);

        List<long[]> data = db.execute(query).stream().map(element -> {
            return new long[]{(long) element.get("child"), (long) element.get("parent")};
        }).collect(Collectors.toList());

        log.debug("Data read successfully! Start creating.");
        try {
            long t0 = System.currentTimeMillis();
//            treeIndexs.put(indexName, BIT.createIndex(data, root));
            treeIndex = EncodingAlgorithm.getTree(data, root);
            log.debug("Created successfully! Time: %sms; Index Size: %s", (System.currentTimeMillis() - t0),
                    RamUsageEstimator.sizeOf(treeIndex));
        }catch (Exception e){
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.debug(stackTraceElement.toString());
            }
            log.debug("Create tree index fail, with exception: %s", e.getMessage());
        }
    }


    @UserFunction(value = "Native.isAncestor")
    @Description("")
    public boolean check(@Name("parent") Node parent, @Name("child") Node child){
        try {
            TreeNode p = treeIndex.get(parent.getId());
            TreeNode c = treeIndex.get(child.getId());
            while (p!=c && c.getParent()!=null) c = c.getParent();
            return p==c;
        } catch (Exception e) {
            log.debug("Create tree index fail, with exception: %s", e.getMessage());
            return false;
        }
    }

    @UserFunction(value = "Native.commonAncestor")
    @Description("")
    public Node commonAncestor(@Name("firstNode") Node nodeA, @Name("secondNode") Node nodeB){
        TreeNode a = treeIndex.get(nodeA.getId());
        TreeNode b = treeIndex.get(nodeB.getId());
        ArrayList<TreeNode> ps = new ArrayList<>();
        TreeNode t = a;
        while (t!=null){ps.add(t);t=t.getParent();}
        t = b;
        while (t!=null && !ps.contains(t)){t = t.getParent();}
        return db.getNodeById(t.getId());
    }

//    @Procedure(value = "BIT.allDescendants", mode = Mode.READ, eager = true)
    @UserFunction(value = "Native.allDescendants")
    @Description("")
    public List<Node> allDescendants(@Name("node") Node node){

        return allDescendantsId(node).stream().map(db::getNodeById).collect(Collectors.toList());
    }

    @UserFunction(value = "Native.allDescendantsId")
    @Description("")
    public List<Long> allDescendantsId(@Name("node") Node node){
        TreeNode n = treeIndex.get(node.getId());
        Stack<TreeNode> stack = new Stack<>();
        ArrayList<TreeNode> des = new ArrayList<>();
//        des.add(n);
        stack.push(n);
        while (!stack.isEmpty()) {
            TreeNode t = stack.pop();
            des.addAll(t.getChildren());
            stack.addAll(t.getChildren());
        }
        return des.stream().map(TreeNode::getId).collect(Collectors.toList());
    }

    @UserFunction(value = "Native.hello")
    @Description("")
    public String hello(){
        return "BIT: :)hello.";
    }

    private String indexName(long id, String type, long max, boolean out) {
        return id + type + max + (out?"out":"in");
    }
}
