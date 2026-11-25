/**
 * One node of a doubly linked list. It holds a piece of data plus links to
 * the node before it (prev) and the node after it (next).
 *
 * Having a 'prev' link is what lets NodeList remove the LAST node in O(1)
 * instead of walking the whole list to find the second-to-last node.
 */
public class Node<T> {

    private T data;
    private Node<T> next;
    private Node<T> prev;

    public Node(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }
}
