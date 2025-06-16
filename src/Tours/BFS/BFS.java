package Tours.BFS;

import AdjacencyLists.Edge.Edge;
import AdjacencyLists.GraphLink.GraphLink;
import AdjacencyLists.Vertex.Vertex;
import DataStructure.LinkedList.LinkedList;
import DataStructure.Queue.LinkedQueue;
import Exceptions.ExceptionIsEmpty;

import DataStructure.Node.Node;

public class BFS<E> {
    public void bfs(GraphLink<E> graph, E startData) throws ExceptionIsEmpty {
        Vertex<E> startVertex = validateAndGetStartVertex(graph, startData);
        if (startVertex == null) {
            return;
        }
        
        performBFSTraversal(startVertex);
    }

    public LinkedList<E> bfsPath(GraphLink<E> graph, E startData, E endData) throws ExceptionIsEmpty {
        PathSearchResult pathResult = validateAndInitializePathSearch(graph, startData, endData);
        if (pathResult == null) {
            return null;
        }
        
        boolean pathFound = searchForPath(pathResult);
        
        return pathFound ? reconstructPath(pathResult.vertices, pathResult.parents, pathResult.endVertex) : null;
    }
    

    private Vertex<E> validateAndGetStartVertex(GraphLink<E> graph, E startData) {
        if (startData == null) {
            return null;
        }
        
        Vertex<E> startVertex = graph.findVertex(startData);
        if (startVertex == null) {
            return null;
        }
        
        graph.resetVisitedFlags();
        return startVertex;
    }
    
    /**
     * Realiza el recorrido BFS e imprime los nodos visitados
     */
    private void performBFSTraversal(Vertex<E> startVertex) throws ExceptionIsEmpty {
        LinkedQueue<Vertex<E>> queue = initializeQueue(startVertex);
        
        while (!queue.isEmpty()) {
            Vertex<E> currentVertex = queue.dequeue();
            System.out.print(currentVertex.getData() + " ");
            
            exploreNeighbors(currentVertex, queue);
        }
    }
    
    /**
     * Inicializa la cola con el vértice inicial
     */
    private LinkedQueue<Vertex<E>> initializeQueue(Vertex<E> startVertex) {
        LinkedQueue<Vertex<E>> queue = new LinkedQueue<>();
        queue.enqueue(startVertex);
        startVertex.setVisited(true);
        return queue;
    }
    
    
    private void exploreNeighbors(Vertex<E> vertex, LinkedQueue<Vertex<E>> queue) {
        DataStructure.Node.Node<Edge<E>> edgeNode = vertex.getListAdj().getHead();
        
        while (edgeNode != null) {
            Vertex<E> neighbor = edgeNode.getData().getRefDest();
            
            if (!neighbor.isVisited()) {
                neighbor.setVisited(true);
                queue.enqueue(neighbor);
            }
            
            edgeNode = edgeNode.getNext();
        }
    }
    
    private PathSearchResult validateAndInitializePathSearch(GraphLink<E> graph, E startData, E endData) {
        if (startData == null || endData == null) {
            return null;
        }
        
        Vertex<E> startVertex = graph.findVertex(startData);
        Vertex<E> endVertex = graph.findVertex(endData);
        
        if (startVertex == null || endVertex == null) {
            return null;
        }
        
        graph.resetVisitedFlags();
        
        return new PathSearchResult(startVertex, endVertex);
    }
    
    
    private boolean searchForPath(PathSearchResult result) throws ExceptionIsEmpty {
        while (!result.queue.isEmpty()) {
            Vertex<E> currentVertex = result.queue.dequeue();
            
            if (isDestinationReached(currentVertex, result.endVertex)) {
                return true;
            }
            
            exploreNeighborsForPath(currentVertex, result);
        }
        
        return false;
    }

    private boolean isDestinationReached(Vertex<E> current, Vertex<E> destination) {
        return current.equals(destination);
    }
    

    private void exploreNeighborsForPath(Vertex<E> vertex, PathSearchResult result) {
        DataStructure.Node.Node<Edge<E>> edgeNode = vertex.getListAdj().getHead();
        
        while (edgeNode != null) {
            Vertex<E> neighbor = edgeNode.getData().getRefDest();
            
            if (!neighbor.isVisited()) {
                processUnvisitedNeighbor(neighbor, vertex, result);
            }
            
            edgeNode = edgeNode.getNext();
        }
    }
    
    private void processUnvisitedNeighbor(Vertex<E> neighbor, Vertex<E> parent, PathSearchResult result) {
        neighbor.setVisited(true);
        result.queue.enqueue(neighbor);
        result.vertices.insertLast(neighbor);
        result.parents.insertLast(parent);
    }
    

    private LinkedList<E> reconstructPath(LinkedList<Vertex<E>> vertices, LinkedList<Vertex<E>> parents, Vertex<E> endVertex) {
        LinkedList<E> path = new LinkedList<>();
        Vertex<E> currentVertex = endVertex;
        
        while (currentVertex != null) {
            path.insertFirst(currentVertex.getData());
            currentVertex = findParent(vertices, parents, currentVertex);
        }
        
        return path;
    }
    
    
    private Vertex<E> findParent(LinkedList<Vertex<E>> vertices, LinkedList<Vertex<E>> parents, Vertex<E> child) {
        Node<Vertex<E>> vertexNode = vertices.getHead();
        Node<Vertex<E>> parentNode = parents.getHead();
        
        while (vertexNode != null && parentNode != null) {
            if (vertexNode.getData().equals(child)) {
                return parentNode.getData();
            }
            vertexNode = vertexNode.getNext();
            parentNode = parentNode.getNext();
        }
        
        return null;
    }
    

    private class PathSearchResult {
        final Vertex<E> startVertex;
        final Vertex<E> endVertex;
        final LinkedQueue<Vertex<E>> queue;
        final LinkedList<Vertex<E>> vertices;
        final LinkedList<Vertex<E>> parents;
        
        PathSearchResult(Vertex<E> start, Vertex<E> end) {
            this.startVertex = start;
            this.endVertex = end;
            this.queue = new LinkedQueue<>();
            this.vertices = new LinkedList<>();
            this.parents = new LinkedList<>();
            
            // Inicializar con el vértice de inicio
            queue.enqueue(start);
            start.setVisited(true);
            vertices.insertLast(start);
            parents.insertLast(null);
        }
    }
}
