package Interfaz;

import Controller.Controller;
import Controller.Punto;
import Controller.Zona;
import DataStructure.Arbolito.BTree;
import DataStructure.Arbolito.BNode;
import Exceptions.ItemNotFound;

import javax.swing.*;
import java.awt.*;

public class InterfazArbol extends JFrame {
    private final Controller controller;
    private final Zona zona;

    public InterfazArbol(Controller controller, Zona zona) {
        super("Árbol B de " + zona.getNombre());
        this.controller = controller;
        this.zona = zona;

        // Validar zona
        if (controller.buscarZona(zona.getNombre()) == null) {
            JOptionPane.showMessageDialog(this, "Zona no encontrada: " + zona.getNombre());
            dispose();
            return;
        }

        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ------ Panel de dibujo -------
        ArbolPanel panel = new ArbolPanel();
        add(new JScrollPane(panel), BorderLayout.CENTER);

        // ------ Botones abajo -------
        JPanel botones = new JPanel();
        botones.add(crearBoton("Agregar Edificio", () -> accionAgregar(panel)));
        botones.add(crearBoton("Eliminar Edificio", () -> accionEliminar(panel)));
        botones.add(crearBoton("Buscar Edificio", this::accionBuscar));
        add(botones, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton crearBoton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> accion.run());
        return b;
    }

    private void accionAgregar(ArbolPanel panel) {
        String name = JOptionPane.showInputDialog(this, "Nuevo edificio:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                boolean ok = controller.addDistrictToZone(zona.getNombre(), name);
                if (!ok) JOptionPane.showMessageDialog(this, "Edificio ya existe.");
            } catch (ItemNotFound ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
            panel.repaint();
        }
    }

    private void accionEliminar(ArbolPanel panel) {
        String name = JOptionPane.showInputDialog(this, "Edificio a eliminar:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                boolean ok = controller.removeDistrictFromZone(zona.getNombre(), name);
                if (!ok) JOptionPane.showMessageDialog(this, "Edificio no existe.");
            } catch (ItemNotFound ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
            panel.repaint();
        }
    }

    private void accionBuscar() {
        String name = JOptionPane.showInputDialog(this, "Edificio a buscar:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                boolean existe = controller.searchDistrictInZone(zona.getNombre(), name);
                JOptionPane.showMessageDialog(this, existe ? "Edificio existe" : "No encontrado");
            } catch (ItemNotFound ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private class ArbolPanel extends JPanel {
        private final Image imgEd;

        public ArbolPanel() {
            // cargar la imagen una sola vez
            ImageIcon iconEd = new ImageIcon(getClass().getResource("/Interfaz/img/casa.png"));
            imgEd = iconEd.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            BTree<Punto> tree = zona.getArbolDistrito();
            if (tree.getRoot() == null) {
                g.drawString("Árbol vacío.", 20, 20);
            } else {
                dibujarNodo(g, tree.getRoot(), getWidth() / 2, 30, getWidth() / 4);
            }
        }

        private void dibujarNodo(Graphics g, BNode<Punto> nodo, int x, int y, int offsetX) {
            if (nodo == null) return;

            int c = nodo.count;
            FontMetrics fm = g.getFontMetrics();

            // Extrae las claves sin castear el arreglo completo
            Punto[] claves = new Punto[c];
            Object[] rawKeys = nodo.keys; // es Comparable[], lo tratamos como Object[]
            for (int i = 0; i < c; i++) {
                claves[i] = (Punto) rawKeys[i];
            }

            // Extrae los hijos
            BNode<Punto>[] hijos = (BNode<Punto>[]) new BNode[c+1];  // arreglo vacío
            Object[] rawHijos = nodo.childs; // es BNode[]
            for (int i = 0; i <= c; i++) {
                hijos[i] = (BNode<Punto>) rawHijos[i];
            }

            // --- luego dibujas edificios igual que antes ---
            int ancho = c * 50;
            int startX = x - ancho/2;
            for (int i = 0; i < c; i++) {
                int ix = startX + i * 50;
                int iy = y;
                g.drawImage(imgEd, ix, iy, this);
                String label = claves[i].toString();
                int textW = fm.stringWidth(label);
                int tx = ix + (40 - textW) / 2;
                int ty = iy + 40 + fm.getAscent() + 2;
                g.drawString(label, tx, ty);
            }

            // líneas a hijos
            for (int i = 0; i <= c; i++) {
                BNode<Punto> h = hijos[i];
                if (h != null) {
                    int idx = Math.min(i, c-1);
                    int px = startX + idx * 50 + 20;
                    int py = y + 40 + fm.getAscent()/2;
                    int childX = x - offsetX + i * (2*offsetX/(c+1));
                    int childY = y + 100;
                    g.drawLine(px, py, childX, childY);
                    dibujarNodo(g, h, childX, childY, offsetX/2);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 600);
        }
    }
}