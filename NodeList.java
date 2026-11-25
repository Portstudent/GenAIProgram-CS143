import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic, node-based, doubly linked list with head AND tail references.
 *
 * Why this list for the snake?
 * The snake's body changes only at its two ends: a new head is added each
 * move and the tail is removed. With a node-based list both are O(1).
 * With an array-based list, adding at the front means shifting every element
 * (O(n)) unless you build a circular buffer.
 *
 * Big-O summary:
 *   addFirst / addLast / removeFirst / removeLast / getFirst / getLast  O(1)
 *   size / isEmpty                                                      O(1)
 *   contains (recursive linear search)                                  O(n)
 *   iterating over the whole list                                       O(n)
 *
 * Implements Iterable so it can be used in a for-each loop.
 */
public class NodeList<T> implements Iterable<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public NodeList() {
        head = null;
        tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Adds to the front of the list. O(1) */
    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
        }
        size++;
    }

    /** Adds to the back of the list. O(1) */
    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setPrev(tail);
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    /** Removes and returns the first item. O(1) */
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        T data = head.getData();
        if (size == 1) {
            head = null;
            tail = null;
        } else {
            head = head.getNext();
            head.setPrev(null);
        }
        size--;
        return data;
    }

    /** Removes and returns the last item. O(1) thanks to the tail and prev links. */
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        T data = tail.getData();
        if (size == 1) {
            head = null;
            tail = null;
        } else {
            tail = tail.getPrev();
            tail.setNext(null);
        }
        size--;
        return data;
    }

    public T getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return head.getData();
    }

    public T getLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.getData();
    }

    /**
     * Recursive linear search. Returns true if an item equal to 'target' is in the list.
     * Public method starts the search; the private helper does the recursion.
     * O(n) time. Each recursive call adds a stack frame, so it also uses O(n) stack space.
     */
    public boolean contains(T target) {
        return containsHelper(head, target);
    }

    /**
     * Helper for contains().
     * Base case 1: ran off the end of the list -> not found.
     * Base case 2: current node holds the target -> found.
     * Recursive case: search the rest of the list starting at the next node.
     */
    private boolean containsHelper(Node<T> current, T target) {
        if (current == null) {
            return false;
        }
        if (current.getData().equals(target)) {
            return true;
        }
        return containsHelper(current.getNext(), target);
    }

    @Override
    public Iterator<T> iterator() {
        return new NodeListIterator();
    }

    /** Walks from head to tail, one node at a time. */
    private class NodeListIterator implements Iterator<T> {
        private Node<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            T data = current.getData();
            current = current.getNext();
            return data;
        }
    }
}
