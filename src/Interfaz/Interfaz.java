package Interfaz;

import javax.swing.*;
import java.awt.*;

import Controller.Controller;
import DataStructure.LinkedList.LinkedList;
import Exceptions.ItemDuplicated;
import Exceptions.ItemNotFound;

public class Interfaz extends JFrame {
    private Controller controller;
    private LinkedList<String> vertices;
    private LinkedList<AristaGrafica> aristas;
    private LinkedList<JButton> botonesVer;
    private DibujoPanel panel;

    public Interfaz(){

        this.controller = new Controller();
        vertices = new LinkedList<String>();
        aristas = new LinkedList<AristaGrafica>();
        botonesVer = new LinkedList<JButton>();

        setTitle("Editor de Grafo");
        setSize(1200, 770);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        panel = new DibujoPanel();
        panel.setLayout(null);
        add(panel, BorderLayout.CENTER);

        // Agregar listener para redimensionamiento
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                panel.repaint();
            }
        });

        JPanel botones = new JPanel();

        JButton btnAgregarVertice = new JButton("Agregar vertice");
        btnAgregarVertice.addActionListener(e -> {
            try {
                agregarVertice();
            } catch (ItemDuplicated ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnAgregarArista = new JButton("Agregar arista");
        btnAgregarArista.addActionListener(e -> {
            try {
                agregarArista();
            } catch (ItemNotFound ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton btnEliminarVertice = new JButton("Eliminar vertice");
        btnEliminarVertice.addActionListener(e -> {
            try {
                eliminiarVertice();
            }catch (ItemNotFound ex){
                throw new RuntimeException(ex);
            }
        });

        JButton btnEliminarArista = new JButton("Eliminar arista");
        btnEliminarArista.addActionListener(e -> {
            try {
                eliminarArista();
            }catch (ItemNotFound ex){
                throw new RuntimeException(ex);
            }
        });

        JButton btnBuscarVertice = new JButton("Buscar vertice");
        btnBuscarVertice.addActionListener(e -> buscarVertice());

        botones.add(btnAgregarVertice);
        botones.add(btnAgregarArista);
        botones.add(btnEliminarVertice);
        botones.add(btnEliminarArista);
        botones.add(btnBuscarVertice);
        add(botones, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void agregarVertice() throws ItemDuplicated {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese nombre del vertice:");
        if (nombre != null && !nombre.isEmpty()) {
            controller.addZone(nombre);
            vertices.insertLast((nombre));

            JButton btn = new JButton(nombre);
            btn.setBounds(0,0,60,30);
            btn.addActionListener(e -> abrirVentanaVertice(nombre));
            botonesVer.insertLast(btn);
            panel.add(btn);
            panel.repaint();
        }
    }

    private void abrirVentanaVertice(String nombre) {
        JFrame ventana = new JFrame("vertice: " + nombre);
        ventana.setSize(300, 150);
        ventana.setLocationRelativeTo(this);
        ventana.add(new JLabel("Este es el vertice: " + nombre, SwingConstants.CENTER));
        ventana.setVisible(true);
    }


    private void agregarArista() throws ItemNotFound {
        if (vertices.length() < 2) {
            JOptionPane.showMessageDialog(this, "Agrega al menos dos vertices.");
            return;
        }

        String[] vs = new String[vertices.length()];
        for(int i = 0; i<vs.length; i++){
            vs[i] = vertices.getNodeAtIndex(i);
        }
        // 94 - 95 -> toArray
        String origen = (String) JOptionPane.showInputDialog(this, "Origen:", "Vertices", JOptionPane.PLAIN_MESSAGE, null, vs, null);
        String destino = (String) JOptionPane.showInputDialog(this, "Destino:", "Vertices", JOptionPane.PLAIN_MESSAGE, null, vs, null);

        if (origen == null || destino == null) return;

        int peso = 1;
        try {
            peso = Integer.parseInt(JOptionPane.showInputDialog(this, "Ingrese el peso de la arista:"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Peso invalido. Se usara 1");
        }

        controller.connectZones(origen, destino, peso);
        aristas.insertLast(new AristaGrafica(origen, destino, peso));
        panel.repaint();

    }

    public  void eliminiarVertice() throws ItemNotFound {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese nombre del vertice:");
        if (nombre == null || nombre.isEmpty()) return;

        if (!vertices.search(nombre)) {
            JOptionPane.showMessageDialog(this, "Vertice no encontrado.");
            return;
        }

        // Eliminar del controlador
        controller.removeZone(nombre);
        int index = vertices.findIndex(nombre);

        vertices.removeNode(nombre);
        botonesVer.removeAtIndex(index);

        // Eliminar las aristas relacionadas
        for (int i = 0; i < aristas.length(); ) {
            AristaGrafica a = aristas.getNodeAtIndex(i);
            if (a.origen.equals(nombre) || a.destino.equals(nombre)) {
                aristas.removeAtIndex(i); // No incrementar i porque la lista se mueve
            } else {
                i++;
            }
        }

        panel.removeAll(); // Borra todos los botones de la vista
        for (int i = 0; i < botonesVer.length(); i++) {
            panel.add(botonesVer.getNodeAtIndex(i)); // Agrega los botones actualizados
        }

        panel.repaint();
    }

    private void eliminarArista() throws ItemNotFound {
        if (aristas.length() == 0) {
            JOptionPane.showMessageDialog(this, "No hay aristas para eliminar.");
            return;
        }

        String[] vs = new String[vertices.length()];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = vertices.getNodeAtIndex(i);
        }

        String origen = (String) JOptionPane.showInputDialog(this, "Origen:", "Eliminar arista", JOptionPane.PLAIN_MESSAGE, null, vs, null);
        String destino = (String) JOptionPane.showInputDialog(this, "Destino:", "Eliminar arista", JOptionPane.PLAIN_MESSAGE, null, vs, null);

        if (origen == null || destino == null) return;

        controller.disconnectZones(origen, destino);

        for (int i = 0; i < aristas.length(); i++) {
            AristaGrafica a = aristas.getNodeAtIndex(i);
            if (a.origen.equals(origen) && a.destino.equals(destino)) {
                aristas.removeAtIndex(i);
                break;
            }
        }

        panel.repaint();
    }

    private void buscarVertice() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del vertice a buscar:");
        if (nombre == null || nombre.isEmpty()) return;

        if (vertices.search(nombre)) {
            JOptionPane.showMessageDialog(this, "El vertice '" + nombre + "' SÍ existe.");
        } else {
            JOptionPane.showMessageDialog(this, "El vertice '" + nombre + "' NO existe.");
        }
    }



    class DibujoPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Verificar si hay vertices para dibujar
            if (vertices.length() == 0) {
                g.setColor(Color.GRAY);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                String mensaje = "Agrega vertices para comenzar";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(mensaje)) / 2;
                int y = getHeight() / 2;
                g.drawString(mensaje, x, y);
                return;
            }

            int radioBoton = Math.max(40, Math.min(80, getWidth() / 20));
            int centroX = getWidth() / 2;
            int centroY = getHeight() / 2;
            int margen = radioBoton + 20;
            int r = Math.min(centroX - margen, centroY - margen);
            r = Math.max(r, 100);

            double angulo = (vertices.length() == 1) ? 0 : 2 * Math.PI / vertices.length();

            Point[] posiciones = new Point[vertices.length()];
            for (int i = 0; i < vertices.length(); i++) {
                int x, y;
                if (vertices.length() == 1) {
                    x = centroX;
                    y = centroY;
                } else {
                    x = (int) (centroX + r * Math.cos(i * angulo));
                    y = (int) (centroY + r * Math.sin(i * angulo));
                }
                posiciones[i] = new Point(x, y);

                JButton btn = botonesVer.getNodeAtIndex(i);
                int anchoBoton = Math.max(60, radioBoton + 10);
                int altoBoton = Math.max(25, radioBoton / 2);
                btn.setBounds(x - anchoBoton / 2, y - altoBoton / 2, anchoBoton, altoBoton);

                int fontSize = Math.max(8, Math.min(14, radioBoton / 5));
                btn.setFont(new Font("Arial", Font.BOLD, fontSize));
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int k = 0; k < aristas.length(); k++) {
                AristaGrafica a = aristas.getNodeAtIndex(k);
                int i = vertices.findIndex(a.origen);
                int j = vertices.findIndex(a.destino);

                if (i != -1 && j != -1) {
                    Point p1 = posiciones[i];
                    Point p2 = posiciones[j];

                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double len = Math.sqrt(dx * dx + dy * dy);
                    if (len == 0) continue;

                    // Recortar para que no se dibuje debajo del botón
                    double t = 30.0 / len;
                    int recorteX1 = (int) (p1.x + dx * t);
                    int recorteY1 = (int) (p1.y + dy * t);
                    int recorteX2 = (int) (p2.x - dx * t);
                    int recorteY2 = (int) (p2.y - dy * t);

                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    drawArrowLine(g2d, recorteX1, recorteY1, recorteX2, recorteY2, 12, 8);

                    // Detectar si existe la arista inversa
                    boolean esBidireccional = false;
                    for (int l = 0; l < aristas.length(); l++) {
                        AristaGrafica otra = aristas.getNodeAtIndex(l);
                        if (otra.origen.equals(a.destino) && otra.destino.equals(a.origen)) {
                            esBidireccional = true;
                            break;
                        }
                    }

                    // Calcular punto medio con desplazamiento si es bidireccional
                    double offset = esBidireccional ? 15 : 0;
                    double px = -dy / len * offset;
                    double py = dx / len * offset;

                    int midX = (int) ((p1.x + p2.x) / 2 + px);
                    int midY = (int) ((p1.y + p2.y) / 2 + py);

                    // Dibujar peso
                    String pesoStr = String.valueOf(a.peso);
                    FontMetrics fm = g2d.getFontMetrics();
                    int pesoWidth = fm.stringWidth(pesoStr);
                    int pesoHeight = fm.getHeight();

                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(midX - pesoWidth / 2 - 2, midY - pesoHeight / 2 - 2, pesoWidth + 4, pesoHeight);
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Arial", Font.BOLD, Math.max(10, radioBoton / 6)));
                    g2d.drawString(pesoStr, midX - pesoWidth / 2, midY + fm.getAscent() / 2);
                }
            }
        }

        private void drawArrowLine(Graphics2D g, int x1, int y1, int x2, int y2, int d, int h) {
            int dx = x2 - x1, dy = y2 - y1;
            double D = Math.sqrt(dx * dx + dy * dy);
            
            // Evitar división por cero si los puntos son el mismo
            if (D == 0) return;
            
            double xm = D - d, xn = xm, ym = h, yn = -h, x;
            double sin = dy / D, cos = dx / D;

            x = xm * cos - ym * sin + x1;
            ym = xm * sin + ym * cos + y1;
            xm = x;

            x = xn * cos - yn * sin + x1;
            yn = xn * sin + yn * cos + y1;
            xn = x;

            int[] xpoints = {x2, (int) xm, (int) xn};
            int[] ypoints = {y2, (int) ym, (int) yn};

            g.drawLine(x1, y1, x2, y2);
            g.fillPolygon(xpoints, ypoints, 3);
        }
    }

    static class AristaGrafica {
        String origen, destino;
        int peso;

        AristaGrafica(String o, String d, int p) {
            origen = o;
            destino = d;
            peso = p;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Interfaz::new);
    }
}
