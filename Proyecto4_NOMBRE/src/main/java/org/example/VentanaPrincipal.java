package org.example;

import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import org.example.dao.RefugioDao;
import org.example.modelos.Refugio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {

    private JPanel panelPrincipal;
    private JTextArea datosRefugio;

    private JButton animalesButton;
    private JButton adoptantesButton;
    private JButton adopcionesButton;
    private JButton refugioButton;
    private JButton exportarButton;
    private JButton metadatosButton;
    private JButton estadisticasButton;
    private JButton salirButton;

    private DatabaseConnection conexion;
    private MongoDatabase db;


    // CONSTRUCTOR
    public VentanaPrincipal() {


        setTitle("Gestion de refugio de animales");
        setContentPane(panelPrincipal);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        datosRefugio.setEditable(false);

        // CONEXION A MONGODB
        conexion = new DatabaseConnection();
        db = conexion.getDatabase();

        try {
            GestionIndices.crearIndices(db);
        } catch (MongoException e) {
            conexion.close();
            throw e;
        }

        cargarDatosRefugio();

        // ANIMALES
        animalesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                VentanaAnimales ventana = new VentanaAnimales(db);

                ventana.setVisible(true);
            }
        });

        // ADOPTANTES
        adoptantesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                VentanaAdoptantes ventana = new VentanaAdoptantes(db);

                ventana.setVisible(true);
            }
        });

        // ADOPCIONES
        adopcionesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                VentanaAdopciones ventana = new VentanaAdopciones(db);

                ventana.setVisible(true);
            }
        });

        // REFUGIO
        refugioButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                VentanaRefugio ventana = new VentanaRefugio(db);

                ventana.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(WindowEvent evento) {

                        cargarDatosRefugio();
                    }
                });

                ventana.setVisible(true);
            }
        });

        // EXPORTAR
        exportarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                VentanaExportar ventana = new VentanaExportar(db);

                ventana.setVisible(true);
            }
        });

        // METADATOS
        metadatosButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    String datos = GestionMetadatos.obtenerMetadatos(db);

                    JTextArea texto = new JTextArea(datos, 20, 55);

                    texto.setEditable(false);

                    JScrollPane scroll = new JScrollPane(texto);

                    JOptionPane.showMessageDialog(VentanaPrincipal.this, scroll, "Metadatos de MongoDB", JOptionPane.INFORMATION_MESSAGE);

                    GestionMetadatos.exportar(datos);

                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "Metadatos guardados en ficheros/Metadatos.txt");

                } catch (MongoException | IOException ex) {

                    JOptionPane.showMessageDialog(VentanaPrincipal.this, "Error al obtener metadatos: " + ex.getMessage());
                }
            }
        });

        // ESTADISTICAS
        estadisticasButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                Estadisticas.mostrarEstadisticas(db);
            }
        });

        // SALIR
        salirButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dispatchEvent(new WindowEvent(VentanaPrincipal.this, WindowEvent.WINDOW_CLOSING));
            }
        });

        // CERRAR CONEXION
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                conexion.close();
            }
        });
    }

    // MOSTRAR LOS DATOS DEL REFUGIO
    private void cargarDatosRefugio() {

        try {

            RefugioDao dao = new RefugioDao(db);

            Refugio refugio = dao.find();

            if (refugio != null) {

                datosRefugio.setText("Nombre: " + refugio.getNombre() + "\n" + "Razon social: " + refugio.getRazonSocial() + "\n" + "CIF: " + refugio.getCif() + "\n" + "Direccion: " + refugio.getDireccion() + "\n" + "Telefono: " + refugio.getTelefono() + "\n" + "Email: " + refugio.getEmail() + "\n" + "Fecha apertura: " + refugio.getFechaApertura());

            } else {

                datosRefugio.setText("No hay datos del refugio registrados");
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, new Insets(12, 12, 12, 12), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("GESTIÓN DE REFUGIO DE ANIMALES");
        panelPrincipal.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Datos del refugio");
        panelPrincipal.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelPrincipal.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        datosRefugio = new JTextArea();
        datosRefugio.setEditable(false);
        datosRefugio.setLineWrap(true);
        datosRefugio.setWrapStyleWord(true);
        scrollPane1.setViewportView(datosRefugio);
        refugioButton = new JButton();
        refugioButton.setText("Refugio");
        panelPrincipal.add(refugioButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        animalesButton = new JButton();
        animalesButton.setText("Animales");
        panelPrincipal.add(animalesButton, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        adoptantesButton = new JButton();
        adoptantesButton.setText("Adoptantes");
        panelPrincipal.add(adoptantesButton, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        adopcionesButton = new JButton();
        adopcionesButton.setText("Adopciones");
        panelPrincipal.add(adopcionesButton, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportarButton = new JButton();
        exportarButton.setText("Exportar datos");
        panelPrincipal.add(exportarButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        metadatosButton = new JButton();
        metadatosButton.setText("Metadatos");
        panelPrincipal.add(metadatosButton, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        estadisticasButton = new JButton();
        estadisticasButton.setText("Estadísticas");
        panelPrincipal.add(estadisticasButton, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        salirButton = new JButton();
        salirButton.setText("Salir");
        panelPrincipal.add(salirButton, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}
