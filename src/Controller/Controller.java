package Controller;

import DataStructure.Arbolito.BTree;
import AdjacencyLists.GraphLink.GraphLink;
import DataStructure.LinkedList.LinkedList;
import DataStructure.Queue.LinkedQueue;
import DataStructure.Stack.LinkedStack;
import Tours.Dijkstra.Dijkstra;
import Tours.BFS.BFS;
import Exceptions.ExceptionIsEmpty;
import Exceptions.ItemNotFound;
import Exceptions.ItemDuplicated;

public class Controller {
	private GraphLink<String> urbanGraph;
	private LinkedList<Zona> zones;
	private LinkedQueue<String> routeHistory;

	// Parámetros del sistema
	private static final int ORDEN_ARBOL = 4;  // Orden del árbol B+ para cada zona

	// Constructor principal
	public Controller() {
		this.urbanGraph = new GraphLink<>(true, true); // Grafo dirigido y ponderado
		this.zones = new LinkedList<>();
		this.routeHistory = new LinkedQueue<>();
	}

	// ==================== GESTIÓN DE ZONAS ====================
	/**
	 * Agrega una nueva zona al sistema
	 * @param zoneName Nombre de la zona a agregar
	 * @return true si se agrega correctamente, false si ya existe
	 * @throws ItemDuplicated si la zona ya existe
	 */
	public boolean addZone(String zoneName) throws ItemDuplicated {
		if (zoneName == null || zoneName.trim().isEmpty()) {
			return false;
		}

		if (urbanGraph.searchVertex(zoneName)) {
			throw new ItemDuplicated("La zona '" + zoneName + "' ya existe en el sistema");
		}

		// Agregar al grafo urbano
		urbanGraph.insertVertex(zoneName);

		// Crear zona con su árbol B+
		Zona nuevaZona = new Zona(zoneName, ORDEN_ARBOL);
		zones.insertLast(nuevaZona);

		return true;
	}

	/**
	 * Elimina una zona del sistema
	 * @param zoneName Nombre de la zona a eliminar
	 * @return true si se elimina correctamente
	 * @throws ItemNotFound si la zona no existe
	 */
	public boolean removeZone(String zoneName) throws ItemNotFound {
		if (zoneName == null || zoneName.isEmpty()) {
			throw new IllegalArgumentException("El nombre de la zona no puede estar vacío.");
		}

		if (!urbanGraph.searchVertex(zoneName)) {
			throw new ItemNotFound("La zona '" + zoneName + "' no existe en el grafo urbano.");
		}

		// Eliminar del grafo
		boolean removedFromGraph = urbanGraph.removeVertex(zoneName);

		// Eliminar de la lista de zonas
		boolean removedFromList = false;
		for (int i = 0; i < zones.length(); i++) {
			Zona zona = zones.getNodeAtIndex(i);
			if (zona.getNombre().equalsIgnoreCase(zoneName)) {
				zones.removeAtIndex(i);
				removedFromList = true;
				break;
			}
		}

		return removedFromGraph && removedFromList;
	}

	/**
	 * Verifica si una zona existe en el sistema
	 * @param zoneName Nombre de la zona a verificar
	 * @return true si la zona existe
	 */
	public boolean zoneExists(String zoneName) {
		if (zoneName == null || zoneName.isEmpty()) return false;
		return urbanGraph.searchVertex(zoneName);
	}

	/**
	 * Obtiene todas las zonas del sistema
	 * @return Lista de nombres de zonas
	 */
	public LinkedList<String> getAllZones() {
		return urbanGraph.getVertices();
	}

	// ==================== GESTIÓN DE RUTAS ENTRE ZONAS ====================
	/**
	 * Conecta dos zonas con un peso específico
	 * @param zone1 Zona de origen
	 * @param zone2 Zona de destino
	 * @param weight Peso de la conexión
	 * @return true si se conecta correctamente
	 * @throws ItemNotFound si alguna zona no existe
	 */
	public boolean connectZones(String zone1, String zone2, int weight) throws ItemNotFound {
		if (zone1 == null || zone2 == null || zone1.isEmpty() || zone2.isEmpty()) {
			throw new IllegalArgumentException("Los nombres de las zonas no pueden estar vacíos.");
		}

		if (!urbanGraph.searchVertex(zone1)) {
			throw new ItemNotFound("La zona origen '" + zone1 + "' no existe.");
		}

		if (!urbanGraph.searchVertex(zone2)) {
			throw new ItemNotFound("La zona destino '" + zone2 + "' no existe.");
		}

		if (urbanGraph.searchEdge(zone1, zone2, -1)) {
			return false; // Ya existe conexión
		}

		urbanGraph.insertEdge(zone1, zone2, weight);
		return true;
	}

