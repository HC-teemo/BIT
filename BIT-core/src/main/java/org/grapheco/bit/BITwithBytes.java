package org.grapheco.bit;

import com.carrotsearch.sizeof.RamUsageEstimator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class BITwithBytes {

    public long root;
    private long[][] indexs;

    private HashMap<String, Long> codeId = new HashMap<>();

    private byte[][] codes;

    private HashMap<Long, Integer> idIndex = new HashMap<>();

    private long[] indexId;

    public BITwithBytes(int weight, int size) {
        init(weight, size);
    }

    public void init(int weight, int size){
        this.indexs = new long[size][];
    }

    public long[] filter(BitSet vector) throws Exception {
        if (vector.length() > indexs.length) throw new Exception("out of bits.");
        return vector.stream().mapToObj(i -> indexs[i]).reduce(
                (a,b)-> {
                    int length = Math.min(a.length, b.length);
                    for (int i = 0; i < length; i++)
                        a[i]= a[i] & b[i];
                    return a;
                }).get();
    }


    public static BITwithBytes fromCSV(String path) throws Exception {
        File csv = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(csv));
        String[] header = br.readLine().split(",");
        int length = Integer.parseInt(header[0]);
        int weight = Integer.parseInt(header[1]);

        BITwithBytes bit = new BITwithBytes(weight, length);
        bit.indexId = new long[length];
        bit.codes = new byte[length][];

        String line = "";
        ArrayList<BitSet> bitmaps = new ArrayList<>(weight);
        for (int i = 0; i < weight; i++) {
            bitmaps.add(new BitSet(length));
        }

        int i = 0;
        while ((line = br.readLine()) != null){
            String[] split = line.trim().split(",");
            long id = Long.parseLong(split[0]);
            String codeStr = split.length==1?"":split[1];
            byte[] code = Base64.getDecoder().decode(codeStr);

            bit.indexId[i] = id;
            bit.codes[i] = code;
            bit.idIndex.put(id, i);
            bit.codeId.put(codeStr, id);

            BitSet vector =  BitSet.valueOf(code);
            int finalI = i;
            vector.stream().mapToObj(bitmaps::get).forEach(bitSet -> bitSet.set(finalI));
            i++;
        }
        for (i = 0; i < weight; i++) {
            bit.indexs[i] = bitmaps.get(i).toLongArray();
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

        String[][] bitmap = new String[this.indexs.length][];
        for (int i = 0; i < this.indexs.length; i++) {
            bitmap[i] = Arrays.stream(indexs[i]).mapToObj(String::valueOf).toArray(String[]::new);
        }
        IO.toCSV(path+"bitmap", new String[]{}, bitmap);
    }

    public static BITwithBytes createIndex(List<long[]> data, long rootId) throws Exception {
        HashMap<Long, TreeNode> tree = EncodingAlgorithm.getTree(data, rootId);
        TreeNode root = tree.get(rootId);
        EncodingAlgorithm.chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        int weight = root.getWeight();
        EncodingAlgorithm.encode(root);

        TreeNode[] nodes = tree.values().toArray(new TreeNode[0]); // TODO

        int length = nodes.length;
        BITwithBytes bit = new BITwithBytes(weight, length);
        bit.indexId = new long[length];
        bit.codes = new byte[length][];

        ArrayList<BitSet> bitmaps = new ArrayList<>(weight);
        for (int i = 0; i < weight; i++) {
            bitmaps.add(new BitSet(length));
        }

        for (int i = 0; i < nodes.length; i++) {
            TreeNode node = nodes[i];
            bit.indexId[i] = node.getId();
            bit.codes[i] = node.getCode();
            bit.idIndex.put(node.getId(), i);
            bit.codeId.put(Base64.getEncoder().encodeToString(node.getCode()), node.getId());

            BitSet vector =  BitSet.valueOf(node.getCode());
            int finalI = i;
            vector.stream().mapToObj(bitmaps::get).forEach(bitSet -> bitSet.set(finalI));
        }
        bit.root = rootId;
        for (int i = 0; i < weight; i++) {
            bit.indexs[i] = bitmaps.get(i).toLongArray();
        }
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
        return BitSet.valueOf(filter(genes)).stream().mapToLong(i -> this.indexId[i]).toArray();
    }

    public long[] geneFilter2(BitSet genes) throws Exception {
        return genes.stream().mapToObj(i -> BitSet.valueOf(indexs[i])).reduce(
                (a,b)-> {a.and(b);
                return a;}).get().stream().mapToLong(i -> this.indexId[i]).toArray();
    }

    public int getVectorSize(){ return this.indexs.length;}

    public int getNodeSize(){ return this.indexId.length;}

    public String printSize(){
        return String.format("indexs: %s, codeId: %s, codes:%s, idIndex:%s, indexId:%s, all: %s",
                RamUsageEstimator.sizeOf(indexs),
                RamUsageEstimator.sizeOf(codeId),
                RamUsageEstimator.sizeOf(codes),
                RamUsageEstimator.sizeOf(idIndex),
                RamUsageEstimator.sizeOf(indexId), RamUsageEstimator.sizeOf(this));
    }
}
