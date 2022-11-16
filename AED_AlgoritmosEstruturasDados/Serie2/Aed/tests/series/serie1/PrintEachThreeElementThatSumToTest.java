package series.serie1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static series.serie1.Arrays.printEachThreeElementsThatSumTo;

public class PrintEachThreeElementThatSumToTest {


    @Test
    public void test_printEachThreeElementThatSumTo_in_empty_subsequence() {
        int[] empty = {};
        assertEquals(0, printEachThreeElementsThatSumTo(empty, 0, empty.length - 1, 0));
    }

    @Test
    public void test_printEachThreeElementThatSumTo_in_singleton_subsequence() {
        int[] empty = {1};
        assertEquals(0, printEachThreeElementsThatSumTo(empty, 0, empty.length - 1, 1));

    }

    @Test
    public void test_printEachThreeElementThatSumTo_in_unsortedConsecutive_subsequence() {
        int[] array = {5, 12, 3, 4, 9, 2, 1, 8, 7, 10, 6, 11};
        assertEquals(7, printEachThreeElementsThatSumTo(array, 0, array.length - 1, 12));
        assertEquals(5, printEachThreeElementsThatSumTo(array, 1, array.length - 2, 12));
    }

    @Test
    public void test_printEachThreeElementThatSumTo_in_sortConsecutive_subsequence() {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        assertEquals(7, printEachThreeElementsThatSumTo(array, 0, array.length - 1, 12));
        assertEquals(3, printEachThreeElementsThatSumTo(array, 1, array.length - 2, 12));

    }

    @Test
    public void test_printEachThreeElementThatSumTo_in_unsortNonConsecutive_subsequence() {
        int[] array = {9, 17, 4, 7, 1, 15, 11, 12, 2, 8};
        assertEquals(2, printEachThreeElementsThatSumTo(array, 0, array.length - 1, 12));
        assertEquals(1, printEachThreeElementsThatSumTo(array, 1, array.length - 2, 12));
    }

    @Test
    public void test_printEachThreeElementThatSumTo_in_sortNonConsecutive_subsequence() {
        int[] array = {1, 2, 4, 7, 8, 9, 11, 12, 15, 17};
        assertEquals(2, printEachThreeElementsThatSumTo(array, 0, array.length - 1, 12));
        assertEquals(0, printEachThreeElementsThatSumTo(array, 1, array.length - 2, 12));

    }


}
