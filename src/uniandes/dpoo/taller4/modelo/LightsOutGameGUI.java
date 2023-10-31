package uniandes.dpoo.taller4.modelo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class LightsOutGameGUI extends JFrame {
    private Tablero tablero;
    private int tamanoCelda = 50; // Tamaño de celda en píxeles
    private int tamano;
    private JLabel movimientosLabel;
    private JButton top10Button;
    private JButton reiniciarButton;
    private JButton nuevoButton;

    public LightsOutGameGUI(int tamano) {
        this.tamano = tamano;
        tablero = new Tablero(tamano);

        // Configurar la ventana principal
        setTitle("Lights Out Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el panel del tablero
        JPanel tableroPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                boolean[][] estadoTablero = tablero.darTablero();
                for (int fila = 0; fila < tamano; fila++) {
                    for (int columna = 0; columna < tamano; columna++) {
                        boolean estado = estadoTablero[fila][columna];
                        g.setColor(estado ? Color.YELLOW : Color.BLACK);
                        g.fillRect(columna * tamanoCelda, fila * tamanoCelda, tamanoCelda, tamanoCelda);

                        // Dibujar el contorno negro
                        g.setColor(Color.BLACK);
                        g.drawRect(columna * tamanoCelda, fila * tamanoCelda, tamanoCelda, tamanoCelda);
                    }
                }
            }
        };

        tableroPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = e.getY() / tamanoCelda;
                int columna = e.getX() / tamanoCelda;
                if (fila >= 0 && fila < tamano && columna >= 0 && columna < tamano) {
                    tablero.jugar(fila, columna);
                    if (tablero.tableroIluminado()) {
                        int puntaje = tablero.calcularPuntaje();
                        JOptionPane.showMessageDialog(null, "¡Felicidades! Tablero resuelto.\nPuntaje: " + puntaje, "Juego Terminado", JOptionPane.INFORMATION_MESSAGE);
                        tablero.reiniciar();
                    }
                    tableroPanel.repaint();
                    actualizarMovimientos();
                }
            }
        });

        // Crear el panel de información (movimientos y botón TOP10)
        JPanel infoPanel = new JPanel();
        movimientosLabel = new JLabel("Movimientos: 0");
        top10Button = new JButton("TOP10");
        reiniciarButton = new JButton("Reiniciar");
        nuevoButton = new JButton("Nuevo");

        // Agregar ActionListener al botón "TOP10"
        top10Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarTop10(); // Mostrar la tabla Top 10
            }
        });

        // Agregar ActionListener al botón "Reiniciar"
        reiniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tablero.reiniciar();
                tableroPanel.repaint();
                actualizarMovimientos();
            }
        });

        // Agregar ActionListener al botón "Nuevo"
        nuevoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear un nuevo juego (iniciar uno completamente nuevo)
                tablero = new Tablero(tamano);
                tableroPanel.repaint();
                actualizarMovimientos();
            }
        });

        // Agregar componentes al infoPanel
        infoPanel.add(movimientosLabel);
        infoPanel.add(top10Button);
        infoPanel.add(reiniciarButton);
        infoPanel.add(nuevoButton);

        // Agregar los paneles y botones a la ventana principal utilizando BorderLayout
        getContentPane().add(tableroPanel, BorderLayout.CENTER);
        getContentPane().add(infoPanel, BorderLayout.NORTH);

        // Mostrar la ventana
        pack(); // Ajustar el tamaño de la ventana automáticamente
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla
        setVisible(true);
    }

    public void actualizarMovimientos() {
        int movimientos = tablero.darJugadas();
        movimientosLabel.setText("Movimientos: " + movimientos);
    }

    public void mostrarTop10() {
        // Crear una nueva ventana para mostrar la tabla CSV de registros Top 10
        JFrame top10Frame = new JFrame("Top 10");
        top10Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear un JTextArea para mostrar la tabla CSV
        JTextArea top10TextArea = new JTextArea(20, 50);
        top10TextArea.setEditable(false); // No se permite la edición del texto

        // Cargar los registros desde el archivo CSV
        Top10 top10 = new Top10();
        File archivoCSV = new File("top10.csv"); // Ruta al archivo CSV
        top10.cargarRecords(archivoCSV);

        // Obtener los registros y mostrarlos en el JTextArea
        for (RegistroTop10 registro : top10.darRegistros()) {
            top10TextArea.append(registro.toString() + "\n");
        }

        // Agregar el JTextArea a un JScrollPane para habilitar el desplazamiento
        JScrollPane scrollPane = new JScrollPane(top10TextArea);

        // Agregar el JScrollPane al JFrame
        top10Frame.add(scrollPane);

        // Ajustar el tamaño de la ventana automáticamente
        top10Frame.pack();

        // Centrar la ventana en la pantalla
        top10Frame.setLocationRelativeTo(null);

        // Mostrar la ventana del Top 10
        top10Frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LightsOutGameGUI(5)); // Tamaño del tablero
    }
}
