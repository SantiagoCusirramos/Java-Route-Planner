package Controller;

import DataStructure.LinkedList.LinkedList;

public class Punto implements Comparable<Punto> {
    private String nombre;
    private LinkedList<ConexionPunto> conexiones;

    public Punto(String nombre) {
        if (nombre == null) throw new IllegalArgumentException("Nombre nulo");
        this.nombre = nombre;
        this.conexiones = new LinkedList<>();
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public int compareTo(Punto o) {
        return this.nombre.compareToIgnoreCase(o.nombre);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Punto)) return false;
        Punto otro = (Punto) obj;
        return this.nombre.equalsIgnoreCase(otro.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode();
    }

    public void agregarConexion(Punto destino, int peso) {
        conexiones.insertLast(new ConexionPunto(destino, peso));
    }

    public LinkedList<ConexionPunto> getConexiones() {
        return conexiones;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
