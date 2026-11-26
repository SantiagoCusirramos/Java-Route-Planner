package model;

// ConexionPunto.java
public class BuildingConnection {
    private Building destino;
    private int peso;

    public BuildingConnection(Building destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public Building getDestino() {
        return destino;
    }

    public int getPeso() {
        return peso;
    }
}
