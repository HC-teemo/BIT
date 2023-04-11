package org.grapheco;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class BaselineTest extends TestBase{
    static Driver driver = null;
    @BeforeAll
    static void init(){
        driver = GraphDatabase.driver(url, AuthTokens.basic(username, password));
    }

    @AfterAll
    static void close(){
        driver.close();
    }

    protected boolean query1(Session session, long id1, long id2, boolean expect) {
        boolean actual = session.run("MATCH (a:Taxonomy{id:"+ id1 +"}), (b:Taxonomy{id:"+ id2 +"})\n" +
                "RETURN exists((b)-[:PARENT*..]->(a))").next().get(0).asBoolean();
        return actual == expect;
    }

    protected boolean query2(Session session, long id1, long id2, long expect) {
//        System.out.println(id1 + "," + id2);
//        long actual = session.run("MATCH (x:Taxonomy{id:"+ id1 +"})-[:PARENT*..]->(p:Taxonomy)<-[:PARENT*..]-(y:Taxonomy{id:"+ id2 +"})\n" +
//                "RETURN p").next().get(0).asNode().get("id").asLong();
        long actual = session.run(
                "MATCH (x:Taxonomy{id:"+id1+"})-[:PARENT*..]->(p:Taxonomy) with collect(p) as ps\n" +
                        "match (p)<-[:PARENT]-(pc)<-[:PARENT*..]-(y:Taxonomy{id:"+id2+"}) where p in ps and not pc in ps\n" +
                        "RETURN p ").next().get(0).asNode().get("id").asLong();
        return actual == expect;
    }

    protected boolean query3(Session session, long id, int expect) {
        int actual = session.run("MATCH (s:Taxonomy{id:" + id + "})<-[:PARENT*..]-(c:Taxonomy)\n" +
                "RETURN c").list().stream().mapToLong(r->r.get(0).asNode().id()).toArray().length;
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
