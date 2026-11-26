# Contexto y próximos pasos

Este documento sirve para retomar el proyecto sin redescubrir nada: de dónde viene, cómo está armado, qué se corrigió, qué limitaciones tiene y qué se puede hacer después.
La descripción general está en el [README](README.md).

---

## 1. Contexto

### Origen

- Proyecto final en equipo de Ingeniería de Sistemas, UCSM. Se desarrolló entre el 2 y el 22 de junio de 2025 con 108 commits. La primera versión se publicó en la organización `UCSM2` (`py-final-2025-pfa25_03`), pero el proyecto es del equipo; esta es la versión que sigue en el GitHub personal de Santiago.
- **Equipo:** Joaquín Yato, Fabián Huamani, Ángel Salas Zapata y Santiago Cusirramos.
- Las estructuras de datos vienen de las prácticas del curso de Estructuras de Datos y Algoritmos. Las versiones completas de esas prácticas están en el repo **Java-Algorithms**.
- **Regla del proyecto:** nada de `java.util` para las estructuras. Todo se hizo a mano: listas, pilas, colas, cola de prioridad, árbol B y grafo.

### Modelo del dominio

| Concepto | En el código | Estructura |
|---|---|---|
| Urbanización (zona) | `model.Zone` y un vértice del grafo | `graph.Graph<String>`, con listas de adyacencia |
| Calle | Arista **dirigida** con peso (distancia o tiempo) | `graph.Edge` |
| Edificio | `model.Building` | Un `BTree<Building>` de orden 4 por zona |
| Ruta más corta | `algorithms.Dijkstra` | Cola de prioridad sobre lista ordenada |
| Ruta con menos paradas | `algorithms.BreadthFirstSearch` | Cola enlazada |

**Flujo:** `ui.MainWindow` → `controller.CityController` → `Graph` / `Zone` → algoritmos.
La interfaz nunca toca el grafo directamente.

### Limpieza del 26/11/2026

1. Se pasó a un repo propio, **conservando los 108 commits** y a todos los autores.
2. Se reorganizó con la estructura de Maven y nombres claros:

| Antes | Ahora |
|---|---|
| `AdjacencyLists/GraphLink`, `Edge`, `Vertex` | `graph.Graph`, `graph.Edge`, `graph.Vertex` |
| `Controller/Controller` | `controller.CityController` |
| `Controller/Zona`, `Controller/Punto` | `model.Zone`, `model.Building` |
| `Tours/BFS`, `Tours/Dijkstra` | `algorithms.BreadthFirstSearch`, `algorithms.Dijkstra` |
| `DataStructure/Arbolito` | `datastructures.btree` |
| `DataStructure/PriorityQueue/PriorityQueueLinkSort` | `datastructures.priorityqueue.SortedListPriorityQueue` |
| `Interfaz/Interfaz`, `Interfaz/InterfazArbol` | `ui.MainWindow`, `ui.ZoneBuildingsWindow` |
| `Interfaz/img` | `src/main/resources/ui/img` |

3. Se corrigieron 11 errores (sección 2) y se agregaron `RegressionChecks`.
4. **Se borró lo que no se usaba:**
   - Clases sin uso: `ArrayQueue`, `ArrayStack` y `ConexionPunto`.
   - Métodos de teoría de grafos que la app no llamaba: `esRueda`, `esCompleto`, `esCamino`, un BFS duplicado…
   - El historial de rutas, que se guardaba pero nunca se mostraba.
   - El `.jar` versionado, los archivos de IntelliJ y el `README.me` vacío.
   - El prototipo `Ejecutable.java`, un visualizador de 15 edificios fijos.

   Todo sigue en el historial. Para recuperar algo: `git show d1472be:src/Ejecutable.java` (o la ruta que corresponda).

### Decisiones de diseño

- **El grafo es dirigido**, porque una calle puede tener un solo sentido. Por eso:
  - **"Todo conectado"** exige conexidad *fuerte*: desde cualquier zona se llega a cualquier otra.
  - **"Componentes conexas"** ignora el sentido (componentes *débiles*): agrupa las zonas unidas por calles.
  - **"Detectar ciclo"** responde si se puede salir de una zona y volver a ella.
  - **"Aisladas"** son las zonas a las que no entra ni sale ninguna calle.
- **Pesos ≥ 0**, porque Dijkstra no funciona con pesos negativos.
- **No hay calles repetidas:** entre A y B, en el mismo sentido, hay como máximo una.
- **Los nombres de zona no distinguen mayúsculas:** "Centro" y "centro" son la misma zona.

---

## 2. Errores corregidos (26/11/2026)

Cada uno tiene su caso en `src/test/java/checks/RegressionChecks.java`.

| Error | Causa | Corrección |
|---|---|---|
| "Todo conectado" fallaba después de borrar la primera zona | `LinkedList.removeNode` no descontaba el tamaño al borrar la cabeza | Descontar `countNodes`; `destroyList` también lo reinicia |
| BFS devolvía `[C, D]` para A→B→C→D | Al reconstruir buscaba el vértice en la lista de padres y devolvía el mismo vértice | Listas paralelas `discovered` y `parentOf` |
| Dijkstra devolvía `[destino]` sin que existiera camino | Reconstruía la ruta aunque la distancia fuera infinita | Lista vacía si la distancia es `MAX_VALUE`; además ignora entradas viejas de la cola |
| Se podía repetir una calle con otro peso | La búsqueda de duplicados comparaba también el peso | Peso negativo en la búsqueda = cualquier peso |
| "Detectar ciclo" casi nunca encontraba ciclos | Comparaba con el tope de la pila, que era el propio vértice | DFS que verifica si se vuelve al vértice de inicio |
| Zonas que recibían calles salían como aisladas | Solo contaba aristas de salida | Grado de entrada + grado de salida |
| El árbol B se corrompía al borrar (50 de 50 corridas fallaban) | El predecesor bajaba por el hijo equivocado, no se rebalanceaba después de borrarlo y la raíz vacía no se eliminaba | Las tres cosas corregidas; ahora pasan 400 corridas aleatorias de orden 3 a 6 |
| Todas las pilas compartían el mismo contador | `LinkedStack.counter` era `static` | Contador por instancia |
| En grafos no dirigidos, la arista inversa apuntaba al destino | `new Edge(vertexDes)` en vez de `vertexOri` | Apunta al origen |
| La interfaz se caía con errores comunes (zona repetida, arista inexistente) | `throw new RuntimeException(ex)` en los botones | Mensajes de error en pantalla |
| Pesos negativos, calles de una zona a sí misma y "Centro"/"centro" duplicados | Faltaba validar | Validación en el controlador y en la interfaz |

