import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class CityGraphVisualizer extends JPanel {

    private double scale = 1.0;

    static class Building {
        String name;
        Point2D.Double position;
        Color color;

        public Building(String name, double x, double y, Color color) {
            this.name = name;
            this.position = new Point2D.Double(x, y);
            this.color = color;
        }
    }

    static class Connection {
        int from;
        int to;

        public Connection(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    private java.util.List<Building> buildings = new ArrayList<>();
    private java.util.List<Connection> connections = new ArrayList<>();

    public CityGraphVisualizer() {
        // Agregar 15 edificios con posiciones y colores variados
        buildings.add(new Building("Centro Financiero", 450, 450, new Color(255, 69, 0)));
        buildings.add(new Building("Residencial A", 200, 400, new Color(60, 179, 113)));
        buildings.add(new Building("Parque Central", 650, 350, new Color(34, 139, 34)));
        buildings.add(new Building("Comercial B", 750, 600, new Color(255, 215, 0)));
        buildings.add(new Building("Hospital", 300, 600, new Color(220, 20, 60)));
        buildings.add(new Building("Estación de tren", 550, 700, new Color(70, 130, 180)));
        buildings.add(new Building("Universidad", 150, 150, new Color(138, 43, 226)));
        buildings.add(new Building("Museo", 600, 150, new Color(255, 140, 0)));
        buildings.add(new Building("Biblioteca", 850, 200, new Color(199, 21, 133)));
        buildings.add(new Building("Parque Infantil", 100, 500, new Color(0, 191, 255)));
        buildings.add(new Building("Plaza Mayor", 500, 100, new Color(255, 105, 180)));
        buildings.add(new Building("Aeropuerto", 900, 500, new Color(105, 105, 105)));
        buildings.add(new Building("Policía", 400, 750, new Color(0, 0, 128)));
        buildings.add(new Building("Estadio", 700, 700, new Color(178, 34, 34)));
        buildings.add(new Building("Centro Comercial", 300, 300, new Color(0, 128, 128)));

        // Definir conexiones entre edificios (índices en la lista)
        // Cada edificio conectado con al menos dos otros
        connections.add(new Connection(0, 1));
        connections.add(new Connection(0, 2));
        connections.add(new Connection(1, 9));
        connections.add(new Connection(1, 14));
        connections.add(new Connection(2, 7));
        connections.add(new Connection(2, 3));
        connections.add(new Connection(3, 11));
        connections.add(new Connection(3, 13));
        connections.add(new Connection(4, 9));
        connections.add(new Connection(4, 12));
        connections.add(new Connection(5, 13));
        connections.add(new Connection(5, 12));
        connections.add(new Connection(6, 10));
        connections.add(new Connection(6, 1));
        connections.add(new Connection(7, 10));
        connections.add(new Connection(7, 8));
        connections.add(new Connection(8, 11));
        connections.add(new Connection(9, 14));
        connections.add(new Connection(10, 0));
        connections.add(new Connection(11, 13));
        connections.add(new Connection(12, 4));
        connections.add(new Connection(13, 14));
        connections.add(new Connection(14, 1));
        connections.add(new Connection(14, 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.scale(scale, scale);

        drawGrid(g2);

        drawConnections(g2);

        drawBuildings(g2);

        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(200, 200, 200, 70));
        int step = 100;
        for (int x = 0; x < getWidth() / scale; x += step) {
            g2.drawLine(x, 0, x, (int)(getHeight() / scale));
        }
        for (int y = 0; y < getHeight() / scale; y += step) {
            g2.drawLine(0, y, (int)(getWidth() / scale), y);
        }
    }

    private void drawBuildings(Graphics2D g2) {
        int w = 70;
        int h = 70;

        for (Building b : buildings) {
            int x = (int) b.position.x - w / 2;
            int y = (int) b.position.y - h / 2;

            GradientPaint gp = new GradientPaint(x, y, b.color.brighter(), x, y + h, b.color.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, w, h, 15, 15);

            g2.setColor(b.color.darker().darker());
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(x, y, w, h, 15, 15);

            g2.setColor(new Color(255, 255, 255, 200));
            for (int i = 10; i < w - 10; i += 18) {
                for (int j = 10; j < h - 10; j += 20) {
                    g2.fillRect(x + i, y + j, 10, 12);
                }
            }

            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillOval(x + 10, y + h - 10, w - 20, 15);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(b.name);

            int textX = (int) b.position.x - textWidth / 2;
            int textY = (int) b.position.y + h / 2 + 20;

            g2.setColor(new Color(0, 0, 0, 100));
            g2.drawString(b.name, textX + 2, textY + 2);

            g2.setColor(Color.BLACK);
            g2.drawString(b.name, textX, textY);
        }
    }

    private void drawConnections(Graphics2D g2) {
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(30, 120, 220, 200));

        for (Connection c : connections) {
            Building from = buildings.get(c.from);
            Building to = buildings.get(c.to);

            Point2D.Double p1 = getBuildingCenter(from);
            Point2D.Double p2 = getBuildingCenter(to);

            drawArrow(g2, p1, p2);

            int peso = getWeight(from, to);
            drawWeightLabel(g2, p1, p2, peso);
        }
    }

    private Point2D.Double getBuildingCenter(Building b) {
        return new Point2D.Double(b.position.x, b.position.y);
    }

    private int getWeight(Building a, Building b) {
        double dx = (a.position.x - b.position.x);
        double dy = (a.position.y - b.position.y);
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy) * 10);
    }

    private void drawWeightLabel(Graphics2D g2, Point2D.Double from, Point2D.Double to, int weight) {
        double midX = (from.x + to.x) / 2;
        double midY = (from.y + to.y) / 2;

        double offsetX = (to.y - from.y) * 0.15;
        double offsetY = (from.x - to.x) * 0.15;

        int labelX = (int) (midX + offsetX);
        int labelY = (int) (midY + offsetY);

        String text = String.valueOf(weight);

        Font font = new Font("Segoe UI", Font.BOLD, 14);
        FontMetrics fm = g2.getFontMetrics(font);
        int width = fm.stringWidth(text);
        int height = fm.getHeight();

        g2.setColor(new Color(255, 255, 255, 180));
        g2.fillRoundRect(labelX - width / 2 - 6, labelY - height + 6, width + 12, height + 2, 12, 12);

        g2.setColor(new Color(30, 120, 220, 230));
        g2.setFont(font);
        g2.drawString(text, labelX - width / 2, labelY);
    }

    private void drawArrow(Graphics2D g2, Point2D.Double from, Point2D.Double to) {
        final int ARR_SIZE = 10;

        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double angle = Math.atan2(dy, dx);

        double len = Math.sqrt(dx * dx + dy * dy);

        AffineTransform at = AffineTransform.getTranslateInstance(from.x, from.y);
        at.rotate(angle);
        g2.transform(at);

        g2.setStroke(new BasicStroke(3));
        g2.setPaint(new GradientPaint((float) 0, (float) 0, new Color(30, 120, 220), (float) (len - ARR_SIZE), 0, new Color(0, 60, 180)));
        g2.drawLine(0, 0, (int) len - ARR_SIZE, 0);

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint((int) len, 0);
        arrowHead.addPoint((int) len - ARR_SIZE, -ARR_SIZE / 2);
        arrowHead.addPoint((int) len - ARR_SIZE, ARR_SIZE / 2);
        g2.fill(arrowHead);

        try {
            g2.transform(at.createInverse());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("City Graph Visualizer");
        CityGraphVisualizer panel = new CityGraphVisualizer();

        JPanel controls = new JPanel();
        controls.setBackground(new Color(245, 245, 245));
        JButton zoomIn = new JButton("＋");
        zoomIn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        zoomIn.setFocusable(false);
        zoomIn.setBackground(new Color(60, 179, 113));
        zoomIn.setForeground(Color.WHITE);
        zoomIn.setPreferredSize(new Dimension(50, 40));

        JButton zoomOut = new JButton("－");
        zoomOut.setFont(new Font("Segoe UI", Font.BOLD, 20));
        zoomOut.setFocusable(false);
        zoomOut.setBackground(new Color(220, 20, 60));
        zoomOut.setForeground(Color.WHITE);
        zoomOut.setPreferredSize(new Dimension(50, 40));

        controls.add(zoomIn);
        controls.add(zoomOut);

        zoomIn.addActionListener(e -> {
            panel.scale *= 1.2;
            panel.repaint();
        });

        zoomOut.addActionListener(e -> {
            panel.scale /= 1.2;
            panel.repaint();
        });

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.SOUTH);

        frame.setSize(1000, 900);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CityGraphVisualizer::createAndShowGUI);
    }
}



