package org.grapheco.bit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class BIT {
    static final long allBitsOne = 0xFFFFFFFFFFFFFFFFL;

    private static final Kryo kryo = new Kryo();
    static {
        kryo.register(long[][].class);
        kryo.register(HashMap.class);
        kryo.register(long[].class);
        kryo.register(byte[][].class);
        kryo.register(byte[].class);
    }


//    public long root;
    private long[][] bitmaps;

    private HashMap<String, Long> code2Id = new HashMap<>();

    private byte[][] codes;

    private HashMap<Long, Integer> id2Index = new HashMap<>();

    private long[] ids;

    public BIT() {
    }

    public BIT(long[][] bitmaps, HashMap<String, Long> code2Id, byte[][] codes, HashMap<Long, Integer> id2Index, long[] ids) {
        this.bitmaps = bitmaps;
        this.code2Id = code2Id;
        this.codes = codes;
        this.id2Index = id2Index;
        this.ids = ids;
    }

    public long[] filter(int[] index) throws Exception {
        if (index.length == 0) {
            long[] result = new long[this.bitmaps[0].length];
            Arrays.fill(result, allBitsOne);
            return result;
        }
        long[] result = bitmaps[index[0]];
        for (int i = 1; i < index.length; i++) {
            long[] bitmap = bitmaps[i];
            IntStream.range(0, bitmap.length).parallel().forEach(j -> result[j] = result[j] & bitmap[j]);
//            for (int j = 0; j < bitmap.length; j++)
//                result[j] = result[j] & bitmap[j];
        }
        return result;
    }

    public long[] filter2(int[] index) throws Exception {
        if (index.length == 0) {
            long[] result = new long[this.bitmaps[0].length];
            Arrays.fill(result, allBitsOne);
            return result;
        }
        long[] result = bitmaps[index[0]];
        for (int i = 1; i < index.length; i++) {
             long[] bitmap = bitmaps[i];
             for (int j = 0; j < bitmap.length; j++)
                 result[j] = result[j] & bitmap[j];
        }
        return result;
    }

    private long[] and(long[] a, long[] b) {
        int length = Math.min(a.length, b.length);
        long[] c = new long[length];
        for (int i = 0; i < length; i++)
            c[i]= a[i] & b[i];
        return c;
    }

    public static BIT fromCSV(String path) throws Exception {
        File csv = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(csv));
        String[] header = br.readLine().split(",");
        int length = Integer.parseInt(header[0]);
        int weight = Integer.parseInt(header[1]);

        BIT bit = new BIT();
        bit.ids = new long[length];
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

            bit.ids[i] = id;
            bit.codes[i] = code;
            bit.id2Index.put(id, i);
            bit.code2Id.put(codeStr, id);

            BitSet vector =  BitSet.valueOf(code);
            int finalI = i;
            vector.stream().mapToObj(bitmaps::get).forEach(bitSet -> bitSet.set(finalI));
            i++;
        }
        bit.bitmaps = new long[weight][];
        for (i = 0; i < weight; i++) {
            bit.bitmaps[i] = bitmaps.get(i).toLongArray();
        }
        return bit;
    }

    public void toCSV(String path) throws Exception {
        String[][] csv = new String[this.ids.length][2];
        for (int i = 0; i < this.ids.length; i++) {
            csv[i][0] = this.ids[i] + "";
            csv[i][1] = Base64.getEncoder().encodeToString(this.codes[i]);
        }
        IO.toCSV(path, new String[]{getNodeSize()+"", getVectorSize()+""}, csv);

        String[][] bitmap = new String[this.bitmaps.length][];
        for (int i = 0; i < this.bitmaps.length; i++) {
            bitmap[i] = Arrays.stream(bitmaps[i]).mapToObj(String::valueOf).toArray(String[]::new);
        }
        IO.toCSV(path+"bitmap", new String[]{getNodeSize()+"", getVectorSize()+""}, bitmap);
    }

    public static BIT createIndex(List<long[]> data, long rootId) throws Exception {
        HashMap<Long, TreeNode> tree = EncodingAlgorithm.getTree(data, rootId);
        TreeNode root = tree.get(rootId);
        EncodingAlgorithm.chotomic(root, EncodingAlgorithm.ChotomicType.Polychotomic);
        int weight = root.getWeight();
        EncodingAlgorithm.encode(root);

        TreeNode[] nodes = tree.values().toArray(new TreeNode[0]); // TODO

        int length = nodes.length;
        BIT bit = new BIT();
        bit.ids = new long[length];
        bit.codes = new byte[length][];

        ArrayList<BitSet> bitmaps = new ArrayList<>(weight);
        for (int i = 0; i < weight; i++) {
            bitmaps.add(new BitSet(length));
        }

        for (int i = 0; i < nodes.length; i++) {
            TreeNode node = nodes[i];
            bit.ids[i] = node.getId();
            bit.codes[i] = node.getCode();
            bit.id2Index.put(node.getId(), i);
            bit.code2Id.put(Base64.getEncoder().encodeToString(node.getCode()), node.getId());

            BitSet vector =  BitSet.valueOf(node.getCode());
            int finalI = i;
            vector.stream().mapToObj(bitmaps::get).forEach(bitSet -> bitSet.set(finalI));
        }
        bit.bitmaps = new long[weight][];
        for (int i = 0; i < weight; i++) {
            bit.bitmaps[i] = bitmaps.get(i).toLongArray();
        }
        return bit;
    }

    public long getIdByCode(byte[] v) throws Exception {
        String key = Base64.getEncoder().encodeToString(v);
        if (code2Id.containsKey(key)){
            return this.code2Id.get(key);
        } else {
            throw new Exception("can not find vector: " + Arrays.toString(v));
        }
    }

    public byte[] getCodeById(long id) throws Exception {
//        if (!this.idIndex.containsKey(id)) throw new Exception("can not find code of " + id);
        if (this.id2Index.get(id) == null)
            return null;
        int index= this.id2Index.get(id);

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
        while (!this.code2Id.containsKey(key) && prefix.length!=0){ //remove a gene
            bitSet.clear(bitSet.length()-1);
            prefix = bitSet.toByteArray();
            key = Base64.getEncoder().encodeToString(prefix);
        }
        return this.code2Id.get(key);
    }

    public long[] geneFilter(byte[] genes) throws Exception {
        int[] vector = BitSet.valueOf(genes).stream().toArray();
        return BitSet.valueOf(filter(vector)).stream().parallel().mapToLong(i -> this.ids[i]).toArray();
    }

    public long[] geneFilter2(byte[] genes) throws Exception {
        int[] vector = BitSet.valueOf(genes).stream().toArray();
        return BitSet.valueOf(filter2(vector)).stream().parallel().mapToLong(i -> this.ids[i]).toArray();
    }

    public int getVectorSize(){ return this.bitmaps.length;}

    public int getNodeSize(){ return this.ids.length;}

    public void serialize(String path) throws Exception {
        Kryo kryo = BIT.kryo;
        write(path+"bitmaps.bin", this.bitmaps, kryo);
        write(path+"ids.bin", this.ids, kryo);
        write(path+"codes.bin", this.codes, kryo);
        write(path+"code2Id.bin", this.code2Id, kryo);
        write(path+"id2Index.bin", this.id2Index, kryo);
    }

    public static BIT deserialize(String path) throws Exception {
        Kryo kryo = BIT.kryo;
        return new BIT(
                read(path+"bitmaps.bin", long[][].class, kryo),
                read(path+"code2Id.bin", HashMap.class, kryo),
                read(path+"codes.bin", byte[][].class, kryo),
                read(path+"id2Index.bin", HashMap.class, kryo),
                read(path+"ids.bin", long[].class, kryo)
        );
    }

    private void write(String path, Object object, Kryo kryo) throws Exception {
        FileOutputStream fos = new FileOutputStream(new File(path));
        Output output = new Output(fos);
        kryo.writeObject(output, object);
        output.close();
        fos.close();
    }

    public static  <T> T read(String path, Class<T> type, Kryo kryo) throws Exception {
        FileInputStream fis = new FileInputStream(new File(path));
        Input input = new Input(fis);
        T result = kryo.readObject(input, type);
        input.close();
        fis.close();
        return result;
    }
}
