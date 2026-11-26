package datastructures.btree;

@SuppressWarnings("unchecked")
public class BNode<E extends Comparable<E>> {
    public int count;
    public E[] keys;
    public BNode<E>[] childs;
    public int idNode;

    public BNode(int n) {
        this.keys = (E[]) new Comparable[n];
        this.childs = (BNode<E>[]) new BNode[n + 1];
        this.count = 0;
        this.idNode = 0;
    }

    public boolean nodeFull(int maxKeys) {
        return count >= maxKeys;
    }

    public boolean nodeEmpty() {
        return count == 0;
    }

    public E[] getKeys() { return keys; }

    public BNode<E>[] getChilds() { return childs; }

    public boolean searchNode(E data, int[] pos) {
        int i = 0;

        while (i < count && data.compareTo(keys[i]) > 0) {
            i++;
        }

        pos[0] = i;

        return (i < count && data.compareTo(keys[i]) == 0);
    }

}
