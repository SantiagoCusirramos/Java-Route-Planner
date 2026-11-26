package model;

import datastructures.btree.BTree;

public class Zone {
    private String nombre;
    private BTree<Building> arbolDistrito;

    public Zone(String nombre, int orden) {
        this.nombre = nombre;
        this.arbolDistrito = new BTree<>(orden);
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public BTree<Building> getArbolDistrito() {
        return arbolDistrito;
    }

}
