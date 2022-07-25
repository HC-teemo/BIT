import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.BitSet;

class MathUtilsTest {

    @Test
    void c() {
        Assertions.assertEquals(1, MathUtils.c(1, 0));
        Assertions.assertEquals(2, MathUtils.c(2, 1));
        Assertions.assertEquals(3, MathUtils.c(3, 1));
        Assertions.assertEquals(6, MathUtils.c(4, 2));
        Assertions.assertEquals(10, MathUtils.c(5, 2));
        Assertions.assertEquals(20, MathUtils.c(6, 3));
        Assertions.assertEquals(35, MathUtils.c(7, 3));
        Assertions.assertEquals(70, MathUtils.c(8, 4));
        Assertions.assertEquals(126, MathUtils.c(9, 4));
        Assertions.assertEquals(252, MathUtils.c(10, 5));
    }

    @Test
    void CHat() {
        Assertions.assertEquals(2, MathUtils.cHat(2));
        Assertions.assertEquals(3, MathUtils.cHat(3));
        Assertions.assertEquals(4, MathUtils.cHat(4));
        Assertions.assertEquals(4, MathUtils.cHat(5));
        Assertions.assertEquals(4, MathUtils.cHat(6));
        Assertions.assertEquals(5, MathUtils.cHat(7));
        Assertions.assertEquals(5, MathUtils.cHat(9));
        Assertions.assertEquals(5, MathUtils.cHat(10));
        Assertions.assertEquals(6, MathUtils.cHat(11));
        Assertions.assertEquals(6, MathUtils.cHat(20));
        Assertions.assertEquals(11, MathUtils.cHat(253));
    }

    @Test
    void getCombinations() {
        Assertions.assertEquals(2, MathUtils.getCombinations(2).length);
        Assertions.assertEquals(3, MathUtils.getCombinations(3).length);
        Assertions.assertEquals(6, MathUtils.getCombinations(4).length);
        Assertions.assertEquals(10, MathUtils.getCombinations(5).length);
        Assertions.assertEquals(20, MathUtils.getCombinations(6).length);
        Assertions.assertEquals(35, MathUtils.getCombinations(7).length);
        Assertions.assertEquals(70, MathUtils.getCombinations(8).length);
        Assertions.assertEquals(126, MathUtils.getCombinations(9).length);
        Assertions.assertEquals(252, MathUtils.getCombinations(10).length);
    }

    @Test
    void bitSetTest() {
        BitSet bitSet = new BitSet();
        bitSet = BitSet.valueOf(new long[]{0L});
        System.out.println(bitSet);
        bitSet = BitSet.valueOf(new long[]{1L});
        System.out.println(bitSet);
    }
}