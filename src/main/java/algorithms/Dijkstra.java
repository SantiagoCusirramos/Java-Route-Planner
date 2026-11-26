package algorithms;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import datastructures.linkedlist.LinkedList;
import datastructures.linkedlist.Node;
import exceptions.ExceptionIsEmpty;
import datastructures.priorityqueue.SortedListPriorityQueue;

public class Dijkstra {

    public static <E> LinkedList<E> shortestPath(Graph<E> graph, E start, E end) throws ExceptionIsEmpty {
        if (graph == null || start == null || end == null) {
            return null;
        }

        Vertex<E> startVertex = graph.findVertex(start);
        Vertex<E> endVertex = graph.findVertex(end);

        if (startVertex == null || endVertex == null) {
            return null;
        }

        LinkedList<VertexDistance<E>> distanceList = new LinkedList<>();
        LinkedList<VertexPredecessors<E>> predecessorsList = new LinkedList<>();
        SortedListPriorityQueue priorityQueue = new SortedListPriorityQueue<>();

        Node<Vertex<E>> current = graph.listVertex.getHead();

        while (current != null) {
            putDistance(distanceList, current.getData(), Integer.MAX_VALUE);
            putPredecessor(predecessorsList, current.getData(), null);
            current = current.getNext();
        }

        putDistance(distanceList, startVertex, 0);
        priorityQueue.enqueue(new VertexDistance<>(startVertex, 0));

        while (!priorityQueue.isEmpty()) {
            VertexDistance<E> currentVertexDistance = (VertexDistance<E>) priorityQueue.dequeue();
            Vertex<E> currentVertex = currentVertexDistance.getVertex();

            if (currentVertex.equals(endVertex)) {
                break;
            }

            Node<Edge<E>> edgeNode = currentVertex.getListAdj().getHead();

            while (edgeNode != null) {
                Vertex<E> neighbor = edgeNode.getData().getRefDest();
                int edgeWeight = edgeNode.getData().getWeight();
                int currentDistance = getDistance(distanceList, currentVertex);
                int newDistance = currentDistance + edgeWeight;

                if (newDistance < getDistance(distanceList, neighbor)) {
                    putDistance(distanceList, neighbor, newDistance);
                    putPredecessor(predecessorsList, neighbor, currentVertex);
                    priorityQueue.enqueue(new VertexDistance<>(neighbor, newDistance));
                }
                edgeNode = edgeNode.getNext();
            }
        }

        return reconstructPath(predecessorsList, endVertex);
    }

    private static <E> void putDistance(LinkedList<VertexDistance<E>> distanceList, Vertex<E> vertex, int distance) {
        Node<VertexDistance<E>> current = distanceList.getHead();
        while (current != null) {
            if (current.getData().getVertex().equals(vertex)) {
                current.getData().setDistance(distance);
                return;
            }
            current = current.getNext();
        }
        distanceList.insertLast(new VertexDistance<>(vertex, distance));
    }

    private static <E> int getDistance(LinkedList<VertexDistance<E>> distanceList, Vertex<E> vertex) {
        Node<VertexDistance<E>> current = distanceList.getHead();
        while (current != null) {
            if (current.getData().getVertex().equals(vertex)) {
                return current.getData().getDistance();
            }
            current = current.getNext();
        }
        return Integer.MAX_VALUE;
    }

    private static <E> void putPredecessor(LinkedList<VertexPredecessors<E>> predecessorsList, Vertex<E> currentVertex, Vertex<E> previousVertex) {
        Node<VertexPredecessors<E>> current = predecessorsList.getHead();
        while (current != null) {
            if (current.getData().getCurrentVertex().equals(currentVertex)) {
                current.getData().setPreviousVertex(previousVertex);
                return;
            }
            current = current.getNext();
        }
        predecessorsList.insertLast(new VertexPredecessors<>(currentVertex, previousVertex));
    }

    private static <E> Vertex<E> getPredecessor(LinkedList<VertexPredecessors<E>> predecessorsList, Vertex<E> vertex) {
        Node<VertexPredecessors<E>> current = predecessorsList.getHead();
        while (current != null) {
            if (current.getData().getCurrentVertex().equals(vertex)) {
                return current.getData().getPreviousVertex();
            }
            current = current.getNext();
        }
        return null;
    }

    private static <E> LinkedList<E> reconstructPath(LinkedList<VertexPredecessors<E>> predecessorsList, Vertex<E> endVertex) {
        LinkedList<E> path = new LinkedList<>();
        Vertex<E> current = endVertex;

        while (current != null) {
            path.insertFirst(current.getData());
            current = getPredecessor(predecessorsList, current);
        }

        return path;
    }
}


class VertexDistance<E> implements Comparable<VertexDistance<E>> {
    private Vertex<E> vertex;
    private int distance;

    public VertexDistance(Vertex<E> vertex, int distance) {
        this.vertex = vertex;
        this.distance = distance;
    }

    public Vertex<E> getVertex() {
        return vertex;
    }

    public int getDistance() {
        return distance;
    }

    // Setters
    public void setVertex(Vertex<E> vertex) {
        this.vertex = vertex;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(VertexDistance<E> other) {
        return Integer.compare(this.distance, other.distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        VertexDistance<?> that = (VertexDistance<?>) obj;
        return vertex != null ? vertex.equals(that.vertex) : that.vertex == null;
    }

    @Override
    public int hashCode() {
        return vertex != null ? vertex.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "VertexDistance{vertex=" + vertex + ", distance=" + distance + "}";
    }
}

class VertexPredecessors<E> {
    private Vertex<E> currentVertex;
    private Vertex<E> previousVertex;

    public VertexPredecessors(Vertex<E> currentVertex, Vertex<E> previousVertex) {
        this.currentVertex = currentVertex;
        this.previousVertex = previousVertex;
    }

    public VertexPredecessors(Vertex<E> currentVertex) {
        this.currentVertex = currentVertex;
        this.previousVertex = null;
    }

    // Getters
    public Vertex<E> getCurrentVertex() {
        return currentVertex;
    }

    public Vertex<E> getPreviousVertex() {
        return previousVertex;
    }

    // Setters
    public void setCurrentVertex(Vertex<E> currentVertex) {
        this.currentVertex = currentVertex;
    }

    public void setPreviousVertex(Vertex<E> previousVertex) {
        this.previousVertex = previousVertex;
    }

    public boolean hasPredecessor() {
        return previousVertex != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        VertexPredecessors<?> that = (VertexPredecessors<?>) obj;
        return currentVertex != null ? currentVertex.equals(that.currentVertex) : that.currentVertex == null;
    }

    @Override
    public int hashCode() {
        return currentVertex != null ? currentVertex.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "VertexPredecessors{current=" + currentVertex + ", previous=" + previousVertex + "}";
    }
}