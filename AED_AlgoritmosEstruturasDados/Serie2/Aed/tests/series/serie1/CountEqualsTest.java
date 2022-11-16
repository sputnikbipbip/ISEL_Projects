package series.serie1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static series.serie1.Arrays.countEquals;
//import static org.junit.Assert.*;

public class CountEqualsTest {

    @Test
    public void countEquals_OnBothEmptyArrays() {
        int[] v1 = new int[0];
        int[] v2 = new int[0];
        int count = countEquals(v1, 0, -1, v2, 0, -1);
        assertEquals(0, count);
    }
}