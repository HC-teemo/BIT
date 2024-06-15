package org.grapheco.bit;

import com.carrotsearch.sizeof.RamUsageEstimator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

public class BITwithBitset {

    public long root;
    private ArrayList<BitSet> indexs;

    private HashMap<String, Long> codeId = new HashMap<>();

    private byte[][] codes;

    private HashMap<Long, Integer> idIndex = new HashMap<>();

    private long[] indexId;

    public BITwithBitset(int weight, int size) {
        init(weight, size);
    }

    public void init(int weight, int size){
        this.indexs = new ArrayList<BitSet>(weight);
        for (int i = 0; i < weight; i++) {
            this.indexs.add(new BitSet(size));
        }
    }

    public void insert(int pos, BitSet vector) throws Exception {
        if (vector.length() > indexs.size()) throw new Exception("out of bits." + vector.length() + ">" + indexs.size());
        vector.stream().forEach(i->{
            indexs.get(i).set(pos);
        });
    }

    public BitSet filter(BitSet vector) throws Exception {
        if (vector.length() > indexs.size()) throw new Exception("out of bits.");
        return vector.stream().mapToObj(i -> indexs.get(i)).reduce((bit1, bit2) -> {
            bit1.and(bit2);
            return bit1;
        }).get();
    }

//    public RoaringBitmap filterParallel(BitSet vector) throws Exception {
//        if (vector.length() > indexs.size()) throw new Exception("out of bits.");
//        return vector.stream().parallel().mapToObj(i -> indexs.get(i).clone()).reduce((bit1, bit2) -> {
//            bit1.and(bit2);
//            return bit1;
//        }).get();
//    }

    public static BITwithBitset fromCSV(String path) throws Exception {
        File csv = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(csv));
        String[] header = br.readLine().split(",");
        int length = Integer.parseInt(header[0]);
        int weight = Integer.parseInt(header[1]);

        BITwithBitset bit = new BITwithBitset(weight, length);
        bit.indexId = new long[length];
        bit.codes = new byte[length][];
        int i = 0;

        String line = "";
        while ((line = br.readLine()) != null){
            String[] split = line.trim().split(",");
            long id = Long.parseLong(split[0]);
            String codeStr = split.length==1?"":split[1];
            byte[] code = Base64.getDecoder().decode(codeStr);

            bit.indexId[i] = id;
            bit.codes[i] = code;
            bit.idIndex.put(id, i);
            bit.codeId.put(codeStr, id);
            bit.insert(i, BitSet.valueOf(code));
            i++;
        }
        return bit;
    }

    public void toCSV(String path) throws Exception {
        String[][] csv = new String[this.indexId.length][2];
        for (int i = 0; i < this.indexId.length; i++) {
            csv[i][0] = this.indexId[i] + "";
            csv[i][1] = Base64.getEncoder().encodeToString(this.codes[i]);
        }
        IO.toCSV(path, new String[]{getNodeSize()+"", getVectorSize()+""}, csv);
    }

    public static BITwithBitset createIndex(List<long[]> data, long rootId) throws Exception {
        HashMap<Long, TreeNode> tree = EncodingAlgorithm.getTree(data, rootId);
        TreeNode root = tree.get(rootId);
        EncodingAlgorithm.chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        int size = root.getWeight();
        EncodingAlgorithm.encode(root);


        TreeNode[] nodes = tree.values().toArray(new TreeNode[0]); // TODO
        BITwithBitset bit = new BITwithBitset(size, nodes.length);
        bit.indexId = new long[nodes.length];
        bit.codes = new byte[nodes.length][];

        for (int i = 0; i < nodes.length; i++) {
            TreeNode node = nodes[i];
            bit.indexId[i] = node.getId();
            bit.codes[i] = node.getCode();
            bit.idIndex.put(node.getId(), i);
            bit.codeId.put(Base64.getEncoder().encodeToString(node.getCode()), node.getId());
            bit.insert(i, BitSet.valueOf(node.getCode()));
        }
        bit.root = rootId;
        return bit;
    }

    public long getIdByCode(byte[] v) throws Exception {
        String key = Base64.getEncoder().encodeToString(v);
        if (codeId.containsKey(key)){
            return this.codeId.get(key);
        } else {
            throw new Exception("can not find vector: " + Arrays.toString(v));
        }
    }

    public long getIdByIndex(int index) { return this.indexId[index];}

    public byte[] getCodeById(long id) throws Exception {
//        if (!this.idIndex.containsKey(id)) throw new Exception("can not find code of " + id);
        if (this.idIndex.get(id) == null)
            return null;
        int index= this.idIndex.get(id);

        return this.codes[index];
    }

    public boolean isAncestor(long a, long c) throws Exception {
        return EncodingAlgorithm.isParentOf(getCodeById(a), getCodeById(c));
    }

    public long commonAncestor(long[] ids) throws Exception {
        byte[][] vs = new byte[ids.length][];
        for (int i = 0; i < ids.length; i++) {
            vs[i] = getCodeById(ids[i]);
        }
        byte[] prefix = EncodingAlgorithm.commonPrefix(vs);
        BitSet bitSet = BitSet.valueOf(prefix);
        String key = Base64.getEncoder().encodeToString(prefix);
        while (!this.codeId.containsKey(key) && prefix.length!=0){ //remove a gene
            bitSet.clear(bitSet.length()-1);
            prefix = bitSet.toByteArray();
            key = Base64.getEncoder().encodeToString(prefix);
        }
        return this.codeId.get(key);
    }

    public long[] geneFilter(BitSet genes) throws Exception {
//        ArrayList<RoaringBitmap> select = new ArrayList<>(genes.cardinality());
//        genes.stream().forEach(g->{
//            if (g<this.indexs.size()) select.add(indexs.get(g).clone());
//        });
//        if (genes.isEmpty()){
//            return indexId;
//        } else {
//            RoaringBitmap result = select.stream().parallel().reduce((a, b)->{a.and(b); return a;}).get();
//            return StreamSupport.stream(result.spliterator(), true).mapToLong(this::getIdByIndex).toArray();
//        }
        return StreamSupport.stream(filter(genes).stream().spliterator(), false).mapToLong(this::getIdByIndex).toArray();
    }

    public int getVectorSize(){ return this.indexs.size();}

    public int getNodeSize(){ return this.indexId.length;}

    public String printSize(){
        AtomicLong indexSize = new AtomicLong();
        indexs.forEach(bitmap->{
            indexSize.addAndGet(RamUsageEstimator.sizeOf(bitmap));
        });
        return String.format("indexs: %s, codeId: %s, codes:%s, idIndex:%s, indexId:%s, all: %s",
//                RamUsageEstimator.sizeOf(indexs),
                indexSize,
                RamUsageEstimator.sizeOf(codeId),
                RamUsageEstimator.sizeOf(codes),
                RamUsageEstimator.sizeOf(idIndex),
                RamUsageEstimator.sizeOf(indexId), RamUsageEstimator.sizeOf(this));
    }
}