---

## 3. Limitaciones conocidas

| Limitación | Impacto | Idea |
|---|---|---|
| **Rendimiento.** `findVertex` recorre la lista de vértices (O(V)); Dijkstra inserta en una lista ordenada (O(V) por inserción) y guarda las distancias en listas enlazadas (O(V) por consulta) | Con miles de zonas se vuelve lento: aproximadamente O(V·(V+E)) | Heap binario + índice de vértices (sección 4) |
| Las marcas `visited` viven dentro de cada vértice | Los algoritmos modifican el grafo; dos recorridos a la vez se pisarían | Usar un conjunto de visitados por recorrido |
| La interfaz guarda su propia copia de zonas y aristas, además del controlador | Riesgo de que las dos versiones se desincronicen | Que la interfaz pida los datos al controlador |
| No hay persistencia | Al cerrar la app se pierde todo | Guardar y cargar en JSON o CSV |
| Las zonas se dibujan en círculo; A→B y B→A se superponen | Se ve confuso con muchas zonas | Posiciones libres (arrastrar) y flechas curvas |
| El mismo edificio puede estar en dos zonas | "Buscar edificio" devuelve solo la primera | Decidir si es válido o validarlo |
| Las pruebas no usan JUnit | No se integran con el IDE ni con CI | Pasarlas a JUnit 5 (pide descargar la dependencia) |
| Se perdieron los PDFs del proyecto (informe, bitácora, manual) | Falta documentación original | Pedírselos al equipo o al aula virtual |
| Nombres mezclados: clases en inglés, métodos y textos en español | Estilo inconsistente | El código nuevo, en inglés |

---

## 4. Próximos pasos

En orden. Cada paso es chico y se puede hacer en una sesión:

1. **Probar la interfaz a mano** después de la limpieza: crear zonas, calles, rutas y edificios.
2. **Pruebas con JUnit 5 + GitHub Actions**: convertir `RegressionChecks` en tests de JUnit y correrlos en cada push. Hay que agregar la dependencia de JUnit al `pom.xml` y descargarla. Después, sumar tests por estructura: lista, pila, cola, árbol B y grafo.
3. **Medir antes de optimizar**: un generador de grafos grandes (1k, 10k y 100k zonas) y tiempos de Dijkstra y BFS. Hay que guardar los resultados.
4. **Heap binario** para la cola de prioridad: Dijkstra pasa a O((V+E) log V).
5. **Índice de vértices** (tabla hash propia o mapa) para que `findVertex` sea O(1).
6. **Volver a medir** y publicar la comparación v1 (a mano) vs v2 (optimizada) en el README. Es buen material para el CV.
7. **Persistencia**: guardar y cargar la ciudad.
8. **Mejorar la interfaz**: arrastrar zonas, flechas curvas y resaltar en el mapa la ruta encontrada.

---

## 5. Futuras implementaciones

### Algoritmos de rutas

- [ ] **A\*** con heurística de distancia, cuando las zonas tengan coordenadas
- [ ] Dijkstra bidireccional
- [ ] Bellman-Ford (permite pesos negativos y detecta ciclos negativos)
- [ ] Floyd-Warshall: tabla de distancias entre todas las zonas
- [ ] K rutas más cortas (Yen), para ofrecer rutas alternativas

### Análisis de la ciudad

- [ ] Componentes **fuertemente** conexas (Tarjan o Kosaraju), para mostrar qué grupos de zonas se comunican en ambos sentidos
- [ ] Puntos de articulación y puentes: calles o zonas críticas que, si se cierran, parten la ciudad
- [ ] Árbol de expansión mínima (Prim o Kruskal): la red mínima de calles que conecta todo
- [ ] Orden topológico, si el grafo no tiene ciclos

### Funciones

- [ ] Cerrar o abrir calles temporalmente (obras o accidentes) y recalcular
- [ ] Pesos según la hora del día (tráfico)
- [ ] Varios criterios a la vez: distancia, tiempo y costo
- [ ] Ruta que pase por varias paradas
- [ ] Historial de rutas consultadas, visible en la interfaz (antes se guardaba y no se mostraba)

---

## 6. Proyectos futuros

| Idea | Descripción |
|---|---|
| **Datos reales de Arequipa** | Importar calles de OpenStreetMap, con coordenadas para A\* y un mapa real |
| **Rutas eco (base FlowteachIA)** | Un modelo que estime el tráfico por hora y el CO₂ de cada ruta, para sugerir la más eco. Une rutas, ML y web en un solo proyecto |
| **Versión web** | Backend con Spring Boot que exponga el motor de rutas y frontend con Leaflet |
| **Benchmark publicado** | Comparar las estructuras a mano con las de `java.util` y con el heap, con gráficos |
| **Librería compartida con Java-Algorithms** | Que este proyecto use las estructuras de ese repo, sin copias duplicadas |
