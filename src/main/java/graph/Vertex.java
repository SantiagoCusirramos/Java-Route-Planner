package graph;

import datastructures.linkedlist.LinkedList;

public class Vertex<E> {
    private E data;
    private LinkedList<Edge<E>> listAdj; // estos son los que siguen - este usa los edge, osea en si el peso y la referencia
    private boolean visited;

    public Vertex(E data) {
        this.data = data;
        this.listAdj = new LinkedList<Edge<E>>();
        this.visited = false;
    }

    public E getData() {
        return data;
    }

    public LinkedList<Edge<E>> getListAdj() {
        return listAdj;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setData(E data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Vertex<?> v) {
            return this.data.equals(v.getData());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return data == null ? 0 : data.hashCode();
    }

    @Override
    public String toString(){
        return this.data + "-->" + this.listAdj.toString() + "\n";
    }

}