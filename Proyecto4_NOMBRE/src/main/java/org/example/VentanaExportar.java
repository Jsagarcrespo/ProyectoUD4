package org.example;

import com.mongodb.client.MongoDatabase;
import org.example.ExportarJson;
import org.example.ExportarXml;



import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class VentanaExportar extends JFrame {

    private JPanel panelPrincipal;
    private JComboBox<String> coleccionComboBox;
    private JButton xmlButton;
    private JButton jsonButton;
    private JButton cerrarButton;

    private MongoDatabase db;

    // CONSTRUCTOR
    public VentanaExportar(MongoDatabase db) {

        this.db = db;

        setTitle("Exportar datos");
        setContentPane(panelPrincipal);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        coleccionComboBox.addItem("animales");
        coleccionComboBox.addItem("adoptantes");
        coleccionComboBox.addItem("adopciones");

        xmlButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                exportar("xml");
            }
        });

        jsonButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                exportar("json");
            }
        });

        cerrarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
            }
        });
    }

    // EXPORTAR DATOS
    private void exportar(String formato) {

        String coleccion = (String) coleccionComboBox.getSelectedItem();

        String ruta = "ficheros/" + coleccion + "." + formato;

        File fichero = new File(ruta);

        // COMPROBAR SI EXISTE EL FICHERO
        if (fichero.exists()) {

            int opcion = JOptionPane.showConfirmDialog(this, "El fichero ya existe. ¿Deseas sobrescribirlo?", "Exportar", JOptionPane.YES_NO_OPTION);

            if (opcion != JOptionPane.YES_OPTION) {

                return;
            }
        }

        try {

            if (formato.equals("xml")) {

                switch (coleccion) {

                    case "animales":
                        ExportarXml.exportarAnimales(db);
                        break;

                    case "adoptantes":
                        ExportarXml.exportarAdoptantes(db);
                        break;

                    case "adopciones":
                        ExportarXml.exportarAdopciones(db);
                        break;
                }

            } else {

                switch (coleccion) {

                    case "animales":
                        ExportarJson.exportarAnimales(db);
                        break;

                    case "adoptantes":
                        ExportarJson.exportarAdoptantes(db);
                        break;

                    case "adopciones":
                        ExportarJson.exportarAdopciones(db);
                        break;
                }
            }

            JOptionPane.showMessageDialog(this, coleccion + "." + formato + " exportado correctamente");

        } catch (IOException e) {

            JOptionPane.showMessageDialog(this, "Error al escribir el fichero:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al exportar:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}