package org.example;

import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import org.example.dao.RefugioDao;
import org.example.modelos.Refugio;

import javax.swing.*;
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

        conexion = new DatabaseConnection();
        db = conexion.getDatabase();

        GestionIndices.crearIndices(db);

        cargarDatosRefugio();

        setTitle("Gestion de refugio de animales");
        setContentPane(panelPrincipal);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        datosRefugio.setEditable(false);

        // CONEXION A MONGODB
        conexion = new DatabaseConnection();
        db = conexion.getDatabase();

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

    // METODO TEMPORAL
    private void mostrarPendiente(String apartado) {

        JOptionPane.showMessageDialog(this, apartado + ": ventana pendiente de implementar");
    }
}
