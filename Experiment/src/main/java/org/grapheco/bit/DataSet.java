package org.grapheco.bit;

import java.io.File;
import java.util.HashMap;

public class DataSet {
    private static final String rootDir = System.getProperty("user.dir");
    private static final String datasetDir = rootDir + "/dataset";
    private static final String experimentDir = datasetDir + "/experiment";

    public static final String NCBI = datasetDir + "/ncbi/NCBI414.csv";
    public static final String NCBI414_h3 = datasetDir + "/ncbi/h3.csv";
    public static final String NCBI414_h4 = datasetDir + "/ncbi/h4.csv";
    public static final String NCBI414_h5 = datasetDir + "/ncbi/h5.csv";
    public static final String NCBI414_h6 = datasetDir + "/ncbi/h6.csv";
    public static final String NCBI414_h10 = datasetDir + "/ncbi/h10.csv";
    public static final String NCBI414_h12 = datasetDir + "/ncbi/h12.csv";
    public static final String NCBI414_h14 = datasetDir + "/ncbi/h14.csv";
    public static final String NCBI414_h20 = datasetDir + "/ncbi/h20.csv";

    public static final File list2 = new File(experimentDir+"/list/h6_10.csv");
    public static final File list3 = new File(experimentDir+"/list/h6_100.csv");
    public static final File list4 = new File(experimentDir+"/list/h6_1000.csv");
    public static final File list5 = new File(experimentDir+"/list/h6_10000.csv");

    public static final File lca2 = new File(experimentDir+"/list/h6_10000.csv");

    public static HashMap<Long, TreeNode> treeData(String path) {
        return EncodingAlgorithm.getTree(IO.loadData(path));
    }

}
