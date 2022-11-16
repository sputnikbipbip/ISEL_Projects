package series.serie2;

import java.util.Comparator;

public class ListUtils {

    public static <E> E getKBiggest(Node<E> list, int k, Comparator<E> cmp) {
        if(list == null) return null;
        if(k <= 0) return null;
        Node <E> front = list;
        Node <E> back = list;
        int counter = 1;
        while (counter < k - 1) {
            if(front.next == null)
                return null;
            front = front.next;
            ++counter;
        }
        while (front.next != null) {
            front = front.next;
            if(front.next == null)
                return back.value;
            back = back.next;
        }
        return null;
    }

    public static <E> Node<E> merge(Node<E>[] lists, Comparator<E> cmp) {
        Node<E> res = new Node<E>();
        Node<E> aux = null;
        res.next = res;
        res.previous=res;
        int size = lists.length;
        int counter = 0;
        int support = counter;
        while(counter < lists.length){
            if(lists[counter] == null) {
                ++counter;
                size--;
            }else {
                lists[support++] = lists[counter++];
            }
        }
        buildMinHeap(lists,size,cmp);
        while(size > 0) {
            aux = lists[0];
            lists[0] = lists[0].next;
            aux.next = res;
            aux.previous = res.previous;
            res.previous.next = aux;
            res.previous = aux;
            if(lists[0] == null) {
                lists[0] = lists[--size];
            }
            minHeapify(lists,0, size, cmp);
        }
        return res;
    }

    public static <E> void buildMinHeap(Node<E>[] h, int n, Comparator<E> cmp) {
        for (int i = n / 2 - 1; i >= 0; i--) {
            if(h[i]!=null) minHeapify(h, i, n, cmp);
        }
    }

    public static <E> void minHeapify(Node<E>[] h, int i, int n, Comparator<E> cmp) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        int smallest;
        if (l < n && cmp.compare(h[l].value, h[i].value) < 0)
            smallest = l;
        else smallest = i;
        if (r < n && cmp.compare(h[r].value, h[smallest].value) < 0)
            smallest = r;
        if (smallest != i) {
            exchange(h, i, smallest);
            minHeapify(h, smallest, n, cmp);
        }
    }

    private static <E> void exchange(Node<E>[] h, int i, int j) {
        Node<E> aux = h[i];
        h[i] = h[j];
        h[j] = aux;
    }
}



