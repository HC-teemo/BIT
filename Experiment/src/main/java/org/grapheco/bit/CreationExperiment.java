package org.grapheco.bit;

public class CreationExperiment {
    static String dataDir = DataSet.datasetDir+"/generated/";
    static String outDir  = DataSet.datasetDir+"/generated-out/";

    static String[] scales = new String[]{
            "0.01", "0.03",
            "0.1", "0.3", "1", "3",
            "10", "30",
//            "100",
    };

    public static void createAndOutput(String input, String output, String size) throws Exception {
        long start = System.currentTimeMillis();
        BIT bit = BIT.createIndex(IO.loadData(input), 1L);
        long checkpoint1 = System.currentTimeMillis();
        bit.serialize(output);
        long end = System.currentTimeMillis();
        System.out.printf("Create Index in Size %s Cost: %s ms and %s bits.%n", size, checkpoint1-start, bit.getVectorSize());
        System.out.printf("Output Index in Size %s Cost: %s ms.%n", size, end-checkpoint1);
    }

    public static void main(String[] args) throws Exception {
        for (String scale : scales) {
            String input = dataDir+"size"+scale+".csv";
            String out = outDir+"/"+scale+"/";

            createAndOutput(input,out, scale);
        }
    }
}
