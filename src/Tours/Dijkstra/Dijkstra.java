package Tours.Dijkstra;

import AdjacencyLists.Edge.Edge;
import AdjacencyLists.GraphLink.GraphLink;
import AdjacencyLists.Vertex.Vertex;
import DataStructure.LinkedList.LinkedList;
import DataStructure.Node.Node;

import java.util.HashMap;
import java.util.PriorityQueue;

public class Dijkstra {
    public static <E> LinkedList<E> shortestPath(GraphLink<E> graph, E start, E end) {
        if (graph == null || start == null || end == null) {
            return null;
        }

        Vertex<E> startVertex = graph.findVertex(start);
        Vertex<E> endVertex = graph.findVertex(end);

        if (startVertex == null || endVertex == null) {
            return null;
        }

        // 1. Inicialización
        HashMap<Vertex<E>, Integer> distances = new HashMap<>();
        HashMap<Vertex<E>, Vertex<E>> predecessors = new HashMap<>();
        PriorityQueue<VertexDistance<E>> priorityQueue = new PriorityQueue<>();

        // Todos los nodos empiezan con distancia infinita, excepto el origen
        Node<Vertex<E>> current = graph.listVertex.getHead();
        while (current != null) {
            distances.put(current.getData(), Integer.MAX_VALUE);
            predecessors.put(current.getData(), null);
            current = current.getNext();
        }
        distances.put(startVertex, 0);
        priorityQueue.add(new VertexDistance<>(startVertex, 0));

        // 2. Procesamiento
        while (!priorityQueue.isEmpty()) {
            VertexDistance<E> currentVertexDistance = priorityQueue.poll();
            Vertex<E> currentVertex = currentVertexDistance.vertex;

            // Si llegamos al destino, terminamos
            if (currentVertex.equals(endVertex)) {
                break;
            }

            // Explorar vecinos
            Node<Edge<E>> edgeNode = currentVertex.getListAdj().getHead();
            while (edgeNode != null) {
                Vertex<E> neighbor = edgeNode.getData().getRefDest();
                int edgeWeight = edgeNode.getData().getWeight();
                int newDistance = distances.get(currentVertex) + edgeWeight;

                // Si encontramos un camino más corto
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, currentVertex);
                    priorityQueue.add(new VertexDistance<>(neighbor, newDistance));
                }
                edgeNode = edgeNode.getNext();
            }
        }

        // 3. Reconstruir el camino
        return reconstructPath(predecessors, endVertex);
    }

    private static <E> LinkedList<E> reconstructPath(HashMap<Vertex<E>, Vertex<E>> predecessors, Vertex<E> endVertex) {
        LinkedList<E> path = new LinkedList<>();
        Vertex<E> current = endVertex;

        while (current != null) {
            path.insertFirst(current.getData());
            current = predecessors.get(current);
        }

        return path;
    }
}


class VertexDistance<E> implements Comparable<VertexDistance<E>> {
    Vertex<E> vertex;
    int distance;

    public VertexDistance(Vertex<E> vertex, int distance) {
        this.vertex = vertex;
        this.distance = distance;
    }

    @Override
    public int compareTo(VertexDistance<E> other) {
        return Integer.compare(this.distance, other.distance);
    }
}