package ui;

import javax.swing.*;
import java.awt.*;

import controller.CityController;
import datastructures.linkedlist.LinkedList;
import exceptions.ExceptionIsEmpty;
import exceptions.ItemDuplicated;
import exceptions.ItemNotFound;
import model.Zone;

public class MainWindow extends JFrame {
    private CityController controller;
    private LinkedList<String> vertices;
    private LinkedList<AristaGrafica> aristas;
    private LinkedList<JComponent> botonesVer;
    private DibujoPanel panel;

    private void estiloBoton(JButton boton, Color fondo, Color texto, int ancho, int alto) {
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        boton.setPreferredSize(new Dimension(ancho, alto));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public MainWindow() {
        this.controller = new CityController();
        vertices = new LinkedList<>();
        aristas = new LinkedList<>();
        botonesVer = new LinkedList<>();

        setTitle("Editor de Grafo");
        setSize(1990, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel = new DibujoPanel();
        panel.setLayout(null);
        add(panel, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panel.repaint();
            }
        });

        JPanel botones = new JPanel(new GridLayout(2, 7, 10, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnAgregarVertice = new JButton("Agregar Urbanizacin");
        estiloBoton(btnAgregarVertice, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnAgregarVertice.addActionListener(e -> {
            try {
                agregarVertice();
            } catch (ItemDuplicated ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton btnAgregarArista = new JButton("Agregar arista");
        estiloBoton(btnAgregarArista, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnAgregarArista.addActionListener(e -> {
            try {
                agregarArista();
            } catch (ItemNotFound ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton btnEliminarVertice = new JButton("Eliminar Urbanizacin");
        estiloBoton(btnEliminarVertice, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnEliminarVertice.addActionListener(e -> {
            try {
                eliminarVertice();
            } catch (ItemNotFound ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton btnModificarVertice = new JButton("Modificar Urbanizacin");
        estiloBoton(btnModificarVertice, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnModificarVertice.addActionListener(e -> {
            try {
                modificarNombreVertice();
            } catch (ItemDuplicated | ItemNotFound ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        JButton btnEliminarArista = new JButton("Eliminar arista");
        estiloBoton(btnEliminarArista, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnEliminarArista.addActionListener(e -> {
            try {
                eliminarArista();
            } catch (ItemNotFound ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton btnModificarPeso = new JButton("Modificar peso");
        estiloBoton(btnModificarPeso, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnModificarPeso.addActionListener(e -> {
            try {
                modificarPesoArista();
            } catch (ItemNotFound ex) {
                throw new RuntimeException(ex);
            }
        });
        JButton btnBuscarVertice = new JButton("Buscar Urbanización");
        estiloBoton(btnBuscarVertice, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnBuscarVertice.addActionListener(e -> buscarVertice());

        JButton btnBuscarEdificio = new JButton("Buscar edificio");
        estiloBoton(btnBuscarEdificio, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnBuscarEdificio.addActionListener(e -> buscarEdificio());

        JButton btnExplorarBFS = new JButton("Explorar BFS");
        estiloBoton(btnExplorarBFS, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnExplorarBFS.addActionListener(e -> explorarBFS());

        JButton btnDetectarCiclo = new JButton("Detectar ciclo");
        estiloBoton(btnDetectarCiclo, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnDetectarCiclo.addActionListener(e -> {
            try {
                detectarCiclo();
            } catch (ExceptionIsEmpty ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnConexas = new JButton("Componentes conexas");
        estiloBoton(btnConexas, new Color(59, 89, 182), Color.WHITE, 170, 40);
        btnConexas.addActionListener(e -> mostrarComponentesConexas());

        JButton btnZonasAisladas = new JButton("Urbanizaciones aisladas");
        estiloBoton(btnZonasAisladas, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnZonasAisladas.addActionListener(e -> mostrarZonasAisladas());

        JButton btnSistemaConectado = new JButton("Todo conectado");
        estiloBoton(btnSistemaConectado, new Color(59, 89, 182), Color.WHITE, 180, 40);
        btnSistemaConectado.addActionListener(e -> verificarSistemaConectado());

        JButton btnRutaDijkstra = new JButton("Ruta más corta (Dijkstra)");
        estiloBoton(btnRutaDijkstra, new Color(59, 89, 182), Color.WHITE, 200, 40);
        btnRutaDijkstra.addActionListener(e -> mostrarRutaMasCortaDijkstra());

        botones.add(btnAgregarVertice);
        botones.add(btnAgregarArista);
        botones.add(btnEliminarVertice);
        botones.add(btnModificarVertice);
        botones.add(btnEliminarArista);
        botones.add(btnModificarPeso);
        botones.add(btnBuscarVertice);
        botones.add(btnBuscarEdificio);
        botones.add(btnExplorarBFS);
        botones.add(btnDetectarCiclo);
        botones.add(btnConexas);
        botones.add(btnZonasAisladas);
        botones.add(btnSistemaConectado);
        botones.add(btnRutaDijkstra);
        add(botones, BorderLayout.SOUTH);

        setVisible(true);
    }
    
    private void agregarVertice() throws ItemDuplicated {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese nombre del vértice:");
        if (nombre != null && !nombre.isEmpty()) {
            controller.addZone(nombre);
            vertices.insertLast(nombre);

            // Cargar imagen de recurso
            ImageIcon icono;
            try {
                icono = new ImageIcon(getClass().getResource("/ui/img/edificios.png"));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen del vértice.");
                return;
            }

            BotonVertice boton = new BotonVertice(nombre, icono);
            botonesVer.insertLast(boton);
            panel.add(boton);
            panel.repaint();
        }
    }

    private void modificarNombreVertice() throws ItemDuplicated, ItemNotFound {
        if (vertices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay vértices.");
            return;
        }

        String[] opciones = new String[vertices.length()];
        for (int i = 0; i < opciones.length; i++) {
            opciones[i] = vertices.getNodeAtIndex(i);
        }

        String actual = (String) JOptionPane.showInputDialog(this,
                "Seleccione vértice a renombrar:", "Modificar vértice",
                JOptionPane.PLAIN_MESSAGE, null, opciones, null);

        if (actual == null || actual.isEmpty()) return;

        String nuevo = JOptionPane.showInputDialog(this, "Nuevo nombre:");
        if (nuevo == null || nuevo.isEmpty()) return;

        if (actual.equals(nuevo)) {
            JOptionPane.showMessageDialog(this, "El nombre es el mismo.");
            return;
        }

        if (vertices.search(nuevo)) {
            throw new ItemDuplicated("Ya existe un vértice con ese nombre.");
        }

        controller.renameZone(actual, nuevo);

        int idx = vertices.findIndex(actual);
        vertices.set(idx, nuevo);

        // Quitar el viejo botón
        BotonVertice viejoBoton = (BotonVertice) botonesVer.getNodeAtIndex(idx);
        panel.remove(viejoBoton);

        // Crear nuevo botón con el nuevo nombre
        ImageIcon icono = new ImageIcon(getClass().getResource("/ui/img/edificios.png")); // o tu imagen real
        BotonVertice nuevoBoton = new BotonVertice(nuevo, icono);

        botonesVer.set(idx, nuevoBoton);
        panel.add(nuevoBoton);

        // Actualizar aristas
        for (int i = 0; i < aristas.length(); i++) {
            AristaGrafica a = aristas.getNodeAtIndex(i);
            if (a.origen.equals(actual)) a.origen = nuevo;
            if (a.destino.equals(actual)) a.destino = nuevo;
        }

        panel.revalidate(); // actualiza el layout
        panel.repaint();    // redibuja
    }

    private void abrirVentanaVertice(String nombre) throws ItemNotFound {
        Zone zona = controller.buscarZona(nombre);
        if (zona == null) {
            JOptionPane.showMessageDialog(this, "Zona no encontrada.");
            return;
        }

        ZoneBuildingsWindow ventana = new ZoneBuildingsWindow(controller, zona);
        ventana.setVisible(true);
    }

    private void agregarArista() throws ItemNotFound {
        if (vertices.length() < 2) {
            JOptionPane.showMessageDialog(this, "Agrega al menos dos vértices");
            return;
        }
        String[] vs = new String[vertices.length()];
        for (int i = 0; i < vs.length; i++) vs[i] = vertices.getNodeAtIndex(i);
        String origen = (String) JOptionPane.showInputDialog(this, "Origen:", "Vertices", JOptionPane.PLAIN_MESSAGE, null, vs, null);
        String destino = (String) JOptionPane.showInputDialog(this, "Destino:", "Vertices", JOptionPane.PLAIN_MESSAGE, null, vs, null);
        if (origen == null || destino == null) return;

        // No duplicar misma dirección
        for (int i = 0; i < aristas.length(); i++) {
            AristaGrafica ex = aristas.getNodeAtIndex(i);
            if (ex.origen.equals(origen) && ex.destino.equals(destino)) {
                JOptionPane.showMessageDialog(this, "Ya existe una arista de " + origen + " a " + destino);
                return;
            }
        }
        // Reutilizar peso de inversa
        int peso = -1;
        for (int i = 0; i < aristas.length(); i++) {
            AristaGrafica inv = aristas.getNodeAtIndex(i);
            if (inv.origen.equals(destino) && inv.destino.equals(origen)) {
                peso = inv.peso;
                JOptionPane.showMessageDialog(this, "Reutilizando peso inverso: " + peso);
                break;
            }
        }
        if (peso == -1) {
            try {
                String ps = JOptionPane.showInputDialog(this, "Ingrese peso:");
                if (ps == null) return;
                peso = Integer.parseInt(ps);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Peso inválido, usando 1");
                peso = 1;
            }
        }
        controller.connectZones(origen, destino, peso);
        aristas.insertLast(new AristaGrafica(origen, destino, peso));
        panel.repaint();
    }

    private void eliminarVertice() throws ItemNotFound {
        if (vertices.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay zonas para eliminar.");
            return;
        }

        // Mostrar lista de zonas actuales
        String[] opciones = new String[vertices.length()];
        for (int i = 0; i < opciones.length; i++) {
            opciones[i] = vertices.getNodeAtIndex(i);
        }

        String nombre = (String) JOptionPane.showInputDialog(this,
                "Seleccione la zona a eliminar:", "Eliminar Zona",
                JOptionPane.PLAIN_MESSAGE, null, opciones, null);

        if (nombre == null || nombre.trim().isEmpty()) return;

        int idx = vertices.findIndex(nombre);

        // 1. Eliminar de tu controlador
        controller.removeZone(nombre);

        // 2. Eliminar de la lista de vértices
        vertices.removeAtIndex(idx);

        // 3. Eliminar del panel visual
        JComponent botonAEliminar = botonesVer.getNodeAtIndex(idx);
        panel.remove(botonAEliminar);

        // 4. Eliminar de la lista de botones
        botonesVer.removeAtIndex(idx);

        // 5. Eliminar las aristas asociadas a ese vértice
        for (int i = 0; i < aristas.length(); ) {
            AristaGrafica a = aristas.getNodeAtIndex(i);
            if (a.origen.equals(nombre) || a.destino.equals(nombre)) {
                aristas.removeAtIndex(i); // no incrementamos i, porque se movieron los índices
            } else {
                i++;
            }
        }

        // 6. Actualizar la interfaz
        panel.revalidate();
        panel.repaint();
    }

    //opcion para eliminar arista origen-destino no funciona correctamente, corregir
    private void eliminarArista() throws ItemNotFound {
        if (aristas.length() == 0) {
            JOptionPane.showMessageDialog(this, "No hay aristas.");
            return;
        }
        String[] vs = new String[vertices.length()];
        for (int i = 0; i < vs.length; i++) vs[i] = vertices.getNodeAtIndex(i);
        String origen = (String) JOptionPane.showInputDialog(this, "Origen:", "Eliminar arista", JOptionPane.PLAIN_MESSAGE, null, vs, null);
        String destino = (String) JOptionPane.showInputDialog(this, "Destino:", "Eliminar arista", JOptionPane.PLAIN_MESSAGE, null, vs, null);
        if (origen == null || destino == null) return;
        controller.disconnectZones(origen, destino);
        for (int i = 0; i < aristas.length(); i++) {
            AristaGrafica a = aristas.getNodeAtIndex(i);
            if (a.origen.equals(origen) && a.destino.equals(destino)) { aristas.removeAtIndex(i); break; }
        }
        panel.repaint();
    }

    //al modificar el peso de aristas dobles (de A a B y de B a A) solo se actualiza el valor de un lado, corregir
    private void modificarPesoArista() throws ItemNotFound {
        if (aristas.length() == 0) {
            JOptionPane.showMessageDialog(this, "No hay aristas.");
            return;
        }

        String[] vs = new String[vertices.length()];
        for (int i = 0; i < vs.length; i++) vs[i] = vertices.getNodeAtIndex(i);
        String origen = (String) JOptionPane.showInputDialog(this, "Origen:", "Modificar Peso", JOptionPane.PLAIN_MESSAGE, null, vs, null);
        String destino = (String) JOptionPane.showInputDialog(this, "Destino:", "Modificar Peso", JOptionPane.PLAIN_MESSAGE, null, vs, null);
        if (origen == null || destino == null) return;

        String inputPeso = JOptionPane.showInputDialog(this, "Nuevo peso:");
        if (inputPeso == null) return;

        int nuevoPeso;
        try {
            nuevoPeso = Integer.parseInt(inputPeso);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Peso inválido.");
            return;
        }

        try {
            controller.updateEdgeWeight(origen, destino, nuevoPeso);
            for (int i = 0; i < aristas.length(); i++) {
                AristaGrafica a = aristas.getNodeAtIndex(i);
                if ((a.origen.equals(origen) && a.destino.equals(destino)) ||
                        (a.origen.equals(destino) && a.destino.equals(origen))) {
                    a.peso = nuevoPeso;
                }
            }
            panel.repaint();
        } catch (ItemNotFound ex) {
            JOptionPane.showMessageDialog(this, "No se encontró la arista para modificar.");
        }
    }

    private void buscarVertice() {
        String nombre = JOptionPane.showInputDialog(this, "Buscar vértice:");
        if (nombre == null || nombre.isEmpty()) return;
        JOptionPane.showMessageDialog(this, vertices.search(nombre)?
                "Existe: " + nombre : "No existe: " + nombre);
    }

    private void buscarEdificio() {
        String edificio = JOptionPane.showInputDialog(this, "Nombre del edificio a buscar:");
        if (edificio == null || edificio.trim().isEmpty()) return;

        String zonaEncontrada = controller.findZoneOfDistrict(edificio.trim());

        if (zonaEncontrada != null) {
            JOptionPane.showMessageDialog(this,
                    "El edificio '" + edificio + "' se encuentra en la zona '" + zonaEncontrada + "'.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se encontró el edificio '" + edificio + "' en ninguna zona.");
        }
    }

    private void explorarBFS() {
        if (vertices.length() < 2) {
            JOptionPane.showMessageDialog(this, "Agrega al menos dos zonas.");
            return;
        }
        String origen = (String) JOptionPane.showInputDialog(this, "Zona origen:", "Exploración BFS",
                JOptionPane.PLAIN_MESSAGE, null, vertices.toArray(), null);
        String destino = (String) JOptionPane.showInputDialog(this, "Zona destino:", "Exploración BFS",
                JOptionPane.PLAIN_MESSAGE, null, vertices.toArray(), null);
        if (origen == null || destino == null) return;

        try {
            LinkedList<String> camino = controller.findPath(origen, destino);
            if (camino == null || camino.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró camino entre las zonas.");
            } else {
                StringBuilder ruta = new StringBuilder("Ruta BFS:\n");
                for (int i = 0; i < camino.length(); i++) {
                    ruta.append(camino.getNodeAtIndex(i));
                    if (i != camino.length() - 1) ruta.append(" -> ");
                }
                JOptionPane.showMessageDialog(this, ruta.toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en la búsqueda: " + e.getMessage());
        }
    }

    private void detectarCiclo() throws ExceptionIsEmpty {
        if (vertices.length() < 3) {
            JOptionPane.showMessageDialog(this, "Agrega al menos 3 zonas para detectar un ciclo.");
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Ingrese la zona seleccionada");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        boolean esCiclo = controller.isValidCycle(input);
        JOptionPane.showMessageDialog(this,
                esCiclo ? "Es un ciclo válido." : "No es un ciclo válido.");
    }

    private void mostrarComponentesConexas() {
        LinkedList<LinkedList<String>> componentes = controller.getConnectedComponents();
        StringBuilder sb = new StringBuilder("Componentes conexas:\n\n");

        for (int i = 0; i < componentes.length(); i++) {
            LinkedList<String> comp = componentes.getNodeAtIndex(i);
            sb.append("Componente ").append(i + 1).append(": ");
            for (int j = 0; j < comp.length(); j++) {
                sb.append(comp.getNodeAtIndex(j));
                if (j != comp.length() - 1) sb.append(", ");
            }
            sb.append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void mostrarZonasAisladas() {
        LinkedList<String> aisladas = controller.getIsolatedZones();
        if (aisladas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay urbanizaciones aisladas.");
            return;
        }

        StringBuilder sb = new StringBuilder("Urbanizaciones aisladas:\n");
        for (int i = 0; i < aisladas.length(); i++) {
            sb.append("- ").append(aisladas.getNodeAtIndex(i)).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void verificarSistemaConectado() {
        boolean conectado = controller.isSystemConnected();
        if (conectado) {
            JOptionPane.showMessageDialog(this, "Las urbanizaciones están completamente conectado.");
        } else {
            JOptionPane.showMessageDialog(this, "Las urbanizaciones NO están completamente conectado.");
        }
    }

    private void mostrarRutaMasCortaDijkstra() {
        if (vertices.length() < 2) {
            JOptionPane.showMessageDialog(this, "Agrega al menos dos zonas.");
            return;
        }

        String origen = (String) JOptionPane.showInputDialog(this,
                "Zona origen:", "Ruta más corta",
                JOptionPane.PLAIN_MESSAGE, null, vertices.toArray(), null);

        String destino = (String) JOptionPane.showInputDialog(this,
                "Zona destino:", "Ruta más corta",
                JOptionPane.PLAIN_MESSAGE, null, vertices.toArray(), null);

        if (origen == null || destino == null) return;

        try {
            LinkedList<String> camino = controller.findShortestPath(origen, destino);

            if (camino == null || camino.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró una ruta entre las zonas.");
            } else {
                StringBuilder ruta = new StringBuilder("Ruta más corta (Dijkstra):\n");
                for (int i = 0; i < camino.length(); i++) {
                    ruta.append(camino.getNodeAtIndex(i));
                    if (i != camino.length() - 1) ruta.append(" → ");
                }
                JOptionPane.showMessageDialog(this, ruta.toString());
            }
        } catch (ItemNotFound | ExceptionIsEmpty ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    class BotonVertice extends JComponent {
        private String nombre;
        private final Image imagen;

        BotonVertice(String nombre, ImageIcon icono) {
            this.nombre = nombre;
            this.imagen = icono.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            setPreferredSize(new Dimension(80, 100));
            setSize(80, 100);
            setOpaque(false);
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    try {
                        abrirVentanaVertice(BotonVertice.this.nombre); // siempre usa el valor actualizado
                    } catch (ItemNotFound e) {
                        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                    }
                }
            });
        }

        public void setNombre(String nuevo) {
            this.nombre = nuevo;
            repaint(); // Redibuja el componente para reflejar el nuevo nombre
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Dibuja la imagen
            int imgX = (getWidth() - 70) / 2;
            g2.drawImage(imagen, imgX, 0, 70, 70, this);

            // Formato del texto de los vertices
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.setColor(Color.WHITE);

            // Metrices del texto
            FontMetrics fm = g2.getFontMetrics();
            String texto = nombre;
            int maxWidth = getWidth();

            // Si el texto es más ancho que el componente, lo recortamos y añadimos "…"
            if (fm.stringWidth(texto) > maxWidth) {
                while (fm.stringWidth(texto + "…") > maxWidth && texto.length() > 0) {
                    texto = texto.substring(0, texto.length() - 1);
                }
                texto += "…";
            }

            int textWidth = fm.stringWidth(texto);
            int textX = (getWidth() - textWidth) / 2;
            int textY = 90;  // altura fija debajo de la imagen


            g2.drawString(texto, textX, textY);
        }
    }

    class DibujoPanel extends JPanel {

        private Image fondo;

        public DibujoPanel() {
            try {
                fondo = new ImageIcon(getClass().getResource("/ui/img/fondo.jpeg")).getImage();
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen de fondo.");
            }
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (fondo != null) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Valor de transparencia: 0.0 (completamente transparente) a 1.0 (opaco)
                float alpha = 0.9f;

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);

                g2d.dispose(); // Limpia el contexto de dibujo
            }

            if (vertices.length() == 0) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                String msg = "Agrega vértices";
                FontMetrics fm = g.getFontMetrics();
                g.drawString(msg, (getWidth()-fm.stringWidth(msg))/2, getHeight()/2);
                return;
            }
            int radio = Math.max(40, Math.min(80, getWidth()/20));
            int cX = getWidth()/2, cY = getHeight()/2;
            int m = radio+20;
            int R = Math.max(100, Math.min(cX-m, cY-m));
            double ang = vertices.length()==1?0:2*Math.PI/vertices.length();
            Point[] pts = new Point[vertices.length()];
            for (int i=0;i<vertices.length();i++) {
                int x = (vertices.length()==1?cX:(int)(cX+R*Math.cos(i*ang)));
                int y = (vertices.length()==1?cY:(int)(cY+R*Math.sin(i*ang)));
                pts[i]=new Point(x,y);
                JComponent comp = botonesVer.getNodeAtIndex(i);
                Dimension d = comp.getPreferredSize();
                comp.setBounds(x-d.width/2,y-d.height/2,d.width,d.height);
            }
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            for (int k=0;k<aristas.length();k++){
                AristaGrafica a=aristas.getNodeAtIndex(k);
                Point p1=pts[vertices.findIndex(a.origen)];
                Point p2=pts[vertices.findIndex(a.destino)];
                double dx=p2.x-p1.x,dy=p2.y-p1.y;
                double len=Math.hypot(dx,dy);if(len==0)continue;

                // Mientras el numero sea menor la fecha se alarga
                double t=40/len;
                int x1=(int)(p1.x+dx*t),y1=(int)(p1.y+dy*t);
                int x2=(int)(p2.x-dx*t),y2=(int)(p2.y-dy*t);

                //estilo de la flecha
                g2.setColor(new Color(100, 149, 237));
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                drawArrowLine(g2, x1, y1, x2, y2, 15, 10);

                // calcularse la posicion del peso
                double ux=dx/len,uy=dy/len;
                double nx=-uy,ny=ux;

                // → "50" es cuántos píxeles avanzas desde el origen
                // → "10" es el desplazamiento perpendicular
                int px=(int)(p1.x+ux*70+nx*20);
                int py=(int)(p1.y+uy*70+ny*20);
                String s=String.valueOf(a.peso);
                FontMetrics fm2=g2.getFontMetrics();
                int w=fm2.stringWidth(s),h=fm2.getHeight();

                // Modificacion del subrayado del peso
                g2.setColor(new Color(255, 215, 0));
                g2.fillRoundRect(px - w/2 - 5, py - h/2 - 2, w + 10, h + 4, 5, 5);
                
                // Tamaño del tipo de fuente del peso
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString(s, px - w/2, py + fm2.getAscent()/2 - 2);
            }
        }
        private void drawArrowLine(Graphics2D g,int x1,int y1,int x2,int y2,int d,int h){
            double dx=x2-x1,dy=y2-y1,D=Math.hypot(dx,dy);
            if(D==0)return;
            double xm=D-d,ym=h;
            double xn=xm,yn=-h;
            double sin=dy/D,cos=dx/D,x;
            x=xm*cos-ym*sin+x1;ym=xm*sin+ym*cos+y1;xm=x;
            x=xn*cos-yn*sin+x1;yn=xn*sin+yn*cos+y1;xn=x;
            int[] xp={x2,(int)xm,(int)xn};
            int[] yp={y2,(int)ym,(int)yn};
            g.drawLine(x1,y1,x2,y2);
            g.fillPolygon(xp,yp,3);
        }
    }
    static class AristaGrafica{String origen,destino;int peso;AristaGrafica(String o,String d,int p){origen=o;destino=d;peso=p;}}
}
