package org.grapheco.bit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class VectorTest extends TestBase{
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
                "RETURN b.vector STARTS WITH a.vector").next().get(0).asBoolean();
        return actual == expect;
    }

    @Override
    protected boolean query2(Session session, long id1, long id2, long expect) {
        long actual = session.run("MATCH (x:Taxonomy{id:"+ id1 +"}), (y:Taxonomy{id:"+id2+"}) \n" +
                "WITH x.vector AS vx, y.vector AS vy\n" +
                "WITH [i IN range(0,size(vx)) WHERE left(vx,i)=left(vy,i) and right(left(vx,i),1)='1' or i=0| left(vx,i)] AS prefix\n" +
                "MATCH (p:Taxonomy) where p.vector in prefix\n" +
                "RETURN p ORDER By size(p.vector) desc limit 1").next().get(0).asNode().get("id").asLong();
        return actual == expect;
    }

    @Override
    protected boolean query3(Session session, long id, int expect) {
        int actual = session.run("MATCH (s:Taxonomy{id:"+id+"}), (c:Taxonomy) \n" +
                "WHERE c.vector STARTS WITH s.vector and c <> s\n" +
                "RETURN c").list().stream().mapToLong(r->r.get(0).asNode().id()).toArray().length;
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