	/**
	 * Desconecta dos zonas si existe una conexión
	 * @param zone1 Zona de origen
	 * @param zone2 Zona de destino
	 * @return true si se desconecta correctamente
	 * @throws ItemNotFound si alguna zona no existe
	 */
	public boolean disconnectZones(String zone1, String zone2) throws ItemNotFound {
		if (!urbanGraph.searchVertex(zone1) || !urbanGraph.searchVertex(zone2)) {
			throw new ItemNotFound("Una o ambas zonas no existen.");
		}

		boolean removed = urbanGraph.removeEdge(zone1, zone2);

		if (!removed) {
			throw new ItemNotFound("No existe la arista");
		}

		return true;
	}

	public boolean updateEdgeWeight(String zone1, String zone2, int newWeight) throws ItemNotFound {
		if (!urbanGraph.searchVertex(zone1) || !urbanGraph.searchVertex(zone2)) {
			throw new ItemNotFound("Una o ambas zonas no existen.");
		}

		boolean updated1 = urbanGraph.modifyEdgeWeight(zone1, zone2, newWeight);
		boolean updated2 = urbanGraph.modifyEdgeWeight(zone2, zone1, newWeight); // el otro sentido

		if (!updated1 && !updated2) {
			throw new ItemNotFound("La arista no existe en ninguna dirección.");
		}

		return true;
	}


	// ==================== BÚSQUEDA DE RUTAS ====================
	/**
	 * Encuentra la ruta más corta entre dos zonas usando Dijkstra
	 * @param origin Zona de origen
	 * @param destination Zona de destino
	 * @return Lista de zonas que forman la ruta más corta
	 * @throws ItemNotFound si alguna zona no existe
	 * @throws ExceptionIsEmpty si no hay ruta
	 */
	public LinkedList<String> findShortestPath(String origin, String destination)
			throws ItemNotFound, ExceptionIsEmpty {

		if (!urbanGraph.searchVertex(origin)) {
			throw new ItemNotFound("La zona origen '" + origin + "' no existe");
		}

		if (!urbanGraph.searchVertex(destination)) {
			throw new ItemNotFound("La zona destino '" + destination + "' no existe");
		}

		LinkedList<String> path = Dijkstra.shortestPath(urbanGraph, origin, destination);
		if (path != null && !path.isEmpty()) {
			String routeRecord = origin + " -> " + destination + " (Dijkstra)";
			routeHistory.enqueue(routeRecord);
		}

		return path;
	}

	/**
	 * Encuentra cualquier camino entre dos zonas usando BFS
	 * @param origin Zona de origen
	 * @param destination Zona de destino
	 * @return Lista de zonas que forman el camino
	 * @throws ItemNotFound si alguna zona no existe
	 * @throws ExceptionIsEmpty si no hay camino
	 */
	public LinkedList<String> findPath(String origin, String destination)
			throws ItemNotFound, ExceptionIsEmpty {

		if (!urbanGraph.searchVertex(origin)) {
			throw new ItemNotFound("La zona origen '" + origin + "' no existe");
		}

		if (!urbanGraph.searchVertex(destination)) {
			throw new ItemNotFound("La zona destino '" + destination + "' no existe");
		}

		BFS<String> bfs = new BFS<>();
		LinkedList<String> path = bfs.bfsPath(urbanGraph, origin, destination);

		if (path != null && !path.isEmpty()) {
			String routeRecord = origin + " -> " + destination + " (BFS)";
			routeHistory.enqueue(routeRecord);
		}

		return path;
	}

