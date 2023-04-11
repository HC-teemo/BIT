package org.grapheco;

import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class TestBase {

    static String url = "bolt://localhost:7687";

    static String username = "neo4j";

    static String password = "123";
    static long[][] testData1 = DataSet.query1();
    static long[][] testData2 = DataSet.query2();
    static long[][] testData3 = DataSet.query3();

    protected abstract boolean query1(Session session, long id1, long id2, boolean expect);

    protected abstract boolean query2(Session session, long id1, long id2, long expect);

    protected abstract boolean query3(Session session, long id, int expect);

    protected void preheat(Session session){
        session.run("match (n) return n limit 10");
    }

    protected void test4query1(Session session){
        preheat(session);
        long t0 = System.currentTimeMillis();
        long t1;
        long tt = 0;
        ArrayList<Long> time = new ArrayList<Long>(testData1.length);
        int passed = 0;
        for (long[] t : testData1){
            if(this.query1(session, t[0], t[1], t[2]==1)) passed++;
            t1 = System.currentTimeMillis();
            tt += t1 - t0;
            time.add(t1 - t0);
            t0 = t1;
        }
        session.close();
        System.out.printf("========Query 1========\n" +
                "all time: %s ms\n" +
                "test number: %s \n" +
                "avg time: %s ms\n" +
                "passed: %s / %s\n", time.toString(), testData1.length, tt / testData1.length,
                passed, testData1.length);
    }

    protected void test4query2(Session session){
        preheat(session);
        long t0 = System.currentTimeMillis();
        long t1;
        long tt = 0;
        ArrayList<Long> time = new ArrayList<Long>(testData2.length);
        int passed = 0;
        for (long[] t : testData2){
            if(this.query2(session, t[0], t[1], t[2])) passed++;
            t1 = System.currentTimeMillis();
            tt += t1 - t0;
            time.add(t1 - t0);
            t0 = t1;
        }
        session.close();
        System.out.printf("========Query 2========\n" +
                        "all time: %s ms\n" +
                        "test number: %s \n" +
                        "avg time: %s ms\n" +
                        "passed: %s / %s\n", time.toString(), testData2.length, tt / testData2.length,
                passed, testData2.length);
    }

    protected void test4query3(Session session){
        preheat(session);
        long t0 = System.currentTimeMillis();
        long t1;
        long tt = 0;
        ArrayList<Long> time = new ArrayList<Long>(testData3.length);
        int passed = 0;
        for (long[] t : testData3){
            if(this.query3(session, t[0], (int) t[1])) passed++;
            t1 = System.currentTimeMillis();
            tt += t1 - t0;
            time.add(t1 - t0);
            t0 = t1;
        }
        session.close();
        System.out.printf("========Query 3========\n" +
                        "all time: %s ms\n" +
                        "test number: %s \n" +
                        "avg time: %s ms\n" +
                        "passed: %s / %s\n", time.toString(), testData3.length, tt / testData3.length,
                passed, testData3.length);
    }
}
