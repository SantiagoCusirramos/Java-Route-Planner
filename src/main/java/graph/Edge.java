package graph;

public class Edge<E> {
    private Vertex<E> refDest; // referencia de la arista que le continua
    private int weight;

    public Edge(Vertex<E> refDest, int weight) {
        this.refDest = refDest;
        this.weight = weight;
    }

    // tengo la sospecha de que necesitaremos este constructor mas :v
    public Edge(Vertex<E> refDest) {
        this.refDest = refDest;
        this.weight = -1;
    }

    // Dos aristas son iguales si llegan al mismo destino (el peso no cuenta)
    @Override
    public boolean equals(Object o){
        if(o instanceof Edge<?> e){
            return this.refDest.equals(e.refDest);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return refDest.hashCode();
    }

    public Vertex<E> getRefDest() {
        return refDest;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String toString(){
        if(this.weight > -1){
            return this.refDest.getData() + " [" + this.weight + "]";
        } else {
            return refDest.getData() + " , ";
        }
    }

}
