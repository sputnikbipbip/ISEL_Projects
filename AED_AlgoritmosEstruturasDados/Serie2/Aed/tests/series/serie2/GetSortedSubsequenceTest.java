package series.serie2;

import static org.junit.jupiter.api.Assertions.*;
import static series.serie2.Iterables.getSortedSubsequence;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class GetSortedSubsequenceTest {
		static final Iterable<Integer> SEmpty = Collections.<Integer>emptyList();
		static final Iterable<Integer> SSingle = Collections.singletonList(5);
		static final List<Integer> SIncreasing = Arrays.asList( 3, 5, 7, 10, 12 ) ;
		static final List<Integer> SDecreasing =  Arrays.asList( 12, 10, 7, 5, 3 );
		static final List<Integer> SEquals = Arrays.asList( 5, 5, 5, 5, 5 ) ;
		static final List<Integer> SSome= Arrays.asList( 1, 3, 2, 5, 7, 6, 4, 8 );
		static Iterable<Integer> emptyIterable=() -> {
			return new Iterator<>() {
				@Override
				public boolean hasNext() {
					return false;
				}
				@Override
				public Integer next() {
					throw new NoSuchElementException();
				}
			};
		};
		
		@Test
		public void getSortedSubsequence_withEmptySequences(){
			assertIterableEquals(emptyIterable
			,getSortedSubsequence(SEmpty, 5));
		}

		@Test 
		public void getSortedSubsequence_withOneElementSequence(){
			Iterable<Integer> result=getSortedSubsequence(SSingle, 5);
			assertTrue(result.iterator().hasNext());
			assertIterableEquals(SSingle, result);
			Iterator<Integer> it=result.iterator();
			assertTrue(it.hasNext());
			assertTrue(it.hasNext());
			
		}

		@Test 
		public void getSortedSubsequence_withIncreasingSequence(){
			List<Integer> subList=SIncreasing.subList(1,1);
			for(int i=1; i<SIncreasing.size();i++){
				assertIterableEquals(subList, getSortedSubsequence(subList, 5));
			}
		}


		@Test 
		public void getSortedSubsequence_withDecreasingSequence(){
			List<Integer> subList=SDecreasing.subList(3,3);
			for(int i=0; i<SDecreasing.size()-1;i++){
				assertIterableEquals(subList, getSortedSubsequence(subList, 5));
			}
		}

		@Test 
		public void getSortedSubsequence_withEqualsSequence(){
			List<Integer> subList=null;
			for(int i=0; i<SEquals.size();i++){
				subList=SEquals.subList(i, SEquals.size());
				assertIterableEquals(emptyIterable,getSortedSubsequence(subList, 6));
			}
			for(int i=0; i<SEquals.size();i++){
				subList=SEquals.subList(i, SEquals.size());
				assertIterableEquals(subList, getSortedSubsequence(subList, 5));
			}
		}

		@Test
    	public void getUnSortedSequence_withSomeSequence(){
			List<Integer> someSubsequence = Arrays.asList(5, 7, 8);
			assertIterableEquals(someSubsequence, getSortedSubsequence(SSome, 5));
		}

    	@Test
    	public void getSortedSequence_nextInEmptySequence(){
		    assertThrows(NoSuchElementException.class, ()->getSortedSubsequence(SEmpty, 5).iterator().next());
		}

		@Test
		public void getValuesBetween_NextWithNoElements(){
			Iterator<Integer> it=  getSortedSubsequence(SSingle, 5).iterator();
			assertEquals(5, it.next().intValue());
        	assertThrows(NoSuchElementException.class,() -> it.next());
		}

		@Test
		public void getValuesBetween_RemoveElements(){
			Iterator<Integer> it= getSortedSubsequence(SSingle, 5).iterator();
			it.next();
        	assertThrows(UnsupportedOperationException.class,() -> it.remove());
		}



}
