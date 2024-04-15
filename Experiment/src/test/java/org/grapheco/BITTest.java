package org.grapheco;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class BITTest extends TestBase{
    static Driver driver = null;
    @BeforeAll
    static void init(){
        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
    }
    @AfterAll
    static void close(){
        driver.close();
    }
    @Override
    protected boolean query1(Session session, long id1, long id2, boolean expect) {
        boolean actual = session.run("MATCH (a:Taxonomy{id:"+ id1 +"}), (b:Taxonomy{id:"+ id2 +"})\n" +
                "RETURN BIT.isAncestor(a,b)").next().get(0).asBoolean();
        if (actual!=expect) System.out.printf("fail in test: %s %s, expect: %s, actual: %s \n", id1, id2, expect, actual);
        return actual == expect;
    }

    @Override
    protected boolean query2(Session session, long id1, long id2, long expect) {
        long actual = session.run("MATCH (x:Taxonomy{id:"+ id1 +"}), (y:Taxonomy{id:"+id2+"}) \n" +
                "RETURN BIT.commonAncestor(x,y)").next().get(0).asNode().get("id").asLong();
        if (actual!=expect) System.out.printf("fail in test: %s %s, expect: %s, actual: %s \n", id1, id2, expect, actual);
        return actual == expect;
    }

    @Override
    protected boolean query3(Session session, long id, int expect) {
        int actual = session.run("MATCH (s:Taxonomy{id:"+id+"}) \n" +
                "unwind BIT.allDescendantsId(s) as c return c").list().size();
        if (actual!=expect) System.out.printf("fail in test: %s, expect: %s, actual: %s \n", id, expect, actual);
        return actual == expect;
    }

    @Test
    void test1(){
        Session session = driver.session();
        this.test4query1(session);
    }

    @Test
    void test2(){
        Session session = driver.session();
        this.test4query2(session);
    }

    @Test
    void test3(){
        Session session = driver.session();
        this.test4query3(session);
    }
}
