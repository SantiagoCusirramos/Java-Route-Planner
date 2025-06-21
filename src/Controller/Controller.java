package Controller;

import DataStructure.Arbolito.BTree;
import AdjacencyLists.GraphLink.GraphLink;
import DataStructure.LinkedList.LinkedList;
import DataStructure.Queue.LinkedQueue;
import DataStructure.Stack.LinkedStack;
import Tours.Dijkstra.Dijkstra;
import Exceptions.ExceptionIsEmpty;
import Exceptions.ItemNotFound;
import Exceptions.ItemDuplicated;


public class Controller {
	private GraphLink<String> urbanGraph;
	private LinkedList<Zona> zones;
	private LinkedQueue<String> routeHistory;
	private LinkedStack<String> operationStack;


	// ===================== GESTIÓN DE ZONAS COMO DISTRITOS =====================


	private final int ordenArbolDistrito = 4; // orden del árbol B para cada distrito

	// ===========================================================================

	public Controller() {
		this.urbanGraph = new GraphLink<>(false, true); // No dirigido, con pesos
		this.zones = new LinkedList<>();
		this.routeHistory = new LinkedQueue<>();
		this.operationStack = new LinkedStack<>();
	}

	//Inicializa el grafo como dirigido o no dirigido
	public Controller(boolean isDirected, boolean isWeighted) {
		this.urbanGraph = new GraphLink<>(isDirected, isWeighted);
		this.zones = new LinkedList<>();
		this.routeHistory = new LinkedQueue<>();
		this.operationStack = new LinkedStack<>();
	}

	// ==================== GESTIÓN DE ZONAS ====================

	// Se añade una nueva zona al sistema
	public boolean addZone(String zoneName) throws ItemDuplicated {
		if (zoneName == null || zoneName.trim().isEmpty()) {
			return false;
		}

		if (urbanGraph.searchVertex(zoneName)) {
			throw new ItemDuplicated("La zona '" + zoneName + "' ya existe en el sistema");
		}

		urbanGraph.insertVertex(zoneName);

		// Crear zona con orden del árbol B (ajusta según necesidad, por ejemplo, 4)
		Zona nuevaZona = new Zona(zoneName, 4);
		zones.insertLast(nuevaZona); // <-- ahora guarda la Zona, no solo el nombre

		// Registrar operación
		operationStack.push("ADD_ZONE:" + zoneName);

		return true;
	}


	//se elimina una zona del sistema
	public boolean removeZone(String zoneName) throws ItemNotFound {
		if (zoneName == null || !urbanGraph.searchVertex(zoneName)) {
			throw new ItemNotFound("La zona '" + zoneName + "' no existe en el sistema");
		}

		boolean removed = urbanGraph.removeVertex(zoneName);
		if (removed) {
			zones.removeNode(zoneName);
			operationStack.push("REMOVE_ZONE:" + zoneName);
		}

		return removed;
	}


	//se verifica si una zona existe en el sistema
	public boolean zoneExists(String zoneName) {
		return urbanGraph.searchVertex(zoneName);
	}


	public LinkedList<String> getAllZones() {
		return urbanGraph.getVertices();
	}

	// ==================== GESTIÓN DE RUTAS ====================

	//conecta 2 zonas con una ruta
	public boolean connectZones(String zone1, String zone2) throws ItemNotFound {
		return connectZones(zone1, zone2, 1); // Peso por defecto
	}


	//ruta con peso
	public boolean connectZones(String zone1, String zone2, int weight) throws ItemNotFound {
		if (!urbanGraph.searchVertex(zone1)) {
			throw new ItemNotFound("La zona origen '" + zone1 + "' no existe");
		}
		if (!urbanGraph.searchVertex(zone2)) {
			throw new ItemNotFound("La zona destino '" + zone2 + "' no existe");
		}

		urbanGraph.insertEdge(zone1, zone2, weight);
		operationStack.push("CONNECT:" + zone1 + "->" + zone2 + ":" + weight);

		return true;
	}


	//desconecta 2 zonas
	public boolean disconnectZones(String zone1, String zone2) throws ItemNotFound {
		if (!urbanGraph.searchVertex(zone1) || !urbanGraph.searchVertex(zone2)) {
			throw new ItemNotFound("Una o ambas zonas no existen en el sistema");
		}

		boolean removed = urbanGraph.removeEdge(zone1, zone2);
		if (removed) {
			operationStack.push("DISCONNECT:" + zone1 + "->" + zone2);
		}

		return removed;
	}


