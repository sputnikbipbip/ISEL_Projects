package series.serie1;

public class StringHeap {

    public static int minimum (AvaliadorDePalavra[] h) {
        return h[0].getPrefixCounter();
    }

    private static void exchange(AvaliadorDePalavra[] h, int i, int j) {
        AvaliadorDePalavra aux = h[i];
        h[i] = h[j];
        h[j] = aux;
    }

    public static void buildMinHeap(AvaliadorDePalavra[] h, int n) {
        for (int i = n / 2 - 1; i >= 0; i--)
            minHeapify(h, i, n);
    }

    public static void minHeapify(AvaliadorDePalavra[] h, int i, int n) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        int smallest;

        if (l < n && h[l].getPrefixCounter() < h[i].getPrefixCounter())
            smallest = l;
        else smallest = i;
        if (r < n && h[r].getPrefixCounter() < h[smallest].getPrefixCounter())
            smallest = r;
        if (smallest != i) {
            exchange(h, i, smallest);
            minHeapify(h, smallest, n);
        }
    }


    public static boolean organizer(AvaliadorDePalavra[] h, int i, AvaliadorDePalavra word) {

        if(i>=h.length)return false;
        int l= 2 * i + 1;
        int r= 2 * i + 2;
        boolean equalPrefix = false;

        if (h[i].getPrefixCounter()== word.getPrefixCounter()) {
            equalPrefix =h[i].getWord().equalsIgnoreCase(word.getWord());
            if(equalPrefix==false) {
                equalPrefix = organizer(h,l,word);
                if(equalPrefix==false)equalPrefix = organizer(h,r,word);
            }
            else return equalPrefix;
        }
        else if(h[i].getPrefixCounter()<word.getPrefixCounter()) {

            equalPrefix = organizer(h,l,word);
            if(equalPrefix==false)equalPrefix = organizer(h,r,word);
        }
        else return equalPrefix;

        return equalPrefix;
    }
}