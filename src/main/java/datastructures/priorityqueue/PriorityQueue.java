package datastructures.priorityqueue;

import exceptions.ExceptionIsEmpty;

public interface PriorityQueue<E> {
	void enqueue(E item);
	E dequeue() throws ExceptionIsEmpty;
	E front() throws ExceptionIsEmpty;
	E back() throws ExceptionIsEmpty;
	boolean isEmpty();
}
