package ch.epfl.rigel.astronomy;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterable view acting as if it were the concatenation of multiple lists. Beside being iterable, this view allows
 * random access to its elements. For performance reasons, this class stores its underlying lists without copying them.
 *
 * @implNote because of the way this class is implemented, iteration is faster than random access when reading the
 * entire list and should be preferred when possible.
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public final class ListConcatenation<T> implements Iterable<T> {  /* BONUS MODIFICATION: allows us to efficiently
                                                                                         merge lists */
    private final List<List<? extends T>> lists;  // NOTE : the wildcard is necessary to allow subclass concatenation

    /**
     * Creates a concatenation of the provided lists.
     *
     * @param lists the lists to concatenate
     */
    public ListConcatenation(List<List<? extends T>> lists) {
        this.lists = List.copyOf(lists);
    }

    /**
     * Gives an iterator over the elements of the underlying lists.
     *
     * @return an iterator over the elements of the underlying lists.
     */
    @Override
    public Iterator<T> iterator() {
        return new ConcatenationIterator<>(this);
    }

    /**
     * Gives the object stored at the given index.
     *
     * @param i the index the object would have if the underlying lists where concatenated
     * @return the object stored at the given index.
     * @implNote this method runs in O(n), where n is the number of lists that are concatenated
     */
    public T get(int i) {
        int j = i;
        for (List<? extends T> list : lists) {
            int size = list.size();
            if (j < size) return list.get(j);
            j -= size;
        }
        throw new IndexOutOfBoundsException("The provided index is too big");
    }

    private static class ConcatenationIterator<S> implements Iterator<S> {
        private final List<List<? extends S>> lists;
        private int listIndex;
        private Iterator<? extends S> currentIterator;

        private ConcatenationIterator(ListConcatenation<S> concatenation) {
            this.lists = concatenation.lists;
            setListIndex(0);
        }

        private void setListIndex(int i) {
            listIndex = i;
            currentIterator = i < lists.size() ? lists.get(i).iterator() : null;
        }

        /**
         * Informs if there is a next element.
         *
         * @return boolean indicating whether there is a next element.
         */
        @Override
        public boolean hasNext() {
            if (currentIterator == null) return false;
            return currentIterator.hasNext() || listIndex < lists.size() - 1;
        }

        /**
         * Gives the next element in the iteration.
         *
         * @return the next element in the iteration.
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public S next() {
            if (!hasNext()) throw new NoSuchElementException();
            if (!currentIterator.hasNext()) setListIndex(listIndex + 1);
            return currentIterator.next();
        }
    }
}
