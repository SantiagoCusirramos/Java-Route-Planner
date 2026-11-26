package datastructures.stack;

import exceptions.ExceptionIsEmpty;

public interface Stack<E> {
    void push(E e);
    E pop()throws ExceptionIsEmpty;
    E top()throws ExceptionIsEmpty;
    boolean isEmpty();
}