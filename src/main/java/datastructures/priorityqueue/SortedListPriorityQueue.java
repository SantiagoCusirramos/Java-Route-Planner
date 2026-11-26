package datastructures.priorityqueue;

import datastructures.linkedlist.Node;
import exceptions.ExceptionIsEmpty;

public class SortedListPriorityQueue<E extends Comparable<E>> implements PriorityQueue<E> {

    private Node<E> first;
    private Node<E> last;

    public SortedListPriorityQueue() {
        this.first = null;
        this.last = null;
    }

    public void enqueue(E item) {
        Node<E> newNode = new Node<>(item);

        if (isEmpty()) {
            first = last = newNode;
            return;
        }

        if (item.compareTo(first.getData()) < 0) {
            newNode.setNext(first);
            first = newNode;
            return;
        }

        Node<E> currentNode = first;
        while (currentNode.getNext() != null && item.compareTo(currentNode.getNext().getData()) >= 0) {
            currentNode = currentNode.getNext();
        }

        newNode.setNext(currentNode.getNext());
        currentNode.setNext(newNode);

        if (newNode.getNext() == null) {
            last = newNode;
        }
    }

    public E dequeue() throws ExceptionIsEmpty {
        if (isEmpty()) {
            throw new ExceptionIsEmpty();
        }

        E item = first.getData();
        first = first.getNext();

        if (first == null) {
            last = null;
        }
        return item;
    }

    public E front() throws ExceptionIsEmpty {
        if(isEmpty()){
            throw new ExceptionIsEmpty();
        } else {
            return first.getData();
        }
    }

    public E back() throws ExceptionIsEmpty {
        if(isEmpty()){
            throw new ExceptionIsEmpty();
        } else {
            return last.getData();
        }
    }

    public boolean isEmpty() {
        return first == null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<E> current = first;
        while (current != null) {
            sb.append(current.getData().toString());
            if (current.getNext() != null)
                sb.append(" -> ");
            current = current.getNext();
        }
        return sb.toString();
    }
}