	// ==================== GESTIÓN DE ÁRBOLES B+ ====================
	/**
	 * Añade un distrito (nodo) al árbol B+ de una zona
	 * @param zoneName Zona donde se añadirá el distrito
	 * @param districtName Nombre del distrito a añadir
	 * @return true si se añade correctamente
	 * @throws ItemNotFound si la zona no existe
	 */
	public boolean addDistrictToZone(String zoneName, String districtName) throws ItemNotFound {
		Zona zona = buscarZona(zoneName);
		if (zona == null) {
			throw new ItemNotFound("La zona '" + zoneName + "' no existe.");
		}

		BTree<Punto> arbol = zona.getArbolDistrito();
		Punto nuevo = new Punto(districtName);

		if (arbol.search(nuevo) != false) {
			return false; // Distrito ya existe
		}

		arbol.insert(nuevo);
		return true;
	}

	/**
	 * Elimina un distrito del árbol B+ de una zona
	 * @param zoneName Zona donde se encuentra el distrito
	 * @param districtName Nombre del distrito a eliminar
	 * @return true si se elimina correctamente
	 * @throws ItemNotFound si la zona o distrito no existen
	 */
	public boolean removeDistrictFromZone(String zoneName, String districtName) throws ItemNotFound {
		Zona zona = buscarZona(zoneName);
		if (zona == null) {
			throw new ItemNotFound("La zona '" + zoneName + "' no existe.");
		}

		BTree<Punto> arbol = zona.getArbolDistrito();
		Punto distrito = new Punto(districtName);

		if (false == arbol.search(distrito)) {
			return false; // No existe
		}

		arbol.remove(distrito);
		return true;
	}

	/**
	 * Busca un distrito en el árbol B+ de una zona
	 * @param zoneName Zona donde buscar
	 * @param districtName Nombre del distrito a buscar
	 * @return true si el distrito existe
	 * @throws ItemNotFound si la zona no existe
	 */
	public boolean searchDistrictInZone(String zoneName, String districtName) throws ItemNotFound {
		Zona zona = buscarZona(zoneName);
		if (zona == null) {
			throw new ItemNotFound("La zona '" + zoneName + "' no existe.");
		}

		BTree<Punto> arbol = zona.getArbolDistrito();
		Punto distrito = new Punto(districtName);
		return arbol.search(distrito);
	}

	/**
	 * Muestra todos los distritos de una zona
	 * @param zoneName Zona a mostrar
	 * @throws ItemNotFound si la zona no existe
	 */
	public void showDistrictsOfZone(String zoneName) throws ItemNotFound {
		Zona zona = buscarZona(zoneName);
		if (zona == null) {
			throw new ItemNotFound("La zona '" + zoneName + "' no existe.");
		}

		System.out.println("Distritos en la zona '" + zoneName + "':");
		System.out.println(zona.getArbolDistrito().toString());
	}

	public String getZoneBPlusTreeAsString(String zoneName) throws ItemNotFound {
		Zona zona = buscarZona(zoneName);
		if (zona == null) {
			throw new ItemNotFound("La zona '" + zoneName + "' no existe.");
		}

		BTree<Punto> arbol = zona.getArbolDistrito();
		if (arbol.getRoot() == null) {
			return "Árbol vacío.";
		}

		return arbol.toStructuredString(); // Este método debe estar en tu clase BTree
	}

	// ==================== ANÁLISIS DEL SISTEMA ====================
	/**
	 * Verifica si el sistema está conectado
	 * @return true si el grafo es conexo
	 */
	public boolean isSystemConnected() {
		return urbanGraph.isConexo();
	}

	/**
	 * Detecta ciclos en el grafo
	 * @param zonePath Ruta de zonas para verificar
	 * @return true si es un ciclo válido
	 */
	public boolean isValidCycle(LinkedList<String> zonePath) {
		return urbanGraph.esCiclo(zonePath);
	}

	/**
	 * Obtiene las componentes conexas del grafo
	 * @return Lista de componentes conexas
	 */
	public LinkedList<LinkedList<String>> getConnectedComponents() {
		return urbanGraph.getConnectedComponents();
	}

	/**
	 * Detecta zonas aisladas (sin conexiones)
	 * @return Lista de zonas aisladas
	 */
	public LinkedList<String> getIsolatedZones() {
		LinkedList<String> isolatedZones = new LinkedList<>();
		LinkedList<String> allZones = urbanGraph.getVertices();

		for (int i = 0; i < allZones.length(); i++) {
			String zone = allZones.getNodeAtIndex(i);
			if (urbanGraph.gradoNodo(zone) == 0) {
				isolatedZones.insertLast(zone);
			}
		}

		return isolatedZones;
	}