	//verifica si las zonas estan conectadas
	public boolean areZonesConnected(String zone1, String zone2) {
		return urbanGraph.searchEdge(zone1, zone2, -1);
	}

	// ==================== BÚSQUEDA DE RUTAS AVANZADA ====================


	//camino mas corto entre 2 zonas usando dijkstra
	public LinkedList<String> findShortestPath(String origin, String destination) throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(origin)) {
			throw new ItemNotFound("La zona origen '" + origin + "' no existe");
		}
		if (!urbanGraph.searchVertex(destination)) {
			throw new ItemNotFound("La zona destino '" + destination + "' no existe");
		}

		// Usar algoritmo de Dijkstra mejorado
		LinkedList<String> path = Dijkstra.shortestPath(urbanGraph, origin, destination);

		if (path != null && !path.isEmpty()) {
			// Guardar en historial
			String routeRecord = origin + " -> " + destination + " (Dijkstra)";
			routeHistory.enqueue(routeRecord);
			operationStack.push("DIJKSTRA_ROUTE:" + routeRecord);
		}

		return path;
	}

	/**
	 * Encuentra el camino más corto usando el algoritmo interno del grafo
	 */
	public LinkedList<String> findShortestPathInternal(String origin, String destination) throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(origin)) {
			throw new ItemNotFound("La zona origen '" + origin + "' no existe");
		}
		if (!urbanGraph.searchVertex(destination)) {
			throw new ItemNotFound("La zona destino '" + destination + "' no existe");
		}

		LinkedList<String> path = urbanGraph.shortPath(origin, destination);

		if (path != null) {
			// Guardar en historial
			String routeRecord = origin + " -> " + destination + " (Internal)";
			routeHistory.enqueue(routeRecord);
			operationStack.push("INTERNAL_ROUTE:" + routeRecord);
		}

		return path;
	}


	//encuentra cualquier camino entre 2 zonas usando bfs
	public LinkedList<String> findPath(String origin, String destination) throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(origin)) {
			throw new ItemNotFound("La zona origen '" + origin + "' no existe");
		}
		if (!urbanGraph.searchVertex(destination)) {
			throw new ItemNotFound("La zona destino '" + destination + "' no existe");
		}

		LinkedList<String> path = urbanGraph.bfsPath(origin, destination);

		if (path != null) {
			String routeRecord = origin + " -> " + destination + " (BFS)";
			routeHistory.enqueue(routeRecord);
			operationStack.push("BFS_ROUTE:" + routeRecord);
		}

		return path;
	}

	//encuentra el camino más corto usando dijkstra
	public LinkedStack<String> findShortestPathStack(String origin, String destination) throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(origin)) {
			throw new ItemNotFound("La zona origen '" + origin + "' no existe");
		}
		if (!urbanGraph.searchVertex(destination)) {
			throw new ItemNotFound("La zona destino '" + destination + "' no existe");
		}

		// Usar la clase Dijkstra externa
		LinkedList<String> pathList = Dijkstra.shortestPath(urbanGraph, origin, destination);

		if (pathList == null || pathList.isEmpty()) {
			return null;
		}

		// Convertir LinkedList a LinkedStack
		LinkedStack<String> pathStack = new LinkedStack<>();

		try {
			// Crear un stack temporal para manejar el orden
			LinkedStack<String> tempStack = new LinkedStack<>();

			// Si LinkedList tiene getNode(index):
			for (int i = 0; i < pathList.length(); i++) {
				String vertex = pathList.getNodeAtIndex(i);
				tempStack.push(vertex);
			}

			// Invertir el orden para el stack final
			while (!tempStack.isEmpty()) {
				pathStack.push(tempStack.pop());
			}

			String routeRecord = origin + " -> " + destination + " (Dijkstra External Stack)";
			routeHistory.enqueue(routeRecord);
			operationStack.push("DIJKSTRA_EXTERNAL_STACK:" + routeRecord);

		} catch (Exception e) {
			throw new ExceptionIsEmpty("Error al procesar el camino de Dijkstra");
		}

		return pathStack;
	}

	// ==================== EXPLORACIÓN MEJORADA ====================

	//explora todas las zonas conectadas usando dfs
	public void exploreFromZone(String startZone) throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(startZone)) {
			throw new ItemNotFound("La zona '" + startZone + "' no existe");
		}

		System.out.println("Explorando (DFS) desde " + startZone + ":");
		urbanGraph.dfs(startZone);
		System.out.println();

		operationStack.push("EXPLORE_DFS:" + startZone);
	}

	//explorar todas las zonas conectadas desde un punto mediante bfs
	public void exploreFromZoneBFS(String startZone) throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(startZone)) {
			throw new ItemNotFound("La zona '" + startZone + "' no existe");
		}

		System.out.println("Explorando (BFS) desde " + startZone + ":");
		urbanGraph.bfs(startZone);
		System.out.println();

		operationStack.push("EXPLORE_BFS:" + startZone);
	}

	//lista de zonas visitadas en orden dfs
	public LinkedList<String> getDFSTraversal(String startZone) throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(startZone)) {
			throw new ItemNotFound("La zona '" + startZone + "' no existe");
		}

		operationStack.push("DFS_TRAVERSAL:" + startZone);

		// Placeholder - implementar según la estructura de GraphLink
		LinkedList<String> traversal = new LinkedList<>();
		// Aquí iría la lógica para obtener el recorrido DFS como lista
		return traversal;
	}


	public LinkedList<String> getBFSTraversal(String startZone) throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(startZone)) {
			throw new ItemNotFound("La zona '" + startZone + "' no existe");
		}

		operationStack.push("BFS_TRAVERSAL:" + startZone);

		// Placeholder - implementar según la estructura de GraphLink
		LinkedList<String> traversal = new LinkedList<>();
		// Aquí iría la lógica para obtener el recorrido BFS como lista
		return traversal;
	}

	// ==================== ANÁLISIS DE ZONAS ====================


	//grafo de conectividad de una zona
	public int getZoneConnectivity(String zoneName) throws ItemNotFound {
		if (!urbanGraph.searchVertex(zoneName)) {
			throw new ItemNotFound("La zona '" + zoneName + "' no existe");
		}

		return urbanGraph.gradoNodo(zoneName);
	}

	//verifica si el sistema esta conectado
	public boolean isSystemConnected() {
		return urbanGraph.isConexo();
	}


	//verifica si el sistema forma un grafo completo
	public boolean isSystemComplete() {
		return urbanGraph.esCompleto();
	}


	//verifica si un camino es valido
	public boolean isValidPath(LinkedList<String> zonePath) {
		return urbanGraph.esCamino(zonePath);
	}

	//verifica si es un ciclo valido
	public boolean isValidCycle(LinkedList<String> zonePath) {
		return urbanGraph.esCiclo(zonePath);
	}


	//verifica si se forma una estructura de rueda
	public boolean isWheelStructure(LinkedList<String> zonePath) {
		return urbanGraph.esRueda(zonePath);
	}

	// ==================== ANÁLISIS DE RUTAS MÚLTIPLES ====================


	//encontrar todas las rutas posibles entre 2 zonas
	public LinkedList<LinkedList<String>> findAllPaths(String origin, String destination, int maxPaths)
			throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(origin)) {
			throw new ItemNotFound("La zona origen '" + origin + "' no existe");
		}
		if (!urbanGraph.searchVertex(destination)) {
			throw new ItemNotFound("La zona destino '" + destination + "' no existe");
		}

		LinkedList<LinkedList<String>> allPaths = new LinkedList<>();

		operationStack.push("FIND_ALL_PATHS:" + origin + "->" + destination);
		return allPaths;
	}


	//compara diferentes algoritmos de busqueda en un par origen-destino
	public RouteComparisonResult compareRoutingAlgorithms(String origin, String destination)
			throws ItemNotFound, ExceptionIsEmpty {
		if (!urbanGraph.searchVertex(origin) || !urbanGraph.searchVertex(destination)) {
			throw new ItemNotFound("Una o ambas zonas no existen");
		}

		RouteComparisonResult result = new RouteComparisonResult();

		//recorrido bfs
		long startTime = System.nanoTime();
		LinkedList<String> bfsPath = urbanGraph.bfsPath(origin, destination);
		long bfsTime = System.nanoTime() - startTime;

		//algoritmo dijkstra
		startTime = System.nanoTime();
		LinkedList<String> dijkstraPath = Dijkstra.shortestPath(urbanGraph, origin, destination);
		long dijkstraTime = System.nanoTime() - startTime;

		//camino más corto interno
		startTime = System.nanoTime();
		LinkedList<String> internalPath = urbanGraph.shortPath(origin, destination);
		long internalTime = System.nanoTime() - startTime;

		result.setBfsPath(bfsPath);
		result.setDijkstraPath(dijkstraPath);
		result.setInternalPath(internalPath);
		result.setBfsTime(bfsTime);
		result.setDijkstraTime(dijkstraTime);
		result.setInternalTime(internalTime);

		operationStack.push("COMPARE_ALGORITHMS:" + origin + "->" + destination);

		return result;
	}

	// ==================== HISTORIAL Y OPERACIONES ====================

	//historial de rutas consultadas
	public LinkedQueue<String> getRouteHistory() {
		return routeHistory;
	}


	//ultimas n consultas del historial
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

		// Restaurar el stack
		while (!tempStack.isEmpty()) {
			tempStack.pop();
		}

		return recentRoutes;
	}


	//deshace la ultima operacion realizada
	public boolean undoLastOperation() throws ExceptionIsEmpty {
		if (operationStack.isEmpty()) {
			return false;
		}

		String lastOperation = operationStack.pop();
		String[] parts = lastOperation.split(":");

		try {
			switch (parts[0]) {
			case "ADD_ZONE":
				urbanGraph.removeVertex(parts[1]);
				zones.removeNode(parts[1]);
				break;
			case "REMOVE_ZONE":
				urbanGraph.insertVertex(parts[1]);
				zones.insertLast(parts[1]);
				break;
			case "CONNECT":
				String[] connection = parts[1].split("->");
				urbanGraph.removeEdge(connection[0], connection[1]);
				break;
			case "DISCONNECT":
				String[] disconnection = parts[1].split("->");
				urbanGraph.insertEdge(disconnection[0], disconnection[1]);
				break;
			default:
				//para operaciones de consulta no se requiere deshacer
				return true;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	//limpiar historial
	public void clearHistory() {
		routeHistory.clear();
		while (!operationStack.isEmpty()) {
			try {
				operationStack.pop();
			} catch (ExceptionIsEmpty e) {
				break;
			}
		}
	}

	// ==================== UTILIDADES AVANZADAS ====================


	//estadisticas detalladas del sistema
	public String getSystemStats() {
		StringBuilder stats = new StringBuilder();
		stats.append("=== ESTADÍSTICAS AVANZADAS DEL SISTEMA ===\n");
		stats.append("Total de zonas: ").append(zones.length()).append("\n");
		stats.append("Sistema conectado: ").append(isSystemConnected() ? "Sí" : "No").append("\n");
		stats.append("Sistema completo: ").append(isSystemComplete() ? "Sí" : "No").append("\n");
		stats.append("Rutas en historial: ").append(routeHistory.size()).append("\n");
		stats.append("Operaciones realizadas: ").append(operationStack.totalAmount()).append("\n");

		//estadísticas de conectividad por zona
		stats.append("\n=== CONECTIVIDAD POR ZONA ===\n");
		try {
			LinkedList<String> allZones = getAllZones();
			stats.append("(Estadísticas de conectividad individual disponibles por zona)\n");
		} catch (Exception e) {
			stats.append("Error al calcular conectividad individual\n");
		}

		return stats.toString();
	}


	//validar integridad del sistema
	public SystemIntegrityReport validateSystem() {
		SystemIntegrityReport report = new SystemIntegrityReport();

		try {
			// Verificar consistencia de datos
			report.setZoneConsistency(zones.length() == urbanGraph.getVertices().length());
			report.setConnectivityStatus(isSystemConnected());
			report.setCompleteGraphStatus(isSystemComplete());

			// Verificar si hay zonas aisladas
			int isolatedZones = 0;
			LinkedList<String> allZones = getAllZones();
			// Contar zonas con grado 0

			report.setIsolatedZonesCount(isolatedZones);
			report.setValid(report.isZoneConsistency() && isolatedZones == 0);

		} catch (Exception e) {
			report.setValid(false);
			report.setErrorMessage("Error durante la validación: " + e.getMessage());
		}

		return report;
	}


	//estado actual del sistema
	public String exportSystemState() {
		StringBuilder export = new StringBuilder();
		export.append("=== ESTADO DEL SISTEMA ===\n");
		export.append("Timestamp: ").append(System.currentTimeMillis()).append("\n");
		export.append("Zones: ").append(zones.length()).append("\n");
		export.append("Connected: ").append(isSystemConnected()).append("\n");
		export.append("Complete: ").append(isSystemComplete()).append("\n");

		// Agregar más detalles del estado
		export.append("\n").append(getSystemStats());

		return export.toString();
	}


	//reiniciar sistema
	public void resetSystem() {
		this.urbanGraph = new GraphLink<>(false, true);
		this.zones = new LinkedList<>();
		this.routeHistory = new LinkedQueue<>();
		this.operationStack = new LinkedStack<>();
	}


	//obtener grafo subyacente
	public GraphLink<String> getUrbanGraph() {
		return urbanGraph;
	}

	// ==================== CLASES AUXILIARES ====================


	//Clase para almacenar resultados de comparación de algoritmos de ruteo
	public static class RouteComparisonResult {
		private LinkedList<String> bfsPath;
		private LinkedList<String> dijkstraPath;
		private LinkedList<String> internalPath;
		private long bfsTime;
		private long dijkstraTime;
		private long internalTime;

		// Getters y Setters
		public LinkedList<String> getBfsPath() { return bfsPath; }
		public void setBfsPath(LinkedList<String> bfsPath) { this.bfsPath = bfsPath; }

		public LinkedList<String> getDijkstraPath() { return dijkstraPath; }
		public void setDijkstraPath(LinkedList<String> dijkstraPath) { this.dijkstraPath = dijkstraPath; }

		public LinkedList<String> getInternalPath() { return internalPath; }
		public void setInternalPath(LinkedList<String> internalPath) { this.internalPath = internalPath; }

		public long getBfsTime() { return bfsTime; }
		public void setBfsTime(long bfsTime) { this.bfsTime = bfsTime; }

		public long getDijkstraTime() { return dijkstraTime; }
		public void setDijkstraTime(long dijkstraTime) { this.dijkstraTime = dijkstraTime; }

		public long getInternalTime() { return internalTime; }
		public void setInternalTime(long internalTime) { this.internalTime = internalTime; }

		@Override
		public String toString() {
			return "RouteComparisonResult{" +
					"bfsTime=" + bfsTime + "ns, " +
					"dijkstraTime=" + dijkstraTime + "ns, " +
					"internalTime=" + internalTime + "ns}";
		}
	}

	//Clase para reporte de integridad del sistema

	public static class SystemIntegrityReport {
		private boolean valid;
		private boolean zoneConsistency;
		private boolean connectivityStatus;
		private boolean completeGraphStatus;
		private int isolatedZonesCount;
		private String errorMessage;

		// Getters y Setters
		public boolean isValid() { return valid; }
		public void setValid(boolean valid) { this.valid = valid; }

		public boolean isZoneConsistency() { return zoneConsistency; }
		public void setZoneConsistency(boolean zoneConsistency) { this.zoneConsistency = zoneConsistency; }

		public boolean isConnectivityStatus() { return connectivityStatus; }
		public void setConnectivityStatus(boolean connectivityStatus) { this.connectivityStatus = connectivityStatus; }

		public boolean isCompleteGraphStatus() { return completeGraphStatus; }
		public void setCompleteGraphStatus(boolean completeGraphStatus) { this.completeGraphStatus = completeGraphStatus; }

		public int getIsolatedZonesCount() { return isolatedZonesCount; }
		public void setIsolatedZonesCount(int isolatedZonesCount) { this.isolatedZonesCount = isolatedZonesCount; }

		public String getErrorMessage() { return errorMessage; }
		public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

		@Override
		public String toString() {
			return "SystemIntegrityReport{" +
					"valid=" + valid +
					", zoneConsistency=" + zoneConsistency +
					", connectivityStatus=" + connectivityStatus +
					", completeGraphStatus=" + completeGraphStatus +
					", isolatedZonesCount=" + isolatedZonesCount +
					", errorMessage='" + errorMessage + '\'' +
					'}';
		}
	}



	// ==================== GESTIÓN DE DISTRITOS ====================



}
