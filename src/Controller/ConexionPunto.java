package Controller;

// ConexionPunto.java
public class ConexionPunto {
    private Punto destino;
    private int peso;

    public ConexionPunto(Punto destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }

    public Punto getDestino() {
        return destino;
    }

    public int getPeso() {
        return peso;
    }
}
