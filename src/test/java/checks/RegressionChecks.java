package checks;

import controller.CityController;
import datastructures.btree.BTree;
import datastructures.linkedlist.LinkedList;
import datastructures.stack.LinkedStack;
import exceptions.ItemDuplicated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * Pruebas de regresión sin dependencias externas.
 * Cada caso corresponde a un error que tenía la versión original del proyecto.
 *
 * Ejecutar:  mvn compile test-compile
 *            java -cp target/classes:target/test-classes checks.RegressionChecks
 */
public class RegressionChecks {

    private static int passed = 0;
    private static final List<String> failures = new ArrayList<>();

    private static void check(String name, Object expected, Object actual) {
        if (String.valueOf(expected).equals(String.valueOf(actual))) {
            passed++;
            System.out.println("  OK    " + name);
        } else {
            failures.add(name);
            System.out.println("  FALLA " + name + "  esperado=" + expected + "  obtenido=" + actual);
        }
    }

    private static CityController city(String... zones) throws ItemDuplicated {
        CityController c = new CityController();
        for (String z : zones) c.addZone(z);
        return c;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Estructuras");
        LinkedList<Integer> list = new LinkedList<>();
        list.insertLast(1); list.insertLast(2); list.removeNode(1);
        check("LinkedList: borrar la cabeza descuenta el tamaño", 1, list.length());
        list.destroyList();
        check("LinkedList: destroyList deja tamaño 0", 0, list.length());

        LinkedStack<Integer> s1 = new LinkedStack<>(), s2 = new LinkedStack<>();
        s1.push(1); s1.push(2); s2.push(3);
        check("LinkedStack: cada pila tiene su propio contador", 2, s1.totalAmount());

        System.out.println("Rutas");
        CityController c = city("A", "B", "C", "D", "E");
        c.connectZones("A", "B", 1); c.connectZones("B", "C", 1);
        c.connectZones("C", "D", 1); c.connectZones("B", "E", 1);
        check("BFS: reconstruye el camino completo", "[A, B, C, D]", c.findPath("A", "D"));
        check("BFS: sin camino devuelve null", null, c.findPath("D", "A"));
        check("Dijkstra: sin camino devuelve lista vacía", "[]", c.findShortestPath("D", "A"));

        CityController w = city("A", "B", "C");
        w.connectZones("A", "B", 10); w.connectZones("A", "C", 1); w.connectZones("C", "B", 1);
        check("Dijkstra: prefiere el camino de menor peso", "[A, C, B]", w.findShortestPath("A", "B"));
        check("No se repite una arista aunque cambie el peso", false, w.connectZones("A", "B", 7));
        w.updateEdgeWeight("A", "C", 50);
        check("Dijkstra: respeta el peso modificado", "[A, B]", w.findShortestPath("A", "B"));

        boolean rejected;
        try { w.connectZones("A", "C", -5); rejected = false; } catch (IllegalArgumentException e) { rejected = true; }
        check("Rechaza pesos negativos", true, rejected);
        try { w.connectZones("A", "A", 1); rejected = false; } catch (IllegalArgumentException e) { rejected = true; }
        check("Rechaza conectar una zona consigo misma", true, rejected);
        try { w.addZone("a"); rejected = false; } catch (ItemDuplicated e) { rejected = true; }
        check("Rechaza zonas repetidas sin importar mayúsculas", true, rejected);

        System.out.println("Análisis del grafo");
        CityController x = city("X", "Y", "Z");
        x.connectZones("Y", "Z", 1); x.connectZones("Z", "Y", 1);
        x.removeZone("X");
        check("Conectado tras borrar la primera zona", true, x.isSystemConnected());

        CityController oneWay = city("A", "B");
        oneWay.connectZones("A", "B", 1);
        check("Calle de un solo sentido no es 'todo conectado'", false, oneWay.isSystemConnected());
        check("Componentes ignoran el sentido de las calles", 1, oneWay.getConnectedComponents().length());

        CityController cyc = city("A", "B", "C", "D");
        cyc.connectZones("A", "B", 1); cyc.connectZones("B", "C", 1);
        cyc.connectZones("C", "A", 1); cyc.connectZones("C", "D", 1);
        check("Ciclo: A está en A->B->C->A", true, cyc.isValidCycle("A"));
        check("Ciclo: D no puede volver a sí misma", false, cyc.isValidCycle("D"));

        CityController iso = city("A", "B", "C", "D");
        iso.connectZones("A", "C", 1); iso.connectZones("B", "C", 1);
        check("Aisladas: C recibe calles, solo D está aislada", "[D]", iso.getIsolatedZones());

        iso.disconnectZones("A", "C");
        check("Desconectar deja a A aislada", "[A, D]", iso.getIsolatedZones());
        iso.renameZone("B", "Bosque");
        check("Renombrar mantiene las calles", "[Bosque, C]", iso.findPath("Bosque", "C"));

        System.out.println("Árbol B (inserción y borrado aleatorio contra TreeSet)");
        Random r = new Random(42);
        int runs = 0, bad = 0;
        for (int order = 3; order <= 6; order++) {
            for (int run = 0; run < 100; run++, runs++) {
                BTree<Integer> t = new BTree<>(order);
                TreeSet<Integer> ref = new TreeSet<>();
                List<Integer> xs = new ArrayList<>();
                for (int i = 0; i < 80; i++) xs.add(i);
                Collections.shuffle(xs, r);
                for (int v : xs) { t.insert(v); ref.add(v); }
                Collections.shuffle(xs, r);
                boolean ok = true;
                for (int v : xs) {
                    t.remove(v); ref.remove(v);
                    for (int y = 0; y < 80 && ok; y++) ok = t.search(y) == ref.contains(y);
                    if (!ok) break;
                }
                if (!ok || !t.isEmpty()) bad++;
            }
        }
        check("Árbol B: " + runs + " corridas (órdenes 3 a 6) sin errores", 0, bad);

        System.out.println();
        System.out.println(passed + " OK, " + failures.size() + " fallas");
        if (!failures.isEmpty()) System.exit(1);
    }
}
