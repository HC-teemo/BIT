import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void c() {
        Assertions.assertEquals(1, Main.c(1));
        Assertions.assertEquals(2, Main.c(2));
        Assertions.assertEquals(3, Main.c(3));
        Assertions.assertEquals(6, Main.c(4));
        Assertions.assertEquals(10, Main.c(5));
        Assertions.assertEquals(20, Main.c(6));
        Assertions.assertEquals(35, Main.c(7));
        Assertions.assertEquals(70, Main.c(8));
        Assertions.assertEquals(126, Main.c(9));
        Assertions.assertEquals(252, Main.c(10));
    }

    @Test
    void CHat() {
        Assertions.assertEquals(2, Main.cHat(2));
        Assertions.assertEquals(3, Main.cHat(3));
        Assertions.assertEquals(4, Main.cHat(4));
        Assertions.assertEquals(4, Main.cHat(5));
        Assertions.assertEquals(4, Main.cHat(6));
        Assertions.assertEquals(5, Main.cHat(7));
        Assertions.assertEquals(5, Main.cHat(9));
        Assertions.assertEquals(5, Main.cHat(10));
        Assertions.assertEquals(6, Main.cHat(11));
        Assertions.assertEquals(6, Main.cHat(20));
        Assertions.assertEquals(11, Main.cHat(253));
    }
}