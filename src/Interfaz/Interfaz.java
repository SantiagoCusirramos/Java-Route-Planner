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
        setSize(1200, 1000);
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

        botones.add(btnAgregarVertice);
        botones.add(btnAgregarArista);
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
        JFrame ventana = new JFrame("Vértice: " + nombre);
        ventana.setSize(300, 150);
        ventana.setLocationRelativeTo(this);
        ventana.add(new JLabel("Este es el vértice: " + nombre, SwingConstants.CENTER));
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

    class DibujoPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Verificar si hay vértices para dibujar
            if (vertices.length() == 0) {
                // Opcional: dibujar un mensaje cuando no hay vértices
                g.setColor(Color.GRAY);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                String mensaje = "Agrega vértices para comenzar";
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(mensaje)) / 2;
                int y = getHeight() / 2;
                g.drawString(mensaje, x, y);
                return;
            }

            // Calcular dimensiones dinámicamente
            int radioBoton = Math.max(40, Math.min(80, getWidth() / 20)); // Radio adaptativo
            int centroX = getWidth() / 2;
            int centroY = getHeight() / 2;
            
            // Radio del círculo que contiene los vértices (adaptativo al tamaño de pantalla)
            int margen = radioBoton + 20;
            int r = Math.min(centroX - margen, centroY - margen);
            
            // Asegurar un radio mínimo
            r = Math.max(r, 100);
            
            // Manejo especial para un solo vértice
            double angulo;
            if (vertices.length() == 1) {
                angulo = 0; // No importa el ángulo para un solo vértice
            } else {
                angulo = 2 * Math.PI / vertices.length();
            }

            // Posiciones de los vértices
            Point[] posiciones = new Point[vertices.length()];
            for (int i = 0; i < vertices.length(); i++) {
                int x, y;
                if (vertices.length() == 1) {
                    // Centrar el único vértice
                    x = centroX;
                    y = centroY;
                } else {
                    x = (int) (centroX + r * Math.cos(i * angulo));
                    y = (int) (centroY + r * Math.sin(i * angulo));
                }
                posiciones[i] = new Point(x, y);

                // Reposicionar botón con tamaño adaptativo
                JButton btn = botonesVer.getNodeAtIndex(i);
                int anchoBoton = Math.max(60, radioBoton + 10);
                int altoBoton = Math.max(25, radioBoton / 2);
                btn.setBounds(x - anchoBoton/2, y - altoBoton/2, anchoBoton, altoBoton);
                
                // Ajustar tamaño de fuente del botón
                int fontSize = Math.max(8, Math.min(14, radioBoton / 5));
                btn.setFont(new Font("Arial", Font.BOLD, fontSize));
            }

            // Dibujar aristas
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (int k = 0; k < aristas.length(); k++) {
                AristaGrafica a = aristas.getNodeAtIndex(k);
                int i = vertices.findIndex(a.origen);
                int j = vertices.findIndex(a.destino);

                if (i != -1 && j != -1) { // Verificar que los vértices existan
                    Point p1 = posiciones[i];
                    Point p2 = posiciones[j];

                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    drawArrowLine(g2d, p1.x, p1.y, p2.x, p2.y, 12, 8);

                    // Dibujar peso de la arista
                    int midX = (p1.x + p2.x) / 2;
                    int midY = (p1.y + p2.y) / 2;
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Arial", Font.BOLD, Math.max(10, radioBoton / 6)));
                    
                    // Fondo blanco para el peso
                    String pesoStr = String.valueOf(a.peso);
                    FontMetrics fm = g2d.getFontMetrics();
                    int pesoWidth = fm.stringWidth(pesoStr);
                    int pesoHeight = fm.getHeight();
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(midX - pesoWidth/2 - 2, midY - pesoHeight/2 - 2, pesoWidth + 4, pesoHeight);
                    g2d.setColor(Color.RED);
                    g2d.drawString(pesoStr, midX - pesoWidth/2, midY + fm.getAscent()/2);
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
