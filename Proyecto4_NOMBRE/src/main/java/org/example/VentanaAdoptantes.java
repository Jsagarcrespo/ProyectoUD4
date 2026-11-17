package org.example;

import com.mongodb.client.MongoDatabase;
import org.example.dao.AdoptanteDao;
import org.example.dao.AdopcionDao;
import org.example.modelos.Adoptante;
import org.example.modelos.Adopcion;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VentanaAdoptantes extends JFrame {

    private JPanel panelPrincipal;
    private JTextField dniField;
    private JTextField nombreField;
    private JTextField apellidosField;
    private JTextField telefonoField;
    private JTextField direccionField;
    private JTextField emailField;
    private JTextField fechaRegistroField;

    private JButton anadirButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JButton listarButton;
    private JButton buscarButton;
    private JButton anadirPreferenciaButton;
    private JButton eliminarPreferenciaButton;
    private JButton adopcionesButton;
    private JButton cerrarButton;

    private JComboBox<String> criterioComboBox;
    private JTextField buscarField;
    private JTable tablaAdoptantes;

    private AdoptanteDao adoptanteDao;
    private AdopcionDao adopcionDao;
    private DefaultTableModel modeloTabla;

    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    // CONSTRUCTOR
    public VentanaAdoptantes(MongoDatabase db) {

        adoptanteDao = new AdoptanteDao(db);
        adopcionDao = new AdopcionDao(db);

        setTitle("Gestión de adoptantes");
        setContentPane(panelPrincipal);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        formato.setLenient(false);

        // Criterios de búsqueda disponibles.
        criterioComboBox.addItem("DNI");
        criterioComboBox.addItem("Nombre");
        criterioComboBox.addItem("Apellidos");
        criterioComboBox.addItem("Preferencia");

        prepararTabla();

        listar();

        anadirButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                anadir();
            }
        });

        modificarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                modificar();
            }
        });

        eliminarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                eliminar();
            }
        });

        limpiarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                limpiar();
            }
        });

        listarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                listar();
            }
        });

        buscarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                buscar();
            }
        });

        anadirPreferenciaButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                anadirPreferencia();
            }
        });

        eliminarPreferenciaButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                eliminarPreferencia();
            }
        });

        adopcionesButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                consultarAdopciones();
            }
        });

        cerrarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
            }
        });

        // Cargamos los datos al seleccionar un adoptante.
        tablaAdoptantes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting()) {

                    seleccionarAdoptante();
                }
            }
        });
    }

    // PREPARAR TABLA
    private void prepararTabla() {

        String[] columnas = {"DNI", "Nombre", "Apellidos", "Teléfono", "Dirección", "Email", "Fecha registro", "Preferencias"};

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        tablaAdoptantes.setModel(modeloTabla);

        tablaAdoptantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // MOSTRAR ADOPTANTES EN LA TABLA
    private void mostrarAdoptantes(List<Adoptante> adoptantes) {

        modeloTabla.setRowCount(0);

        for (Adoptante adoptante : adoptantes) {

            String fecha = "";

            if (adoptante.getFechaRegistro() != null) {

                fecha = formato.format(adoptante.getFechaRegistro());
            }

            modeloTabla.addRow(new Object[]{adoptante.getDni(), adoptante.getNombre(), adoptante.getApellidos(), adoptante.getTelefono(), adoptante.getDireccion(), adoptante.getEmail(), fecha, adoptante.getPreferencias()});
        }
    }

    // LISTAR TODOS LOS ADOPTANTES
    private void listar() {

        try {

            mostrarAdoptantes(adoptanteDao.listar());

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // RECOGER LOS DATOS DE LA VENTANA
    private Adoptante recogerDatos() throws Exception {

        String dni = dniField.getText().trim().toUpperCase();
        String nombre = nombreField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String telefono = telefonoField.getText().trim();
        String direccion = direccionField.getText().trim();
        String email = emailField.getText().trim();
        String fecha = fechaRegistroField.getText().trim();

        // Comprobamos que no haya campos vacíos.
        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || email.isEmpty() || fecha.isEmpty()) {

            throw new IllegalArgumentException("Rellena todos los campos");
        }

        // Comprobamos el formato básico del DNI o NIE.
        if (!dni.matches("[0-9]{8}[A-Z]|[XYZ][0-9]{7}[A-Z]")) {

            throw new IllegalArgumentException("Formato de DNI o NIE incorrecto");
        }

        if (!email.contains("@")) {

            throw new IllegalArgumentException("El email no es válido");
        }

        if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {

            throw new IllegalArgumentException("Introduce la fecha en formato dd/MM/yyyy");
        }

        Date fechaRegistro = formato.parse(fecha);

        return new Adoptante(dni, nombre, apellidos, telefono, direccion, email, fechaRegistro);
    }

    // AÑADIR ADOPTANTE
    private void anadir() {

        try {

            Adoptante adoptante = recogerDatos();

            if (adoptanteDao.find(adoptante.getDni()) != null) {

                throw new IllegalArgumentException("Ya existe un adoptante con ese DNI");
            }

            // Inicializamos el array de preferencias vacío.
            adoptante.setPreferencias(new ArrayList<>());

            adoptanteDao.create(adoptante);

            JOptionPane.showMessageDialog(this, "Adoptante añadido correctamente");

            limpiar();

            listar();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // MODIFICAR ADOPTANTE
    private void modificar() {

        try {

            Adoptante adoptante = recogerDatos();

            if (adoptanteDao.find(adoptante.getDni()) == null) {

                throw new IllegalArgumentException("El adoptante no existe");
            }

            adoptanteDao.update(adoptante);

            JOptionPane.showMessageDialog(this, "Adoptante modificado correctamente");

            listar();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // ELIMINAR ADOPTANTE
    private void eliminar() {

        try {

            Adoptante adoptante = obtenerAdoptante();

            int respuesta = JOptionPane.showConfirmDialog(this, "¿Quieres eliminar este adoptante?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {

                adoptanteDao.delete(adoptante.getDni());

                JOptionPane.showMessageDialog(this, "Adoptante eliminado correctamente");

                limpiar();

                listar();
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // BUSCAR ADOPTANTES
    private void buscar() {

        try {

            String criterio = (String) criterioComboBox.getSelectedItem();

            String valor = buscarField.getText().trim();

            if (valor.isEmpty()) {

                throw new IllegalArgumentException("Introduce un valor para buscar");
            }

            List<Adoptante> adoptantes = new ArrayList<>();

            switch (criterio) {

                case "DNI":

                    Adoptante adoptante = adoptanteDao.find(valor.toUpperCase());

                    if (adoptante != null) {

                        adoptantes.add(adoptante);
                    }

                    break;

                case "Nombre":

                    adoptantes = adoptanteDao.buscarPorNombre(valor);

                    break;

                case "Apellidos":

                    adoptantes = adoptanteDao.buscarPorApellidos(valor);

                    break;

                case "Preferencia":

                    adoptantes = adoptanteDao.buscarPorPreferencia(valor);

                    break;
            }

            mostrarAdoptantes(adoptantes);

            if (adoptantes.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No se han encontrado adoptantes");
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // SELECCIONAR ADOPTANTE DE LA TABLA
    private void seleccionarAdoptante() {

        try {

            int fila = tablaAdoptantes.getSelectedRow();

            if (fila != -1) {

                String dni = (String) modeloTabla.getValueAt(fila, 0);

                Adoptante adoptante = adoptanteDao.find(dni);

                if (adoptante != null) {

                    dniField.setText(adoptante.getDni());
                    nombreField.setText(adoptante.getNombre());
                    apellidosField.setText(adoptante.getApellidos());
                    telefonoField.setText(adoptante.getTelefono());
                    direccionField.setText(adoptante.getDireccion());
                    emailField.setText(adoptante.getEmail());

                    if (adoptante.getFechaRegistro() != null) {

                        fechaRegistroField.setText(formato.format(adoptante.getFechaRegistro()));

                    } else {

                        fechaRegistroField.setText("");
                    }

                    // El DNI es el identificador del documento.
                    dniField.setEditable(false);
                }
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // OBTENER EL ADOPTANTE INDICADO
    private Adoptante obtenerAdoptante() {

        String dni = dniField.getText().trim().toUpperCase();

        Adoptante adoptante = adoptanteDao.find(dni);

        if (adoptante == null) {

            throw new IllegalArgumentException("Selecciona un adoptante existente");
        }

        return adoptante;
    }

    // AÑADIR PREFERENCIA
    private void anadirPreferencia() {

        try {

            Adoptante adoptante = obtenerAdoptante();

            String preferencia = JOptionPane.showInputDialog(this, "Introduce una preferencia");

            if (preferencia == null) {

                return;
            }

            preferencia = preferencia.trim();

            if (preferencia.isEmpty()) {

                throw new IllegalArgumentException("La preferencia está vacía");
            }

            if (adoptante.getPreferencias() != null && adoptante.getPreferencias().contains(preferencia)) {

                throw new IllegalArgumentException("La preferencia ya existe");
            }

            adoptanteDao.anadirPreferencia(adoptante.getDni(), preferencia);

            JOptionPane.showMessageDialog(this, "Preferencia añadida correctamente");

            listar();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // ELIMINAR PREFERENCIA
    private void eliminarPreferencia() {

        try {

            Adoptante adoptante = obtenerAdoptante();

            List<String> preferencias = adoptante.getPreferencias();

            if (preferencias == null || preferencias.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No hay preferencias registradas");

                return;
            }

            // Mostramos las preferencias disponibles para eliminar.
            String preferencia = (String) JOptionPane.showInputDialog(this, "Selecciona la preferencia", "Eliminar preferencia", JOptionPane.QUESTION_MESSAGE, null, preferencias.toArray(), preferencias.get(0));

            if (preferencia != null) {

                adoptanteDao.eliminarPreferencia(adoptante.getDni(), preferencia);

                JOptionPane.showMessageDialog(this, "Preferencia eliminada correctamente");

                listar();
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONSULTAR SOLICITUDES Y ADOPCIONES
    private void consultarAdopciones() {

        try {

            Adoptante adoptante = obtenerAdoptante();

            // Buscamos las adopciones por el DNI del adoptante.
            List<Adopcion> adopciones = adopcionDao.buscarPorAdoptante(adoptante.getDni());

            if (adopciones.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No tiene solicitudes ni adopciones");

                return;
            }

            StringBuilder texto = new StringBuilder();

            for (Adopcion adopcion : adopciones) {

                texto.append("ID adopción: ").append(adopcion.getId()).append("\n");

                texto.append("ID animal: ").append(adopcion.getIdAnimal()).append("\n");

                texto.append("Estado: ").append(adopcion.getEstado()).append("\n");

                if (adopcion.getFechaSolicitud() != null) {

                    texto.append("Fecha solicitud: ").append(formato.format(adopcion.getFechaSolicitud())).append("\n");
                }

                if (adopcion.getFechaResolucion() != null) {

                    texto.append("Fecha resolución: ").append(formato.format(adopcion.getFechaResolucion())).append("\n");
                }

                texto.append("Observaciones: ").append(adopcion.getObservaciones()).append("\n");

                texto.append("--------------------\n");
            }

            JTextArea area = new JTextArea(texto.toString(), 15, 45);

            area.setEditable(false);

            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Solicitudes y adopciones", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // LIMPIAR LOS CAMPOS
    private void limpiar() {

        tablaAdoptantes.clearSelection();

        dniField.setEditable(true);

        dniField.setText("");
        nombreField.setText("");
        apellidosField.setText("");
        telefonoField.setText("");
        direccionField.setText("");
        emailField.setText("");
        fechaRegistroField.setText("");
    }

    // MOSTRAR ERRORES
    private void mostrarError(Exception e) {

        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}