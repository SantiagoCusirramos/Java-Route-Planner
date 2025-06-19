package DataStructure.Arbolito;

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

    public boolean searchNode(E data, int[] pos) {
        int i = 0;

        // Cambiar keys.get(i) por keys[i]
        while (i < count && data.compareTo(keys[i]) > 0) {
            i++;
        }

        pos[0] = i;

        // Cambiar keys.get(i) por keys[i]
        return (i < count && data.compareTo(keys[i]) == 0);
    }

}

//---------------------------------- ver 1

//    public boolean searchNode(E data, int[] pos) {
//        int i = 0;
//
//        while (i < count && data.compareTo(keys.get(i)) > 0) {
//            i++;
//        }
//
//        pos[0] = i;
//
//        return (i < count && data.compareTo(keys.get(i)) == 0);
//    }


//---------------------------------- ver 2

//public boolean searchNode(E data, int[] pos) {
//    pos[0] = 0;
//
//    while (pos[0] < count && ((Comparable<E>)keys).compareTo(keys.get(pos[0])) > 0) {
//        pos[0]++;
//    }
//
//    return pos[0] < count && ((Comparable<E>)keys).compareTo(keys.get(pos[0])) == 0;
//
//}

//---------------------------------- ver 3
//    public boolean searchNode(E data, int[] pos) {
//        pos[0] = 0;
//
//        while (pos[0] < count && keys.get(pos[0]).compareTo(keys.get(pos[0])) > 0) {
//            pos[0]++;
//        }
//
//        return pos[0] < count && keys.get(pos[0]).compareTo(keys.get(pos[0])) == 0;
//    }