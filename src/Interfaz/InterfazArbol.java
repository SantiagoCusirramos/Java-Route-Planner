package Interfaz;

import Controller.Punto;
import Controller.Zona;
import DataStructure.Arbolito.BTree;
import DataStructure.Arbolito.BNode;

import javax.swing.*;
import java.awt.*;

public class InterfazArbol extends JFrame {
    private Zona zona;
    private BTree<Punto> arbol;

    public InterfazArbol(Zona zona) {
        this.zona = zona;
        this.arbol = zona.getArbolDistrito();

        setTitle("Árbol de " + zona.getNombre());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        ArbolPanel panelArbol = new ArbolPanel();
        JScrollPane scroll = new JScrollPane(panelArbol);
        add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel();

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Ingrese nombre del distrito:");
            if (nombre != null && !nombre.trim().isEmpty()) {
                arbol.insert(new Punto(nombre));
                repaint();
            }
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre del distrito a eliminar:");
            if (nombre != null && !nombre.trim().isEmpty()) {
                arbol.remove(new Punto(nombre));
                repaint();
            }
        });

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre del distrito a buscar:");
            if (nombre != null && !nombre.trim().isEmpty()) {
                boolean existe = arbol.search(new Punto(nombre));
                JOptionPane.showMessageDialog(this, existe ? "Existe" : "No encontrado");
            }
        });

        botones.add(btnAgregar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);

        add(botones, BorderLayout.SOUTH);
    }

    class ArbolPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (arbol.getRoot() == null) {
                g.drawString("Árbol vacío.", getWidth() / 2 - 40, getHeight() / 2);
            } else {
                dibujarNodo(g, arbol.getRoot(), getWidth() / 2, 50, getWidth() / 4);
            }
        }

        private void dibujarNodo(Graphics g, BNode<Punto> nodo, int x, int y, int offsetX) {
            if (nodo == null) return;

            int claveCount = nodo.count;
            Punto[] claves = nodo.getKeys();
            BNode<Punto>[] hijos = nodo.getChilds();

            int anchoNodo = 50 + 30 * claveCount;
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x - anchoNodo / 2, y, anchoNodo, 30);
            g.setColor(Color.BLACK);
            g.drawRect(x - anchoNodo / 2, y, anchoNodo, 30);

            for (int i = 0; i < claveCount; i++) {
                String val = claves[i].toString();
                g.drawString(val, x - anchoNodo / 2 + 10 + 30 * i, y + 20);
            }

            for (int i = 0; i <= claveCount; i++) {
                if (hijos[i] != null) {
                    int hijoX = x - offsetX + i * (offsetX * 2 / (claveCount + 1));
                    int hijoY = y + 80;
                    g.drawLine(x, y + 30, hijoX, hijoY);
                    dibujarNodo(g, hijos[i], hijoX, hijoY, offsetX / 2);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1000, 800);
        }
    }
}
