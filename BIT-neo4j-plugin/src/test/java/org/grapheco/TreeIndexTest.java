package org.grapheco;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import java.util.logging.Level;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TreeIndexTest {

    private static final Config driverConfig = Config.build().withLogging(Logging.javaUtilLogging(Level.ALL)).withoutEncryption().toConfig();
    private ServerControls embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {

        this.embeddedDatabaseServer = TestServerBuilders
                .newInProcessBuilder()
                .withProcedure(TreeIndex.class)
                .withFunction(TreeIndex.class)
                .newServer();
    }

    @Test
    public void checksAncestor(){
        // In a try-block, to make sure we close the driver and session after the test
        try(Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
            Session session = driver.session())
        {
            /*
               root - A -B
                    - C -D
             */
            session.run("Create (n:Node{name:'root'})-[:HAS]->(a:Node{name:'a'})-[:HAS]->(b:Node{name:'b'})");
            session.run("Match (n:Node{name:'root'}) Create(n)-[:HAS]->(c:Node{name:'c'})-[:HAS]->(d:Node{name:'d'})");
            session.run("CALL BIT.createTreeIndex(0,'HAS')");
            boolean r0 = session.run("match (n:Node{name:'root'}),(d:Node{name:'d'}) " +
                    "return BIT.isAncestor(n,d)").next().get(0).asBoolean();
            Assertions.assertTrue(r0);
            boolean r1 = session.run("match (n:Node{name:'a'}),(d:Node{name:'d'}) " +
                    "return BIT.isAncestor(n,d)").single().get(0).asBoolean();
            Assertions.assertFalse(r1);

        }
    }

    @Test
    public void commonAncestor(){
        // In a try-block, to make sure we close the driver and session after the test
        try(Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
            Session session = driver.session())
        {
            /*
               root - A -B
                    - C -D
                        -E
             */
            session.run("Create (n:Node{name:'root'})-[:HAS]->(a:Node{name:'a'})-[:HAS]->(b:Node{name:'b'})");
            session.run("Match (n:Node{name:'root'}) Create(n)-[:HAS]->(c:Node{name:'c'})-[:HAS]->(d:Node{name:'d'})");
            session.run("Match (n:Node{name:'c'}) Create(n)-[:HAS]->(c:Node{name:'e'})");
            session.run("CALL BIT.createTreeIndex(0,'HAS')");
            boolean r0 = session.run("match (e:Node{name:'e'}),(d:Node{name:'d'}),(c:Node{name:'c'})" +
                    "return BIT.commonAncestor(e,d) = c").next().get(0).asBoolean();
            Assertions.assertTrue(r0);
            boolean r1 = session.run("match (b:Node{name:'b'}),(d:Node{name:'d'}),(r:Node{name:'root'})" +
                    "return BIT.commonAncestor(b,d) = r").single().get(0).asBoolean();
            Assertions.assertTrue(r1);

        }
    }

    @Test
    public void allDescendants(){
        // In a try-block, to make sure we close the driver and session after the test
        try(Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
            Session session = driver.session())
        {
            /*
               root - A -B
                    - C -D
                        -E
             */
            session.run("Create (n:Node{name:'root'})-[:HAS]->(a:Node{name:'a'})-[:HAS]->(b:Node{name:'b'})");
            session.run("Match (n:Node{name:'root'}) Create(n)-[:HAS]->(c:Node{name:'c'})-[:HAS]->(d:Node{name:'d'})");
            session.run("Match (n:Node{name:'c'}) Create(n)-[:HAS]->(c:Node{name:'e'})");
            session.run("CALL BIT.createTreeIndex(0,'HAS')");
            int r0 = (int) session.run("match (c:Node{name:'c'})" +
                    "return BIT.allDescendants(c)").single().get(0).asList().size();
            Assertions.assertEquals(2, r0);
            int r1 = (int) session.run("match (r:Node{name:'root'})" +
                    "return BIT.allDescendants(r)").single().get(0).asList().size();
            Assertions.assertEquals(5, r1);

        }
    }
}
