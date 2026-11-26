package graph;

import exceptions.ExceptionIsEmpty;
import datastructures.linkedlist.LinkedList;
import datastructures.stack.LinkedStack;
import datastructures.linkedlist.Node;

public class Graph<E> {
    public LinkedList<Vertex<E>> listVertex; // lista de vertices, estos son los que bajan
    protected boolean isDirected; // dirigido o no
    protected boolean isWeighted; // peso o no

    public Graph(boolean isDirected, boolean isWeighted) {
        listVertex = new LinkedList<Vertex<E>>();
        this.isDirected = isDirected;
        this.isWeighted = isWeighted;
    }

    public void insertVertex(E data) {
        if(data == null) {
            return;
        }

        Vertex<E> newVertex = new Vertex<E>(data);

        if(!listVertex.search(newVertex)){
            listVertex.insertLast(newVertex);
        }
    }

    public void insertEdge(E verOri, E verDes){
        insertEdge(verOri, verDes, -1);
    }

    public void insertEdge(E verOri, E verDes, int weight) { // origin ' destiantion
        if(verOri == null || verDes == null){
            return;
        }

        Vertex<E> vertexOri  = findVertex(verOri);
        Vertex<E> vertexDes  = findVertex(verDes);

        if(vertexOri == null || vertexDes == null) {
            return;
        }
        if(searchEdge(verOri, verDes, -1)){ // no se permiten aristas repetidas, sin importar el peso
            return;
        }

        if(isWeighted){ // Aqui el peso esta de mas, es mas, genero redundancia con mi constructor, pero venga, ya vemos como hacerlo
            Edge<E> edge = new Edge<E>(vertexDes, weight);
            vertexOri.getListAdj().insertLast(edge);
        } else {
            Edge<E> edge = new Edge<E>(vertexDes);
            vertexOri.getListAdj().insertLast(edge);
        }

        if(!isDirected){
            Edge<E> edgeInverse = new Edge<E>(vertexOri, weight);
            vertexDes.getListAdj().insertLast(edgeInverse);
        }
    }

    public boolean searchVertex(E data){
        if(data == null){
            return false;
        }
        if(findVertex(data) == null){
            return false;
        } else {
            return true;
        }
    }

    public Vertex<E> findVertex(E data) {
        Node<Vertex<E>> current = listVertex.getHead();

        while(current != null) {
            if(current.getData().getData().equals(data)) {
                return current.getData();
            }
            current = current.getNext();
        }
        return null;
    }

    public boolean searchEdge(E data1, E data2, int weight) {
        // si es necesario :v
        if(data1 == null || data2 == null){
            return false;
        }

        Vertex<E> vertex1  = findVertex(data1);
        Vertex<E> vertex2  = findVertex(data2);

        if(vertex1 == null || vertex2 == null) {
            return false;
        }

        boolean found = searchAdjacencyList(vertex1, vertex2, weight);

        if(!isDirected && !found) { // xd, lo mismo nomas, preguntar, justificacion, si es necesario ya que puede o no ser directo
            found = searchAdjacencyList(vertex2, vertex1, weight);
        }

        return found;
    }

    public boolean removeVertex(E data) { // ya fue ya xd ' re mareado
        if (data == null) {
            return false;
        }

        Vertex<E> vertexToRemove = findVertex(data);

        if (vertexToRemove == null) {
            return false;
        }

        if (isDirected) {
            vertexToRemove.getListAdj().destroyList();

            // me voy a marear horrible xd
            // aca tomamos la lista de los vertices, esto para entrar a cada uno y poder borrar las referencias

            Node<Vertex<E>> currentVertex = listVertex.getHead();

            while (currentVertex != null) {
                Vertex<E> edge = currentVertex.getData();
                Node<Edge<E>> edgeNode = edge.getListAdj().getHead();

                while (edgeNode != null) { // simple, recorremos la lista de aristas hasta hallar el vertice respectivo y borramos
                    if (edgeNode.getData().getRefDest().equals(vertexToRemove)) {
                        edge.getListAdj().removeNode(edgeNode.getData());
                        break;
                    }

                    edgeNode = edgeNode.getNext();
                }
                currentVertex = currentVertex.getNext(); // obvio o,o - el dato puede que no este en un solo vertex, puede q este en varios, razon de la iteracion
            }

        } else {
            Node<Edge<E>> edgeNode = vertexToRemove.getListAdj().getHead();

            while (edgeNode != null) {
                Vertex<E>  vertexConnected = edgeNode.getData().getRefDest(); // aca obtenemos la arista sigueinte
                vertexConnected.getListAdj().removeNode(new Edge<E>(vertexToRemove)); // aca optenemos la lista de adj en relacion a la arista y borramos
                edgeNode = edgeNode.getNext();

            }
        }

        return listVertex.removeNode(vertexToRemove); // borramos pe
    }

