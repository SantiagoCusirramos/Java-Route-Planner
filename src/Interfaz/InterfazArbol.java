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
    private final String zoneName;
    private BTree<Punto> arbol;

    public InterfazArbol(String zoneName) {
        this.controller = new Controller();
        this.zoneName = zoneName;

        // Obtén la zona y su árbol
        Zona zona = controller.buscarZona(zoneName);
        if (zona == null) {
            JOptionPane.showMessageDialog(null, "Zona no encontrada: " + zoneName);
            dispose();
            return;
        }
        this.arbol = zona.getArbolDistrito();

        setTitle("Árbol B+ de " + zoneName);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Panel de dibujo
        ArbolPanel panel = new ArbolPanel();
        JScrollPane scroll = new JScrollPane(panel);
        add(scroll, BorderLayout.CENTER);

        // Botones: Agregar, Eliminar, Buscar
        JPanel botones = new JPanel();

        JButton btnAgregar = new JButton("Agregar Distrito");
        btnAgregar.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Nombre del distrito:");
            if (name != null && !name.trim().isEmpty()) {
                try {
                    boolean ok = controller.addDistrictToZone(zoneName, name);
                    if (!ok) {
                        JOptionPane.showMessageDialog(this, "Distrito ya existe.");
                    }
                } catch (ItemNotFound ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
                panel.repaint();
            }
        });

        JButton btnEliminar = new JButton("Eliminar Distrito");
        btnEliminar.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Nombre del distrito a eliminar:");
            if (name != null && !name.trim().isEmpty()) {
                try {
                    boolean ok = controller.removeDistrictFromZone(zoneName, name);
                    if (!ok) {
                        JOptionPane.showMessageDialog(this, "Distrito no existe.");
                    }
                } catch (ItemNotFound ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
                panel.repaint();
            }
        });

        JButton btnBuscar = new JButton("Buscar Distrito");
        btnBuscar.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Nombre del distrito a buscar:");
            if (name != null && !name.trim().isEmpty()) {
                try {
                    boolean existe = controller.searchDistrictInZone(zoneName, name);
                    JOptionPane.showMessageDialog(this, existe ? "Distrito existe" : "No encontrado");
                } catch (ItemNotFound ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });

        botones.add(btnAgregar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);
        add(botones, BorderLayout.SOUTH);
    }

    private class ArbolPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Zona zona = controller.buscarZona(zoneName);
            arbol = zona.getArbolDistrito();

            if (arbol.getRoot() == null) {
                g.drawString("Árbol vacío.", 20, 20);
            } else {
                dibujarNodo(g, arbol.getRoot(), getWidth() / 2, 30, getWidth() / 4);
            }
        }

        private void dibujarNodo(Graphics g, BNode<Punto> nodo, int x, int y, int offsetX) {
            if (nodo == null) return;

            int c = nodo.count;
            // keys[] es Comparable[], casteamos uno a uno
            Comparable<?>[] rawKeys = nodo.getKeys();
            Punto[] claves = new Punto[c];
            for (int i = 0; i < c; i++) {
                claves[i] = (Punto) rawKeys[i];
            }
            BNode<Punto>[] hijos = nodo.getChilds();

            // Dibuja el rectángulo del nodo
            int ancho = 30 * c + 20;
            g.setColor(Color.WHITE);
            g.fillRect(x - ancho/2, y, ancho, 25);
            g.setColor(Color.BLACK);
            g.drawRect(x - ancho/2, y, ancho, 25);
            for (int i = 0; i < c; i++) {
                g.drawString(claves[i].toString(), x - ancho/2 + 10 + 30*i, y + 17);
            }

            // Dibuja recursivamente hijos
            for (int i = 0; i <= c; i++) {
                BNode<Punto> h = hijos[i];
                if (h != null) {
                    int childX = x - offsetX + i * (2*offsetX/(c+1));
                    int childY = y + 70;
                    g.drawLine(x, y + 25, childX, childY);
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
