package Tours.BFS;

import AdjacencyLists.Edge.Edge;
import AdjacencyLists.GraphLink.GraphLink;
import AdjacencyLists.Vertex.Vertex;
import DataStructure.LinkedList.LinkedList;
import DataStructure.Queue.LinkedQueue;
import DataStructure.Node.Node;
import Exceptions.ExceptionIsEmpty;

public class BFS<E> {
    
    public void bfs(GraphLink<E> graph, E startData) throws ExceptionIsEmpty {
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
    
    public LinkedList<E> bfsPath(GraphLink<E> graph, E startData, E endData) throws ExceptionIsEmpty {
        Vertex<E> start = getValidVertex(graph, startData);
        Vertex<E> end = graph.findVertex(endData);
        
        if (start == null || end == null || endData == null) return null;
        
        LinkedQueue<Vertex<E>> queue = new LinkedQueue<>();
        LinkedList<Vertex<E>> parents = new LinkedList<>();
        
        queue.enqueue(start);
        start.setVisited(true);
        parents.insertLast(null);
        
        while (!queue.isEmpty()) {
            Vertex<E> current = queue.dequeue();
            Vertex<E> currentParent = parents.removeFirst();
            
            if (current.equals(end)) {
                return buildPath(current, currentParent, parents);
            }
            
            enqueueNeighborsWithParents(current, queue, parents);
        }
        
        return null;
    }
    
    private Vertex<E> getValidVertex(GraphLink<E> graph, E data) {
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
    
    private void enqueueNeighborsWithParents(Vertex<E> vertex, LinkedQueue<Vertex<E>> queue, LinkedList<Vertex<E>> parents) {
        Node<Edge<E>> edgeNode = vertex.getListAdj().getHead();
        
        while (edgeNode != null) {
            Vertex<E> neighbor = edgeNode.getData().getRefDest();
            if (!neighbor.isVisited()) {
                neighbor.setVisited(true);
                queue.enqueue(neighbor);
                parents.insertLast(vertex);
            }
            edgeNode = edgeNode.getNext();
        }
    }
    
    private LinkedList<E> buildPath(Vertex<E> target, Vertex<E> targetParent, LinkedList<Vertex<E>> parents) {
        LinkedList<E> path = new LinkedList<>();
        LinkedList<Vertex<E>> pathVertices = new LinkedList<>();
        
        // Reconstruir la cadena de vértices desde el destino
        Vertex<E> current = target;
        Vertex<E> parent = targetParent;
        
        while (current != null) {
            pathVertices.insertFirst(current);
            current = parent;
            parent = (current != null) ? findParentInList(current, parents) : null;
        }
        
        // Convertir vértices a datos
        Node<Vertex<E>> node = pathVertices.getHead();
        while (node != null) {
            path.insertLast(node.getData().getData());
            node = node.getNext();
        }
        
        return path;
    }
    
    private Vertex<E> findParentInList(Vertex<E> child, LinkedList<Vertex<E>> parents) {
        Node<Vertex<E>> node = parents.getHead();
        while (node != null) {
            if (node.getData() != null && node.getData().equals(child)) {
                return node.getData();
            }
            node = node.getNext();
        }
        return null;
    }
}
