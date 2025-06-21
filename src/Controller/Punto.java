package Controller;

public class Punto implements Comparable<Punto> {
    private String nombre;

    public Punto(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public int compareTo(Punto o) {
        return this.nombre.compareToIgnoreCase(o.nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