    public boolean removeEdge(E verOri, E verDes) {
        if (verOri == null || verDes == null) {
            return false;
        }

        Vertex<E> vertexOri = findVertex(verOri);
        Vertex<E> vertexDes = findVertex(verDes);

        if (vertexOri == null || vertexDes == null) {
            return false;
        }

        // dirigido ori -> des
        boolean removed = vertexOri.getListAdj().removeNode(new Edge<>(vertexDes));

        // no dirigido -> eliminar des -> ori
        if (!isDirected && removed) {
            removed = removed || vertexDes.getListAdj().removeNode(new Edge<>(vertexOri));
        }

        return removed;

    }

    // aqui no es necesario preguntar, esto si lo veo necesario, es como un metodo extra entre comillas
    private boolean searchAdjacencyList(Vertex<E> item1, Vertex<E> item2, int weight) {

        LinkedList<Edge<E>> adjList1 = item1.getListAdj();

        Node<Edge<E>> current = adjList1.getHead();

        while(current != null) {
            Edge<E> edge = current.getData();
            // weight < 0 significa "con cualquier peso"
            if(edge.getRefDest().equals(item2) && (weight < 0 || edge.getWeight() == weight)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    // este es mi metodo aux para explorar y ponerlos todos como que no han sido visitaos
    public void resetVisitedFlags() {
        Node<Vertex<E>> current = listVertex.getHead();

        while (current != null) {
            current.getData().setVisited(false);
            current = current.getNext();
        }
    }

    /**
     * Conexo: en un grafo no dirigido, todos los vértices se alcanzan entre sí.
     * En un grafo dirigido se exige conexidad fuerte: desde el primer vértice se llega a todos
     * y todos llegan al primero (si no, habría zonas a las que se entra pero no se sale).
     */
    public boolean isConexo() {
        if (listVertex.isEmpty()) {
            return true; // Un grafo vacío se considera conexo
        }

        Vertex<E> startVertex = listVertex.getHead().getData();
        int total = listVertex.length();

        if (countReachable(startVertex, false) != total) {
            return false;
        }
        return !isDirected || countReachable(startVertex, true) == total;
    }

    /** Cuenta los vértices alcanzables desde start; con reverse=true recorre las aristas al revés. */
    private int countReachable(Vertex<E> start, boolean reverse) {
        resetVisitedFlags();
        LinkedStack<Vertex<E>> pending = new LinkedStack<>();
        start.setVisited(true);
        pending.push(start);
        int count = 0;

        while (!pending.isEmpty()) {
            Vertex<E> current;
            try {
                current = pending.pop();
            } catch (ExceptionIsEmpty e) {
                break;
            }
            count++;

            LinkedList<Vertex<E>> next = reverse ? predecessors(current) : successors(current);
            Node<Vertex<E>> node = next.getHead();
            while (node != null) {
                if (!node.getData().isVisited()) {
                    node.getData().setVisited(true);
                    pending.push(node.getData());
                }
                node = node.getNext();
            }
        }
        return count;
    }

    /** Vértices a los que sale una arista desde v. */
    private LinkedList<Vertex<E>> successors(Vertex<E> v) {
        LinkedList<Vertex<E>> result = new LinkedList<>();
        Node<Edge<E>> edgeNode = v.getListAdj().getHead();
        while (edgeNode != null) {
            result.insertLast(edgeNode.getData().getRefDest());
            edgeNode = edgeNode.getNext();
        }
        return result;
    }

    /** Vértices desde los que entra una arista a v. */
    private LinkedList<Vertex<E>> predecessors(Vertex<E> v) {
        LinkedList<Vertex<E>> result = new LinkedList<>();
        Node<Vertex<E>> current = listVertex.getHead();
        while (current != null) {
            Node<Edge<E>> edgeNode = current.getData().getListAdj().getHead();
            while (edgeNode != null) {
                if (edgeNode.getData().getRefDest().equals(v)) {
                    result.insertLast(current.getData());
                    break;
                }
                edgeNode = edgeNode.getNext();
            }
            current = current.getNext();
        }
        return result;
    }


    /** Grado de salida: cantidad de aristas que salen del vértice. */
    public int gradoNodo(E data) {
        Vertex<E> vertex = findVertex(data);
        if (vertex == null) {
            return -1;
        }

        return vertex.getListAdj().length();
    }

    /** Grado de entrada: cantidad de aristas que llegan al vértice. */
    public int gradoEntrada(E data) {
        Vertex<E> vertex = findVertex(data);
        if (vertex == null) {
            return -1;
        }
        return isDirected ? predecessors(vertex).length() : vertex.getListAdj().length();
    }

    /**
     * Indica si el vértice forma parte de un ciclo.
     * Dirigido: existe un camino que sale de startVertex y vuelve a él.
     * No dirigido: hay un ciclo (de 3 o más vértices) en su componente.
     */
    public boolean esCiclo(Vertex<E> startVertex) {
        if (startVertex == null) {
            return false;
        }

        resetVisitedFlags();
        if (isDirected) {
            return returnsTo(startVertex, startVertex);
        }
        return hasUndirectedCycle(startVertex, null);
    }

    private boolean returnsTo(Vertex<E> current, Vertex<E> target) {
        Node<Edge<E>> edgeNode = current.getListAdj().getHead();
        while (edgeNode != null) {
            Vertex<E> neighbor = edgeNode.getData().getRefDest();
            if (neighbor.equals(target)) {
                return true;
            }
            if (!neighbor.isVisited()) {
                neighbor.setVisited(true);
                if (returnsTo(neighbor, target)) {
                    return true;
                }
            }
            edgeNode = edgeNode.getNext();
        }
        return false;
    }

    private boolean hasUndirectedCycle(Vertex<E> current, Vertex<E> parent) {
        current.setVisited(true);
        Node<Edge<E>> edgeNode = current.getListAdj().getHead();
        while (edgeNode != null) {
            Vertex<E> neighbor = edgeNode.getData().getRefDest();
            if (!neighbor.isVisited()) {
                if (hasUndirectedCycle(neighbor, current)) {
                    return true;
                }
            } else if (!neighbor.equals(parent)) {
                return true; // vecino ya visitado que no es el padre: se cerró un ciclo
            }
            edgeNode = edgeNode.getNext();
        }
        return false;
    }

    public LinkedList<E> getVertices() {
        LinkedList<E> vertices = new LinkedList<E>();
        Node<Vertex<E>> current = listVertex.getHead();

        while (current != null) {
            vertices.insertLast(current.getData().getData());
            current = current.getNext();
        }
        return vertices;
    }

    /**
     * Componentes conexas. En un grafo dirigido se calculan ignorando el sentido de las aristas
     * (componentes débilmente conexas): dos zonas están juntas si hay calles entre ellas en algún sentido.
     */
    public LinkedList<LinkedList<E>> getConnectedComponents() {
        LinkedList<LinkedList<E>> components = new LinkedList<>();
        resetVisitedFlags();

        Node<Vertex<E>> current = listVertex.getHead();

        while (current != null) {
            Vertex<E> vertex = current.getData();
            if (!vertex.isVisited()) {
                LinkedList<E> componentData = new LinkedList<>();
                LinkedStack<Vertex<E>> pending = new LinkedStack<>();
                vertex.setVisited(true);
                pending.push(vertex);

                while (!pending.isEmpty()) {
                    Vertex<E> v;
                    try {
                        v = pending.pop();
                    } catch (ExceptionIsEmpty e) {
                        break;
                    }
                    componentData.insertLast(v.getData());

                    LinkedList<Vertex<E>> neighbors = successors(v);
                    if (isDirected) {
                        Node<Vertex<E>> in = predecessors(v).getHead();
                        while (in != null) {
                            neighbors.insertLast(in.getData());
                            in = in.getNext();
                        }
                    }
                    Node<Vertex<E>> n = neighbors.getHead();
                    while (n != null) {
                        if (!n.getData().isVisited()) {
                            n.getData().setVisited(true);
                            pending.push(n.getData());
                        }
                        n = n.getNext();
                    }
                }
                components.insertLast(componentData);
            }
            current = current.getNext();
        }

        return components;
    }

    public boolean modifyEdgeWeight(E origin, E dest, int newWeight) {
        if (!searchVertex(origin) || !searchVertex(dest)) return false;

        Vertex<E> v1 = findVertex(origin);
        Node<Edge<E>> current = v1.getListAdj().getHead();
        while (current != null) {
            if (current.getData().getRefDest().equals(findVertex(dest))) {
                current.getData().setWeight(newWeight); // asegúrate de tener un setWeight en Edge
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public void renameVertex(E oldName, E newName) {
        Vertex<E> vertex = findVertex(oldName);
        if (vertex != null) {
            vertex.setData(newName);
        }
    }

}
