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

            int radio = 60;
            int centroX = getWidth() / 2;
            int centroY = getHeight() / 2;
            int r = Math.min(centroX, centroY) - (vertices.length() > 10 ? 100 : 60);
            double angulo = 2 * Math.PI / vertices.length();

            // Posiciones de los vértices
            Point[] posiciones = new Point[vertices.length()];
            for (int i = 0; i < vertices.length(); i++) {
                int x = (int) (centroX + r * Math.cos(i * angulo));
                int y = (int) (centroY + r * Math.sin(i * angulo));
                posiciones[i] = new Point(x, y);

                JButton btn = botonesVer.getNodeAtIndex(i);
                btn.setBounds(x-radio/2, y-radio/2, radio, 30);
            }

            // Dibujar aristas
            for (int k = 0; k < aristas.length(); k++) {
                AristaGrafica a = aristas.getNodeAtIndex(k);
                int i = vertices.findIndex(a.origen);
                int j = vertices.findIndex(a.destino);

                Point p1 = posiciones[i];
                Point p2 = posiciones[j];

                g.setColor(Color.BLACK);
                drawArrowLine(g, p1.x, p1.y, p2.x, p2.y, 10, 6);

                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                g.setColor(Color.RED);
                g.drawString(String.valueOf(a.peso), midX, midY);
            }
        }

        private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
            int dx = x2 - x1, dy = y2 - y1;
            double D = Math.sqrt(dx * dx + dy * dy);
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
