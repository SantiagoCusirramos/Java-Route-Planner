package algorithms;

import graph.Edge;
import graph.Graph;
import graph.Vertex;
import datastructures.linkedlist.LinkedList;
import datastructures.queue.LinkedQueue;
import datastructures.linkedlist.Node;
import exceptions.ExceptionIsEmpty;

public class BreadthFirstSearch<E> {
    
    public void bfs(Graph<E> graph, E startData) throws ExceptionIsEmpty {
        Vertex<E> startVertex = getValidVertex(graph, startData);
        if (startVertex == null) return;
        
        LinkedQueue<Vertex<E>> queue = new LinkedQueue<>();
        queue.enqueue(startVertex);
        startVertex.setVisited(true);
        
        while (!queue.isEmpty()) {
            Vertex<E> current = queue.dequeue();
            System.out.print(current.getData() + " ");
            enqueueUnvisitedNeighbors(current, queue);
        }
    }
    
    public LinkedList<E> bfsPath(Graph<E> graph, E startData, E endData) throws ExceptionIsEmpty {
        Vertex<E> start = getValidVertex(graph, startData);
        Vertex<E> end = graph.findVertex(endData);
        
        if (start == null || end == null || endData == null) return null;
        
        LinkedQueue<Vertex<E>> queue = new LinkedQueue<>();
        // Listas paralelas: discovered[i] fue descubierto desde parentOf[i]
        LinkedList<Vertex<E>> discovered = new LinkedList<>();
        LinkedList<Vertex<E>> parentOf = new LinkedList<>();

        queue.enqueue(start);
        start.setVisited(true);
        discovered.insertLast(start);
        parentOf.insertLast(null);

        while (!queue.isEmpty()) {
            Vertex<E> current = queue.dequeue();

            if (current.equals(end)) {
                return buildPath(end, discovered, parentOf);
            }

            enqueueNeighborsWithParents(current, queue, discovered, parentOf);
        }

        return null;
    }
    
    private Vertex<E> getValidVertex(Graph<E> graph, E data) {
        if (data == null) return null;
        
        Vertex<E> vertex = graph.findVertex(data);
        if (vertex != null) graph.resetVisitedFlags();
        
        return vertex;
    }
    
    private void enqueueUnvisitedNeighbors(Vertex<E> vertex, LinkedQueue<Vertex<E>> queue) {
        Node<Edge<E>> edgeNode = vertex.getListAdj().getHead();
        
        while (edgeNode != null) {
            Vertex<E> neighbor = edgeNode.getData().getRefDest();
            if (!neighbor.isVisited()) {
                neighbor.setVisited(true);
                queue.enqueue(neighbor);
            }
            edgeNode = edgeNode.getNext();
        }
    }
    
    private void enqueueNeighborsWithParents(Vertex<E> vertex, LinkedQueue<Vertex<E>> queue,
                                            LinkedList<Vertex<E>> discovered, LinkedList<Vertex<E>> parentOf) {
        Node<Edge<E>> edgeNode = vertex.getListAdj().getHead();

        while (edgeNode != null) {
            Vertex<E> neighbor = edgeNode.getData().getRefDest();
            if (!neighbor.isVisited()) {
                neighbor.setVisited(true);
                queue.enqueue(neighbor);
                discovered.insertLast(neighbor);
                parentOf.insertLast(vertex);
            }
            edgeNode = edgeNode.getNext();
        }
    }
    
    // Reconstruye el camino yendo de cada vértice a su padre hasta llegar al origen
    private LinkedList<E> buildPath(Vertex<E> target, LinkedList<Vertex<E>> discovered, LinkedList<Vertex<E>> parentOf) {
        LinkedList<E> path = new LinkedList<>();
        Vertex<E> current = target;

        while (current != null) {
            path.insertFirst(current.getData());
            current = parentOf.getNodeAtIndex(discovered.findIndex(current));
        }

        return path;
    }
}
