package Interfaz;

import javax.swing.*;
import java.awt.*;

import Controller.Controller;
import DataStructure.LinkedList.LinkedList;
import Exceptions.ItemDuplicated;
import Exceptions.ItemNotFound;
import Controller.Zona;

public class Interfaz extends JFrame {
    private Controller controller;
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
    
    public Interfaz() {
        this.controller = new Controller();
        vertices = new LinkedList<>();
        aristas = new LinkedList<>();
        botonesVer = new LinkedList<>();

        setTitle("Editor de Grafo");
        setSize(1200, 770);
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

        JPanel botones = new JPanel();
        JButton btnAgregarVertice = new JButton("Agregar vértice");
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
        JButton btnEliminarVertice = new JButton("Eliminar vértice");
        estiloBoton(btnEliminarVertice, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnEliminarVertice.addActionListener(e -> {
            try {
                eliminarVertice();
            } catch (ItemNotFound ex) {
                throw new RuntimeException(ex);
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
        JButton btnBuscarVertice = new JButton("Buscar vértice");
        estiloBoton(btnBuscarVertice, new Color(59, 89, 182), Color.WHITE, 150, 40);
        btnBuscarVertice.addActionListener(e -> buscarVertice());

        botones.add(btnAgregarVertice);
        botones.add(btnAgregarArista);
        botones.add(btnEliminarVertice);
        botones.add(btnEliminarArista);
        botones.add(btnBuscarVertice);
        botones.add(btnModificarPeso);
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
                icono = new ImageIcon(getClass().getResource("/Interfaz/img/edificios.png"));
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

    private void abrirVentanaVertice(String nombre) throws ItemNotFound {
        Zona zona = controller.buscarZona(nombre);
        if (zona == null) {
            JOptionPane.showMessageDialog(this, "Zona no encontrada.");
            return;
        }

        InterfazArbol ventana = new InterfazArbol(zona);
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
        String nombre = JOptionPane.showInputDialog(this, "Ingrese vértice:");
        if (nombre == null || nombre.isEmpty()) return;
        if (!vertices.search(nombre)) {
            JOptionPane.showMessageDialog(this, "Vértice no existe.");
            return;
        }
        controller.removeZone(nombre);
        int idx = vertices.findIndex(nombre);
        vertices.removeNode(nombre);
        botonesVer.removeAtIndex(idx);
        for (int i = 0; i < aristas.length(); ) {
            AristaGrafica a = aristas.getNodeAtIndex(i);
            if (a.origen.equals(nombre) || a.destino.equals(nombre)) aristas.removeAtIndex(i);
            else i++;
        }
        panel.removeAll();
        for (int i = 0; i < botonesVer.length(); i++) panel.add(botonesVer.getNodeAtIndex(i));
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

        if (controller.updateEdgeWeight(origen, destino, nuevoPeso)) {
            // actualiza en la lista gráfica
            for (int i = 0; i < aristas.length(); i++) {
                AristaGrafica a = aristas.getNodeAtIndex(i);
                if (a.origen.equals(origen) && a.destino.equals(destino)) {
                    a.peso = nuevoPeso;
                    break;
                }
            }
            panel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No se encontró la arista.");
        }
    }

    private void buscarVertice() {
        String nombre = JOptionPane.showInputDialog(this, "Buscar vértice:");
        if (nombre == null || nombre.isEmpty()) return;
        JOptionPane.showMessageDialog(this, vertices.search(nombre)?
                "Existe: " + nombre : "No existe: " + nombre);
    }

    class BotonVertice extends JComponent {
        private final String nombre;
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
                        abrirVentanaVertice(nombre);
                    } catch (ItemNotFound e) {
                        throw new RuntimeException(e);
                    }
                }
            });
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
                fondo = new ImageIcon(getClass().getResource("/Interfaz/img/fondo.jpeg")).getImage();
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
                int px=(int)(p1.x+ux*70+nx*10);
                int py=(int)(p1.y+uy*70+ny*10);
                String s=String.valueOf(a.peso);
                FontMetrics fm2=g2.getFontMetrics();
                int w=fm2.stringWidth(s),h=fm2.getHeight();

                // Modificacion del subrayado del peso
                g2.setColor(Color.YELLOW);
                int extra = 20;
                g2.fillRect(px - w/2 - extra/2, py - h/2 - 2, w + extra, h+6);

                // Tamaño del tipo de fuente del peso
                g2.setColor(Color.RED);
                g2.setFont(new Font("Arial",Font.BOLD,Math.max(14,radio/6)));
                g2.drawString(s,px-w/2,py+fm2.getAscent()/2);
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
