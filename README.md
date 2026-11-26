# Java-Route-Planner

*Rutas Mínimas*: route planning over a city graph.

Desktop app (Java Swing) for modeling a city as a **directed weighted graph** of neighborhoods (*urbanizaciones*) and finding routes between them.
Each neighborhood also stores its buildings in a **B-Tree**.
Every data structure is implemented by hand: linked list, stack, queue, priority queue, B-Tree and graph. None of them come from `java.util`.

Final team project, Systems Engineering, UCSM, June 2025.
The code and the interface are in Spanish.

## Features

| Feature | How it works |
|---|---|
| Neighborhoods and streets | Add, rename and delete neighborhoods; one-way streets with a weight (distance or time) |
| **Shortest route** | Dijkstra over the street weights |
| **Fewest stops** | BFS, for the route with the fewest neighborhoods in between |
| Fully connected? | Strong connectivity: every neighborhood can reach every other one |
| Connected components | Groups of neighborhoods linked by streets, in either direction |
| Isolated neighborhoods | No street goes in or out |
| Cycle detection | Whether you can leave a neighborhood and come back to it |
| Buildings per neighborhood | Click a neighborhood to open its B-Tree: insert, delete, search and see the tree drawn |
| Find a building | Searches the B-Trees of every neighborhood and tells you where the building is |

## Run it

Requires Java 21 and Maven.

```bash
mvn package
java -jar target/java-route-planner-1.0.0.jar
```

Or without the jar: `mvn compile` and then `java -cp target/classes ui.Main`.

### Regression checks

```bash
mvn compile test-compile
java -cp target/classes:target/test-classes checks.RegressionChecks
```

There are 21 checks, one per bug fixed in the November 2026 cleanup. They include 400 random insert/delete runs of the B-Tree, compared against `TreeSet`.

## Structure

```
src/main/java/
├── ui/               Swing windows: MainWindow (graph), ZoneBuildingsWindow (B-Tree), Main
├── controller/       CityController: the only entry point from the UI to the logic
├── model/            Zone (neighborhood + its B-Tree), Building
├── graph/            Graph (adjacency lists), Vertex, Edge
├── algorithms/       Dijkstra, BreadthFirstSearch
├── datastructures/   linkedlist, queue, stack, priorityqueue, btree
└── exceptions/       ExceptionIsEmpty, ItemNotFound, ItemDuplicated
src/main/resources/ui/img/   icons and background
src/test/java/checks/        RegressionChecks
```

```
MainWindow ──► CityController ──► Graph<String>  ──► Dijkstra / BreadthFirstSearch
                    │                (zones)
                    └──────────► Zone ──► BTree<Building>
```

## Project history

- **June 2025:** built by the team, with 108 commits. The first version was published under the `UCSM2` organization (`py-final-2025-pfa25_03`).
- **November 2026:** moved to this repo with its full history:
  - Reorganized into a Maven layout with descriptive names.
  - Fixed 11 bugs: broken BFS paths, Dijkstra "routes" when no path existed, B-Tree deletion, cycle detection, among others.
  - Removed dead code.

  Details are in [ROADMAP.md](ROADMAP.md) and in the commit messages.

## Credits

Team project. Everyone's commits are kept in the history.

- **Joaquín Yato** ([@JoaquinYato](https://github.com/JoaquinYato)): user interface and controller
- **Fabián Huamani** ([@IamGenarov](https://github.com/IamGenarov)): controller and domain model
- **Ángel Salas Zapata** ([@AngelSalasZapata](https://github.com/AngelSalasZapata)): `BreadthFirstSearch` class, UI styling and code review
- **Santiago Cusirramos** ([@SantiagoCusirramos](https://github.com/SantiagoCusirramos)): data structures, graph, Dijkstra and B-Tree

See [ROADMAP.md](ROADMAP.md) for the context, known limitations and next steps.
