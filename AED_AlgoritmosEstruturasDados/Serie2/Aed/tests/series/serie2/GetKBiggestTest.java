package series.serie2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static series.serie2.ListUtils.getKBiggest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;



public class GetKBiggestTest {
	
	ArrayList<Integer> aList = new ArrayList<Integer>(
		    Arrays.asList(2, 4, 6, 8, 10, 12, 14, 18, 20));

	static final Comparator<Integer> CMP_NATURAL_ORDER= Comparator.naturalOrder();
	
	@Test
	public void  getKBiggest_emptyList_kSmallerThanOne(){
		Node<Integer> list = ListUtilTest.emptyListWithoutSentinel();	
		assertEquals(null, getKBiggest(list, 0, CMP_NATURAL_ORDER));
		assertEquals(null, getKBiggest(list, -1, CMP_NATURAL_ORDER));
	}
	
	@Test
	public void  getKBiggest_emptyList_kGreaterThanListLength(){
		Node<Integer> list = ListUtilTest.emptyListWithoutSentinel();	
		assertEquals(null, getKBiggest(list, 9, CMP_NATURAL_ORDER));
	}
	
	@Test
	public void  getKBiggest_emptyList_kSmallerThanListLength(){
		Node<Integer> list = ListUtilTest.emptyListWithoutSentinel();	
		assertEquals(null, getKBiggest(list, 3, CMP_NATURAL_ORDER));
	}
	
	@Test
	public void getKBiggest_kSmallerThanOne(){
		Node<Integer> list = ListUtilTest.getListWithoutSentinel(aList);
		assertEquals(null, getKBiggest(list, 0, CMP_NATURAL_ORDER));
		assertEquals(null, getKBiggest(list, -1, CMP_NATURAL_ORDER));
	}
	
	@Test
	public void getKBiggest_kEqualsListLength(){
		Node<Integer> list = ListUtilTest.getListWithoutSentinel(aList);
		assertEquals(2, getKBiggest(list, 9, CMP_NATURAL_ORDER));
	}

	@Test
	public void getKBiggest_kGreaterThanListLength(){
		Node<Integer> list = ListUtilTest.getListWithoutSentinel(aList);
		assertEquals(null, getKBiggest(list, 10, CMP_NATURAL_ORDER));
	}
	
	@Test
	public void getKBiggest_kSmaller_ThanListLength(){
		Node<Integer> list = ListUtilTest.getListWithoutSentinel(aList);
		assertEquals(14, getKBiggest(list, 3, CMP_NATURAL_ORDER));
	}	
}


