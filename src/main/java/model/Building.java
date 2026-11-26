package model;

import datastructures.linkedlist.LinkedList;

public class Building implements Comparable<Building> {
    private String nombre;
    private LinkedList<BuildingConnection> conexiones;

    public Building(String nombre) {
        if (nombre == null) throw new IllegalArgumentException("Nombre nulo");
        this.nombre = nombre;
        this.conexiones = new LinkedList<>();
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public int compareTo(Building o) {
        return this.nombre.compareToIgnoreCase(o.nombre);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Building)) return false;
        Building otro = (Building) obj;
        return this.nombre.equalsIgnoreCase(otro.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode();
    }

    public void agregarConexion(Building destino, int peso) {
        conexiones.insertLast(new BuildingConnection(destino, peso));
    }

    public LinkedList<BuildingConnection> getConexiones() {
        return conexiones;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
