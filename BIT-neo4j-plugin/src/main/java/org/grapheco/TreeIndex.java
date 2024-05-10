package org.grapheco;

import com.carrotsearch.sizeof.RamUsageEstimator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.util.*;
import java.util.function.LongFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;


public class TreeIndex {
//    public static HashMap<String, BIT> treeIndexs = new HashMap<>();

    public static BIT treeIndex;
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    @Procedure(value = "BIT.createTreeIndex", mode = Mode.SCHEMA)
    @Description("Create BIT for the tree with input node as root.")
    public void createIndex(@Name("rootId") long root,
                                                  @Name("relationshipType") String type,
                                                  @Name(value = "maxDepth", defaultValue = "-1") long maxDepth,
                                                  @Name(value = "out", defaultValue = "true") boolean out) {
//        String indexName = indexName(root, type, maxDepth, out);
//        log.debug("index name: %s", indexName);
//        if (treeIndexs.containsKey(indexName)){
//            log.debug("Skipping index query since index already exist: `%s`", indexName);
//            return ;
//        }

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
            treeIndex = BIT.createIndex(data, root);
            log.debug("Created successfully! Time: %sms; Nodes: %s; Bit Size: %s; Index Size: %s", (System.currentTimeMillis() - t0),
                    treeIndex.getNodeSize(), treeIndex.getVectorSize(), treeIndex.printSize());
        }catch (Exception e){
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.debug(stackTraceElement.toString());
            }
            log.debug("Create tree index fail, with exception: %s", e.getMessage());
        }
    }


    @UserFunction(value = "BIT.isAncestor")
    @Description("")
    public boolean check(@Name("parent") Node parent, @Name("child") Node child){
        try {
            return treeIndex.isAncestor(parent.getId(), child.getId());
        } catch (Exception e) {
            log.debug("Create tree index fail, with exception: %s", e.getMessage());
            return false;
        }
    }

    @UserFunction(value = "BIT.commonAncestor")
    @Description("")
    public Node commonAncestor(@Name("firstNode") Node nodeA, @Name("secondNode") Node nodeB){
        long prefixId = treeIndex.root;

        try {
            prefixId = treeIndex.commonAncestor(new long[]{nodeA.getId(), nodeB.getId()});
        } catch (Exception e) {
            log.debug("Common ancestor fail: %s", e.getMessage());
        }
        return db.getNodeById(prefixId);
    }

//    @Procedure(value = "BIT.allDescendants", mode = Mode.READ, eager = true)
    @UserFunction(value = "BIT.allDescendants")
    @Description("")
    public List<Node> allDescendants(@Name("node") Node node){
//    public List<Long> allDescendants(@Name("node") Node node){
        byte[] vector = null;
        long t0 = System.currentTimeMillis();
        try {
            vector = treeIndex.getCodeById(node.getId());
        } catch (Exception e) {
            log.debug("Create tree index fail, with exception: %s", e.getMessage());
        }
        if (vector==null) {
            log.error("can not find this node in this index!");
            return new ArrayList<Node>();
        } else {
            BitSet bitSet = BitSet.valueOf(vector);
            long t1 = System.currentTimeMillis();
            log.debug("The vector of %s is: %s", node.getId(), bitSet.toString());
            long[] descendantsId = treeIndex.geneFilter(bitSet);
            long t2 = System.currentTimeMillis();
//            ArrayList<Long> ids = new ArrayList<Long>(descendantsId.length);
//            for (long id : descendantsId) ids.add(id);
//            return ids;
            List<Node> rsl = Arrays.stream(descendantsId).filter(value -> value!=node.getId()).mapToObj(value -> db.getNodeById(value)).collect(Collectors.toList());
            long t3 = System.currentTimeMillis();
            log.debug("get bit time: %s, gene filter time: %s, trans time: %s", t1- t0, t2-t1, t3 - t2);
            return rsl;
        }
    }

    @UserFunction(value = "BIT.allDescendantsId")
    @Description("")
    public List<Long> allDescendantsId(@Name("node") Node node){
//    public List<Long> allDescendants(@Name("node") Node node){
        byte[] vector = null;
        long nodeId= node.getId();
        long t0 = System.currentTimeMillis();
        try {
            vector = treeIndex.getCodeById(nodeId);
        } catch (Exception e) {
            log.debug("Create tree index fail, with exception: %s", e.getMessage());
        }
        if (vector==null) {
            log.error("can not find this node in this index!");
            return new ArrayList<Long>();
        } else {
            BitSet bitSet = BitSet.valueOf(vector);
            long t1 = System.currentTimeMillis();
            log.debug("The vector of %s is: %s", node.getId(), bitSet.toString());
            long[] descendantsId = treeIndex.geneFilter(bitSet);
            long t2 = System.currentTimeMillis();

            ArrayList<Long> ids = new ArrayList<Long>(descendantsId.length);

            for (long id : descendantsId) if(id != nodeId) ids.add(id);
            long t3 = System.currentTimeMillis();
            log.debug("get bit time: %s, gene filter time: %s, trans time: %s", t1- t0, t2-t1, t3 - t2);
            return ids;
        }
    }

    @UserFunction(value = "BIT.hello")
    @Description("")
    public String hello(){
        return "BIT: :)hello.";
    }

    private String indexName(long id, String type, long max, boolean out) {
        return id + type + max + (out?"out":"in");
    }
}
