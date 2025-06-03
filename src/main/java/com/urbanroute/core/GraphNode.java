package core;

import java.util.Objects;

/**
 * Representa un nodo en el grafo que modela una zona urbana.
 * Cada nodo contiene una descripción, coordenadas geográficas y una zona interna con un árbol B+.
 */
public class GraphNode {
    private String id;               // Identificador único del nodo (ej. "Z1")
    private String name;             // Nombre descriptivo de la zona (ej. "Centro Histórico")
    private String description;      // Información adicional o descripción del lugar
    private double latitude;         // Coordenada geográfica: latitud
    private double longitude;        // Coordenada geográfica: longitud

    private Zone zone;               // Estructura interna de la zona (árbol B+)

    /**
     * Constructor para inicializar un nodo con todos los campos necesarios.
     *
     * @param id Identificador único del nodo.
     * @param name Nombre legible de la zona.
     * @param description Descripción textual del lugar.
     * @param latitude Latitud geográfica.
     * @param longitude Longitud geográfica.
     */
    public GraphNode(String id, String name, String description, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zone = new Zone(); // Zona con su árbol B+ vacío
    }

    /**
     * Obtiene el identificador del nodo.
     *
     * @return ID del nodo.
     */
    public String getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la zona.
     *
     * @return Nombre de la zona.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene la descripción de la zona.
     *
     * @return Descripción textual del lugar.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Obtiene la latitud geográfica del nodo.
     *
     * @return Latitud.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Obtiene la longitud geográfica del nodo.
     *
     * @return Longitud.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Obtiene la zona interna asociada al nodo.
     *
     * @return Objeto Zone que contiene un árbol B+.
     */
    public Zone getZone() {
        return zone;
    }

    /**
     * Establece un nuevo nombre para la zona.
     *
     * @param name Nuevo nombre.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Establece una nueva descripción para la zona.
     *
     * @param description Nueva descripción.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Establece una nueva latitud para el nodo.
     *
     * @param latitude Nueva latitud.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Establece una nueva longitud para el nodo.
     *
     * @param longitude Nueva longitud.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Devuelve una representación en texto del nodo, incluyendo ID, nombre y descripción.
     *
     * @return Representación en forma de cadena del nodo.
     */
    @Override
    public String toString() {
        return name + " (" + id + ") - " + description;
    }

    /**
     * Compara si dos nodos son iguales según su ID.
     *
     * @param o Objeto a comparar.
     * @return true si tienen el mismo ID, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphNode)) return false;
        GraphNode node = (GraphNode) o;
        return id.equals(node.id);
    }

    /**
     * Genera un código hash basado en el ID del nodo.
     *
     * @return Código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
