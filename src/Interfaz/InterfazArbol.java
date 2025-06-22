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
    private final Zona zoneName;

    public InterfazArbol(Controller controller, Zona zoneName) {
        super("Árbol B+ de " + zoneName);
        this.controller = controller;
        this.zoneName = zoneName;

        // Valida que la zona exista
        Zona zona = controller.buscarZona(zoneName.getNombre());
        if (zona == null) {
            JOptionPane.showMessageDialog(this, "Zona no encontrada: " + zoneName);
            dispose();
            return;
        }

        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel de dibujo
        ArbolPanel panel = new ArbolPanel();
        add(new JScrollPane(panel), BorderLayout.CENTER);

        // Botones
        JPanel botones = new JPanel();
        botones.add(crearBoton("Agregar Distrito", () -> acciónAgregar(panel)));
        botones.add(crearBoton("Eliminar Distrito", () -> acciónEliminar(panel)));
        botones.add(crearBoton("Buscar Distrito", this::acciónBuscar));
        add(botones, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto, Runnable acción) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> acción.run());
        return b;
    }

    private void acciónAgregar(ArbolPanel panel) {
        String name = JOptionPane.showInputDialog(this, "Distrito:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                boolean ok = controller.addDistrictToZone(zoneName.getNombre(), name);
                if (!ok) JOptionPane.showMessageDialog(this, "Distrito ya existe.");
            } catch (ItemNotFound ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
            panel.repaint();
        }
    }

    private void acciónEliminar(ArbolPanel panel) {
        String name = JOptionPane.showInputDialog(this, "Distrito a eliminar:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                boolean ok = controller.removeDistrictFromZone(zoneName.getNombre(), name);
                if (!ok) JOptionPane.showMessageDialog(this, "Distrito no existe.");
            } catch (ItemNotFound ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
            panel.repaint();
        }
    }

    private void acciónBuscar() {
        String name = JOptionPane.showInputDialog(this, "Distrito a buscar:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                boolean existe = controller.searchDistrictInZone(zoneName.getNombre(), name);
                JOptionPane.showMessageDialog(this, existe ? "Existe" : "No encontrado");
            } catch (ItemNotFound ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private class ArbolPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            BTree<Punto> tree = controller.buscarZona(zoneName.getNombre()).getArbolDistrito();
            if (tree.getRoot() == null) {
                g.drawString("Árbol vacío.", 20, 20);
            } else {
                dibujarNodo(g, tree.getRoot(), getWidth()/2, 30, getWidth()/4);
            }
        }

        private void dibujarNodo(Graphics g, BNode<Punto> nodo, int x, int y, int offX) {
            if (nodo == null) return;
            int c = nodo.count;

            // Copiamos las celdas de claves
            Punto[] claves = new Punto[c];
            System.arraycopy(nodo.keys, 0, claves, 0, c);

            // Dibujo del nodo
            int w = 30*c + 20;
            g.setColor(Color.WHITE);
            g.fillRect(x - w/2, y, w, 25);
            g.setColor(Color.BLACK);
            g.drawRect(x - w/2, y, w, 25);
            for (int i = 0; i < c; i++) {
                g.drawString(claves[i].toString(),
                        x - w/2 + 10 + 30*i, y + 17);
            }

            // Dibujo recursivo de hijos
            for (int i = 0; i <= c; i++) {
                BNode<Punto> h = nodo.childs[i];
                if (h != null) {
                    int cx = x - offX + i * (2*offX/(c+1));
                    int cy = y + 70;
                    g.drawLine(x, y+25, cx, cy);
                    dibujarNodo(g, h, cx, cy, offX/2);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 600);
        }
    }
}
