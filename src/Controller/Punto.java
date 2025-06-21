package Controller;
import DataStructure.LinkedList.LinkedList;



public class Punto implements Comparable<Punto> {
    private String nombre;
    private LinkedList<ConexionPunto> conexiones;

    public Punto(String nombre) {
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
