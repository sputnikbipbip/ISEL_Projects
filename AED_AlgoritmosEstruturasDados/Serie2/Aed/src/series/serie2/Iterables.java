package series.serie2;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iterables {
    public static Iterable<Integer> getSortedSubsequence(final Iterable<Integer> src, int k) {
        return new Iterable<>() {
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {

                    Iterator<Integer> it = src.iterator();
                    Integer value, prev;

                    public boolean hasNext() {
                        while (value == null) {
                            if (it.hasNext()) {
                                value = it.next();
                                if (value < k) value = null;
                                else {
                                    if (prev == null) prev = value;
                                    else if (value < prev) value = null;
                                }
                            } else return false;
                        }
                        return true;
                    }

                    public Integer next() {
                        if (!hasNext())
                            throw new NoSuchElementException();
                        prev = value;
                        value = null;
                        return prev;
                    }
                };
            }
        };
    }
}
