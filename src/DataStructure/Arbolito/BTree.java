package DataStructure.Arbolito;

public class BTree<E extends Comparable<E>> {
    private BNode<E> root;
    private int order; // num maximo de hijos
    private boolean up;
    private boolean down; // esto tambien lo vamos a necesitar para borrar
    private BNode<E> nDes; // nood descendiente

    public BTree(int orden) {
        this.order = orden;
        this.root = null;
        this.up = false;
        this.down = false;
        this.nDes = null;
    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public int size() {
        return this.order;
    }

    public void insert(E cl) {
        up = false;
        E mediana;
        BNode<E> pnew;
        mediana = push(this.root, cl);
        if (up) {
            pnew = new BNode<E>(this.order); // nodo padre especial -> root
            pnew.count = 1;
            pnew.keys[0] = mediana;
            pnew.childs[0] = this.root;
            pnew.childs[1] = nDes;
            this.root = pnew;
        }
    }

    private E push(BNode<E> current, E cl) {
        int pos[] = new int[1];
        E mediana;

        if (current == null) {
            up = true;
            nDes = null;
            return cl;
        } else {
            boolean fl;
            fl = current.searchNode(cl, pos);

            if (fl) {
                System.out.println("Item duplicado\n");
                up = false;
                return null;
            }

            mediana = push(current.childs[pos[0]], cl);

            if (up) {
                if (current.nodeFull(this.order - 1)) {
                    mediana = dividedNode(current, mediana, pos[0]);
                } else {
                    putNode(current, mediana, nDes, pos[0]);
                    up = false;
                }
            }
            return mediana;
        }
    }

    // desplaza nomas, ose actualiza las posiciones
    private void putNode(BNode<E> current, E clave, BNode<E> rd, int k) {
        int i;

        for (i = current.count - 1; i >= k; i--) {
            current.keys[i + 1] = current.keys[i];
            current.childs[i + 2] = current.childs[i + 1];
        }

        current.keys[k] = clave;
        current.childs[k + 1] = rd;
        current.count++;
    }

    private E dividedNode(BNode<E> current, E clave, int position) {
        BNode<E> rd = nDes;
        int i, posMdna;

        // pos new mediana
        posMdna = (position <= this.order / 2) ? this.order / 2 : this.order / 2 + 1;
        nDes = new BNode<E>(this.order);

        //  claves e hijos - mover nodo
        for (i = posMdna; i < this.order - 1; i++) {  // va de la mitad hacia adelante
            nDes.keys[i - posMdna] = current.keys[i];
            nDes.childs[i - posMdna + 1] = current.childs[i + 1];
        }

        nDes.count = (this.order - 1) - posMdna;
        current.count = posMdna;

        // meto el nodo :/
        if (position <= this.order / 2) {
            putNode(current, clave, rd, position); // 1ra mitad
        } else {
            putNode(nDes, clave, rd, position - posMdna); // 2da mitad
        }

        // mediana q sube p
        E median = current.keys[current.count - 1];
        nDes.childs[0] = current.childs[current.count];
        current.count--;

        return median;
    }

    public boolean search(E data) {
        return searchRecursive(root, data);
    }

    private boolean searchRecursive(BNode<E> current, E data) {
        if (current == null) {
            return false;
        }

        int[] pos = new int[1];
        boolean found = current.searchNode(data, pos);

        if (found) {
            return true;
        }

        // comprobamos si - la pos es valida / existe un hijo en esa pos
        if (pos[0] < current.childs.length && current.childs[pos[0]] != null) {
            return searchRecursive(current.childs[pos[0]], data);
        }
        return false;
    }
    public E searchX(E data) {
        return searchRecursiveX(root, data);
    }

    private E searchRecursiveX(BNode<E> current, E data) {
        if (current == null) {
            return null;
        }

        int[] pos = new int[1];
        boolean found = current.searchNode(data, pos);

        if (found) {
            return current.keys[pos[0]];
        }

        if (pos[0] < current.childs.length && current.childs[pos[0]] != null) {
            return searchRecursiveX(current.childs[pos[0]], data);
        }

        return null;
    }


    //    ---------------------------Delete------------------------------------
    public void remove(E cl) {
        down = false;
        delete(root, cl);
        if (root != null && root.count == 0) {
            root = root.childs[0];
        }
    }

    public boolean delete(BNode<E> node, E key) {
        if (node == null) {
            return false;
        }

        int pos[] = new int[1];
        boolean found = node.searchNode(key, pos);

        if (found) {
            if (node.childs[pos[0]] == null) { // hijo izquiero, tiene o no?
                removeKey(node, pos[0]); // tiene entra
                return true;
            } else { // tiene izq
                E pred = getPredecessor(node, pos[0]);
                node.keys[pos[0]] = pred;
                return delete(node.childs[pos[0]], pred);
            }
        } else {
            if (node.childs[pos[0]] == null) {
                return false;
            } else {
                boolean isDeleted = delete(node.childs[pos[0]], key);
                if (node.childs[pos[0]].count < (order - 1) / 2) {
                    fix(node, pos[0]);
                }
                return isDeleted;
            }
        }
    }

    private void removeKey(BNode<E> node, int index) {
        for (int i = index; i < node.count - 1; i++) {
            node.keys[i] = node.keys[i + 1];
        }
        node.keys[node.count - 1] = null;
        node.count--;
    }

    private E getPredecessor(BNode<E> node, int index) {
        BNode<E> current = node.childs[index];
        while (current.childs[index + 1] != null) {
            current = current.childs[index + 1];
        }
        return current.keys[current.count - 1];
    }

    private void fix(BNode<E> parent, int index) {
        if (index > 0 && parent.childs[index - 1].count > (order - 1) / 2) {
            borrowFromLeft(parent, index);
        } else if (index < parent.count && parent.childs[index + 1].count > (order - 1) / 2) {
            borrowFromRight(parent, index);
        } else {
            if (index > 0) {
                merge(parent, index - 1);
            } else {
                merge(parent, index);
            }
        }
    }

    private void merge(BNode<E> parent, int index) {
        BNode<E> left = parent.childs[index];
        BNode<E> right = parent.childs[index + 1];

        // Mover clave del padre al hijo izquierdo
        left.keys[left.count] = parent.keys[index];
        left.count++;

        // Mover claves e hijos del hijo derecho al izquierdo
        for (int i = 0; i < right.count; i++) {
            left.keys[left.count + i] = right.keys[i];
        }
        for (int i = 0; i <= right.count; i++) {
            left.childs[left.count + i] = right.childs[i];
        }
        left.count += right.count;

        // Eliminar la clave del padre y el hijo derecho
        for (int i = index; i < parent.count - 1; i++) {
            parent.keys[i] = parent.keys[i + 1];
            parent.childs[i + 1] = parent.childs[i + 2];
        }

        parent.keys[parent.count - 1] = null;
        parent.childs[parent.count] = null;
        parent.count--;
    }

    private void borrowFromLeft(BNode<E> parent, int index) {
        BNode<E> left = parent.childs[index - 1];
        BNode<E> current = parent.childs[index];

        // Desplazar claves e hijos del niño a la derecha
        for (int i = current.count; i >= 0; i--) {
            current.keys[i + 1] = current.keys[i];
        }

        // Mover clave del padre al niño
        current.keys[0] = parent.keys[index - 1];
        parent.keys[index - 1] = left.keys[left.count - 1];
        left.keys[left.count - 1] = null;

        // Mover hijo del hermano al niño
        if (left.childs[left.count] != null) {
            for (int i = current.count; i >= 0; i--) {
                current.childs[i + 1] = current.childs[i];
            }
            current.childs[0] = left.childs[left.count];
            left.childs[left.count] = null;
        }

        current.count++;
        left.count--;
    }

    private void borrowFromRight(BNode<E> parent, int index) {
        BNode<E> right = parent.childs[index + 1];
        BNode<E> current = parent.childs[index];

        // Insertar clave del padre al final de current
        current.keys[current.count] = parent.keys[index];

        // Reemplazar clave del padre con la primera del hermano derecho
        parent.keys[index] = right.keys[0];

        // Desplazar las claves del hermano derecho una posición a la izquierda
        for (int i = 1; i < right.count; i++) {
            right.keys[i - 1] = right.keys[i];
        }
        right.keys[right.count - 1] = null;

        // Si tiene hijos, mover el primer hijo del hermano derecho al final de current
        if (right.childs[0] != null) {
            current.childs[current.count + 1] = right.childs[0];
            for (int i = 1; i <= right.count; i++) {
                right.childs[i - 1] = right.childs[i];
            }
            right.childs[right.count] = null;
        }

        current.count++;
        right.count--;
    }

    public String toString() {
        String s = "";
        if (isEmpty()) {
            s += "BTree is empty...";
        } else {
            s = writeTree(this.root);
        }
        return s;
    }

    private String writeTree(BNode<E> current) {
        String s = "";
        if (current != null) {
            // Mostrar el contenido del nodo actual
            s += "[";
            for (int i = 0; i < current.count; i++) {
                s += current.keys[i];
                if (i < current.count - 1) {
                    s += ", ";
                }
            }
            s += "]";

            // Si no es una hoja, recorrer los hijos
            boolean isLeaf = true;
            for (int i = 0; i <= current.count; i++) {
                if (current.childs[i] != null) {
                    isLeaf = false;
                    break;
                }
            }

            if (!isLeaf) {
                s += "\n";
                for (int i = 0; i <= current.count; i++) {
                    if (current.childs[i] != null) {
                        // Agregar indentación para mostrar la estructura jerárquica
                        String childContent = writeTree(current.childs[i]);
                        String[] lines = childContent.split("\n");
                        for (String line : lines) {
                            s += "  " + line + "\n";
                        }
                    }
                }
                // Eliminar el último salto de línea
                if (s.endsWith("\n")) {
                    s = s.substring(0, s.length() - 1);
                }
            }
        }
        return s;
    }

    public String toStructuredString() {
        StringBuilder sb = new StringBuilder();
        toStructuredString(root, sb, 0);
        return sb.toString();
    }

    private void toStructuredString(BNode<E> node, StringBuilder sb, int level) {
        if (node == null) return;

        for (int i = 0; i < node.count; i++) {
            for (int j = 0; j < level; j++) sb.append("   ");
            sb.append("- ").append(node.keys[i]).append("\n");
        }

        for (int i = 0; i <= node.count; i++) {
            toStructuredString(node.childs[i], sb, level + 1);
        }
    }
}



////    ---------------------------Borrow------------------------------------
//
//
//private void redistributeFromLeft(BNode<E> parent, int childIdx) {
//    BNode<E> left = parent.childs.get(childIdx - 1);
//    BNode<E> current = parent.childs.get(childIdx);
//
//    // Desplazar claves e hijos del niño a la derecha
//    for (int i = current.count; i >= 0; i--) {
//        current.keys.set(i, current.keys.get(i - 1));
//    }
//    for (int i = current.count + 1; i > 0; i--) {
//        current.childs.set(i, current.childs.get(i - 1));
//    }
//
//    // Mover clave del padre al niño
//    current.keys.set(0, parent.keys.get(childIdx - 1));
//    current.count++;
//
//    // Mover clave del hermano al padre
//    parent.keys.set(childIdx - 1, left.keys.get(left.count - 1));
//
//    // Mover hijo del hermano al niño
//    if (left.childs.get(left.count) != null) {
//        current.childs.set(0, left.childs.get(left.count));
//    }
//
//    left.count--;
//}
//
//private void redistributeFromRight(BNode<E> parent, int childIdx) {
//    BNode<E> child = parent.childs.get(childIdx);
//    BNode<E> rightSibling = parent.childs.get(childIdx + 1);
//
//    // Mover clave del padre al niño
//    child.keys.set(child.count, parent.keys.get(childIdx));
//    child.count++;
//
//    // Mover clave del hermano al padre
//    parent.keys.set(childIdx, rightSibling.keys.get(0));
//
//    // Mover hijo del hermano al niño
//    if (rightSibling.childs.get(0) != null) {
//        child.childs.set(child.count, rightSibling.childs.get(0));
//    }
//
//    // Desplazar claves e hijos del hermano a la izquierda
//    for (int i = 0; i < rightSibling.count - 1; i++) {
//        rightSibling.keys.set(i, rightSibling.keys.get(i + 1));
//    }
//    for (int i = 0; i < rightSibling.count; i++) {
//        rightSibling.childs.set(i, rightSibling.childs.get(i + 1));
//    }
//
//    rightSibling.count--;
//}
//
////    ---------------------------Merge------------------------------------
//
//private void mergeNodes(BNode<E> parent, int idx) {
//    BNode<E> leftChild = parent.childs.get(idx);
//    BNode<E> rightChild = parent.childs.get(idx + 1);
//
//    // Mover clave del padre al hijo izquierdo
//    leftChild.keys.set(leftChild.count, parent.keys.get(idx));
//    leftChild.count++;
//
//    // Mover claves e hijos del hijo derecho al izquierdo
//    for (int i = 0; i < rightChild.count; i++) {
//        leftChild.keys.set(leftChild.count + i, rightChild.keys.get(i));
//    }
//    for (int i = 0; i <= rightChild.count; i++) {
//        leftChild.childs.set(leftChild.count + i, rightChild.childs.get(i));
//    }
//    leftChild.count += rightChild.count;
//
//    // Eliminar la clave del padre y el hijo derecho
//    for (int i = idx; i < parent.count - 1; i++) {
//        parent.keys.set(i, parent.keys.get(i + 1));
//    }
//    for (int i = idx + 1; i < parent.count; i++) {
//        parent.childs.set(i, parent.childs.get(i + 1));
//    }
//    parent.count--;
//
//    // Si el padre es la raíz y quedó vacío
//    if (parent == root && parent.count == 0) {
//        root = leftChild;
//    }
//}
//
////    ---------------------------To String------------------------------------







//-------------------------------------- Metodo imprimir
//    public String toString() {
//        String s = "";
//        if (isEmpty()) {
//            s += "BTree is empty...";
//        } else {
//            s = writeTree(this.root);
//        }
//        return s;
//    }
//
//    private String writeTree(BNode<E> current) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("%-10s %-20s %-10s %-10s\n", "Id.Nodo", "Claves Nodo", "Id.Padre", "Id.Hijos"));
//        sb.append(recorrerYFormatear(current, null));
//        return sb.toString();
//    }
//
//    private String recorrerYFormatear(BNode<E> nodo, BNode<E> padre) {
//        if (nodo == null) return "";
//
//        StringBuilder sb = new StringBuilder();
//
//        // Id del nodo
//        String idNodo = String.valueOf(nodo.idNode);
//
//        // Claves del nodo
//        String claves = "(";
//        for (int i = 0; i < nodo.count; i++) {
//            claves += nodo.keys.get(i);
//            if (i < nodo.count - 1) claves += ", ";
//        }
//        claves += ")";
//
//        // Id del padre
//        String idPadre = (padre == null) ? "--" : "[" + padre.idNode + "]";
//
//        // Ids de los hijos
//        StringBuilder hijos = new StringBuilder();
//        boolean tieneHijos = false;
//        for (int i = 0; i <= nodo.count; i++) {
//            if (nodo.childs.get(i) != null) {
//                if (tieneHijos) hijos.append(", ");
//                hijos.append(nodo.childs.get(i).idNode);
//                tieneHijos = true;
//            }
//        }
//        String idHijos = tieneHijos ? "[" + hijos.toString() + "]" : "--";
//
//        sb.append(String.format("%-10s %-20s %-10s %-10s\n", idNodo, claves, idPadre, idHijos));
//
//        // Recorrer hijos
//        for (int i = 0; i <= nodo.count; i++) {
//            if (nodo.childs.get(i) != null) {
//                sb.append(recorrerYFormatear(nodo.childs.get(i), nodo));
//            }
//        }
//
//        return sb.toString();
//    }