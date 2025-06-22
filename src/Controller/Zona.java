package Controller;

import DataStructure.Arbolito.BTree;

public class Zona {
    private String nombre;
    private BTree<Punto> arbolDistrito;

    public Zona(String nombre, int orden) {
        this.nombre = nombre;
        this.arbolDistrito = new BTree<>(orden);
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public BTree<Punto> getArbolDistrito() {
        return arbolDistrito;
    }

}