	// ==================== SIMULACIÓN DE ESCENARIOS ====================
	/**
	 * Cierra temporalmente una vía (elimina una arista)
	 * @param zone1 Zona origen
	 * @param zone2 Zona destino
	 * @return true si se cierra correctamente
	 * @throws ItemNotFound si la conexión no existe
	 */
	public boolean closeRoad(String zone1, String zone2) throws ItemNotFound {
		return disconnectZones(zone1, zone2);
	}

	/**
	 * Reabre una vía cerrada previamente
	 * @param zone1 Zona origen
	 * @param zone2 Zona destino
	 * @param weight Peso de la conexión
	 * @return true si se reabre correctamente
	 * @throws ItemNotFound si las zonas no existen
	 */
	public boolean reopenRoad(String zone1, String zone2, int weight) throws ItemNotFound {
		if (!urbanGraph.searchVertex(zone1) || !urbanGraph.searchVertex(zone2)) {
			throw new ItemNotFound("Una o ambas zonas no existen");
		}
		urbanGraph.insertEdge(zone1, zone2, weight);
		return true;
	}

	// ==================== MÉTODOS AUXILIARES ====================
	/**
	 * Busca una zona por su nombre
	 * @param zoneName Nombre de la zona a buscar
	 * @return Zona si existe, null en otro caso
	 */
	public Zona buscarZona(String zoneName) {
		for (int i = 0; i < zones.length(); i++) {
			Zona zona = zones.getNodeAtIndex(i);
			if (zona.getNombre().equalsIgnoreCase(zoneName)) {
				return zona;
			}
		}
		return null;
	}

	/**
	 * Obtiene el historial de rutas consultadas
	 * @return Cola con el historial de rutas
	 */
	public LinkedQueue<String> getRouteHistory() {
		return routeHistory;
	}

	/**
	 * Obtiene las últimas N rutas consultadas
	 * @param count Cantidad de rutas a obtener
	 * @return Lista con las últimas rutas
	 * @throws ExceptionIsEmpty si no hay rutas en historial
	 */
	public LinkedList<String> getRecentRoutes(int count) throws ExceptionIsEmpty {
		LinkedList<String> recentRoutes = new LinkedList<>();
		LinkedQueue<String> tempQueue = new LinkedQueue<>();
		int added = 0;

		while (!routeHistory.isEmpty()) {
			tempQueue.enqueue(routeHistory.dequeue());
		}

		LinkedStack<String> tempStack = new LinkedStack<>();
		while (!tempQueue.isEmpty()) {
			String route = tempQueue.dequeue();
			tempStack.push(route);
			routeHistory.enqueue(route);
		}

		while (!tempStack.isEmpty() && added < count) {
			recentRoutes.insertFirst(tempStack.pop());
			added++;
		}

		return recentRoutes;
	}

	// ==================== MÉTODOS DE UTILIDAD ====================
	/**
	 * Obtiene el grafo urbano subyacente
	 * @return El grafo urbano
	 */
	public GraphLink<String> getUrbanGraph() {
		return urbanGraph;
	}

	/**
	 * Obtiene estadísticas del sistema
	 * @return Cadena con estadísticas
	 */
	public String getSystemStats() {
		StringBuilder stats = new StringBuilder();
		stats.append("=== ESTADÍSTICAS DEL SISTEMA ===\n");
		stats.append("Total de zonas: ").append(zones.length()).append("\n");
		stats.append("Sistema conectado: ").append(isSystemConnected() ? "Sí" : "No").append("\n");
		stats.append("Zonas aisladas: ").append(getIsolatedZones().length()).append("\n");
		stats.append("Rutas en historial: ").append(routeHistory.size()).append("\n");

		return stats.toString();
	}

	/**
	 * Reinicia completamente el sistema
	 */
	public void resetSystem() {
		this.urbanGraph = new GraphLink<>(true, true);
		this.zones = new LinkedList<>();
		this.routeHistory = new LinkedQueue<>();
	}
}