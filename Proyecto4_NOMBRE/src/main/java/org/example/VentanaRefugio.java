package org.example;

import com.mongodb.client.MongoDatabase;
import org.example.dao.RefugioDao;
import org.example.modelos.Refugio;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VentanaRefugio extends JFrame {

    private JPanel panelPrincipal;
    private JTextField nombreField;
    private JTextField razonSocialField;
    private JTextField cifField;
    private JTextField direccionField;
    private JTextField telefonoField;
    private JTextField emailField;
    private JTextField fechaAperturaField;
    private JButton guardarButton;
    private JButton cerrarButton;

    private RefugioDao refugioDao;

    // CONSTRUCTOR
    public VentanaRefugio(MongoDatabase db) {

        refugioDao = new RefugioDao(db);

        setTitle("Gestión del refugio");
        setContentPane(panelPrincipal);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cargarDatos();

        guardarButton.addActionListener(e -> guardar());

        cerrarButton.addActionListener(e -> dispose());
    }

    // CARGAR LOS DATOS DEL REFUGIO
    private void cargarDatos() {

        try {

            Refugio refugio = refugioDao.find();

            // Si existe un refugio, mostramos sus datos.
            if (refugio != null) {

                nombreField.setText(refugio.getNombre());
                razonSocialField.setText(refugio.getRazonSocial());
                cifField.setText(refugio.getCif());
                direccionField.setText(refugio.getDireccion());
                telefonoField.setText(refugio.getTelefono());
                emailField.setText(refugio.getEmail());

                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

                if (refugio.getFechaApertura() != null) {

                    fechaAperturaField.setText(formato.format(refugio.getFechaApertura()));
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // GUARDAR O MODIFICAR LOS DATOS DEL REFUGIO
    private void guardar() {

        try {

            // Recogemos los datos introducidos en la ventana.
            String nombre = nombreField.getText().trim();
            String razonSocial = razonSocialField.getText().trim();
            String cif = cifField.getText().trim();
            String direccion = direccionField.getText().trim();
            String telefono = telefonoField.getText().trim();
            String email = emailField.getText().trim();
            String fecha = fechaAperturaField.getText().trim();

            // Comprobamos que no haya campos vacíos.
            if (nombre.isEmpty() || razonSocial.isEmpty() || cif.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty() || fecha.isEmpty()) {

                JOptionPane.showMessageDialog(this, "Rellena todos los campos");

                return;
            }

            // Comprobamos el correo electrónico.
            if (!email.contains("@")) {

                JOptionPane.showMessageDialog(this, "El email no es válido");

                return;
            }

            // Comprobamos el formato de la fecha.
            if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {

                JOptionPane.showMessageDialog(this, "Introduce la fecha en formato dd/MM/yyyy");

                return;
            }

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

            formato.setLenient(false);

            Date fechaApertura = formato.parse(fecha);

            // Creamos el objeto con los datos introducidos.
            Refugio refugio = new Refugio(nombre, razonSocial, cif, direccion, telefono, email, fechaApertura);

            // Si no existe el refugio, lo creamos.
            if (refugioDao.find() == null) {

                refugioDao.create(refugio);

            } else {

                // Si ya existe, modificamos sus datos.
                refugioDao.update(refugio);
            }

            JOptionPane.showMessageDialog(this, "Datos guardados correctamente");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}