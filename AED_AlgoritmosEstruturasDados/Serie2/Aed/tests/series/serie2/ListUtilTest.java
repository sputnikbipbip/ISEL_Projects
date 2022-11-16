package series.serie2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class ListUtilTest {
	
	/*
	 * For circular lists with sentinel
	 */

	public static <E> boolean isEmptyListWithSentinel(Node<E> list) {
		return list.next == list && list.previous == list;
	}

	/*
	 * For non_circular lists with no sentinel
	 */
	
	public static <E> Node<E> emptyListWithoutSentinel() {
		return null;
	}


	public static  <E> boolean isSorted(Node<E> list,Comparator<E> cmp){		
		Node<E> curr=list.next;
		if(curr==list || curr==list.previous) return true;
		while( curr.next!=list){
		    if(cmp.compare(curr.value, curr.next.value)>0) return false;
			curr=curr.next;
		}
		return true;
	}	
	
	
	 public static Node<Integer> getListWithoutSentinel(ArrayList<Integer> array){
		if(array.size()==0) return null;
			Node<Integer> list=new Node<Integer>();
			Node<Integer> cur=list;
			cur.value=array.get(0);
			for(int i=1;i<array.size();i++){
				Node<Integer> next=new Node<Integer>();
				cur.next=next;
				next.previous=cur;
				next.value=array.get(i);
				cur=cur.next;	
			}
		return list;		
	}


}
