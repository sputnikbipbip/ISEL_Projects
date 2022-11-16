package series.serie1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static series.serie1.Arrays.deleteMin;


public class DeleteMinTest {

	private static final int[] reverseSortedArrayWithTreeElement = { 3, 2, 1 };

	@Test
	public void deleteMinTest_onEmptyHeapAndEmptyArray(){
		assertEquals( 0, deleteMin( new int[0], 0 ) );
	}

	@Test
	public void deleteMinTest_onEmptyHeap(){
		int[] maxHeap = java.util.Arrays.copyOf(reverseSortedArrayWithTreeElement, reverseSortedArrayWithTreeElement.length);
		assertEquals( 0, deleteMin( maxHeap, 0 ) );
		assertArrayEquals( reverseSortedArrayWithTreeElement, maxHeap );
	}

	@Test
	public void deleteMinTest_onHeapWithOneElement() {
		int[] maxHeap = { 1 };
		assertEquals(0, deleteMin( maxHeap, 1 ));
		assertEquals( 1, maxHeap[0] );
	}

	@Test
	public void deleteMinTest_withMinOnLastElement() {
		int sizeHeap= reverseSortedArrayWithTreeElement.length;
		int[] actuals = java.util.Arrays.copyOf(reverseSortedArrayWithTreeElement, sizeHeap);
		for ( ; sizeHeap > 0 ; --sizeHeap ) {
			assertEquals( sizeHeap-1, deleteMin( actuals, sizeHeap ) );
			assertArrayEquals( reverseSortedArrayWithTreeElement, actuals );
		}
	}

	@Test
	public void deleteMinTest_withMinNotInLastElement() {
		int[] maxHeap={ 3, 1, 2 };
		assertEquals( maxHeap.length-1, deleteMin( maxHeap, maxHeap.length ) );
		assertEquals( 3, maxHeap[0] );
		assertEquals( 2, maxHeap[1] );
		assertEquals( 2, maxHeap[2] );
	}

	@Test
	public void deleteMinTest_withMoveElement() {
		int[] maxHeap=      { 20, 15, 3, 10, 9, 2, 1, 6, 5, 8, 7 };
		int[][] expecteds={ { 20, 15, 7, 10, 9, 2, 3, 6, 5, 8, 7 },
				{ 20, 15, 8, 10, 9, 7, 3, 6, 5, 8, 7 },
				{ 20, 15, 8, 10, 9, 7, 5, 6, 5, 8, 7 }
		};
		for (int index= 0, sizeHeap = maxHeap.length; index < 3; ++index, --sizeHeap) {
			assertEquals( sizeHeap-1, deleteMin( maxHeap, sizeHeap) );
			assertArrayEquals( expecteds[index], maxHeap );
		}
	}
}
