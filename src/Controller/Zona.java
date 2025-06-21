package Controller;

import DataStructure.Arbolito.BTree;

public class Zona {
    private String nombre;
    private BTree<Object> arbolDistrito;

    public Zona(String nombre, int orden) {
        this.nombre = nombre;
        this.arbolDistrito = new BTree<>(orden);
    }

    public String getNombre() {
        return nombre;
    }

    public BTree<Object> getArbolDistrito() {
        return arbolDistrito;
    }
}
1