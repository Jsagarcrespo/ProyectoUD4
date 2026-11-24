package org.example;

import com.mongodb.client.MongoDatabase;
import org.example.dao.AdoptanteDao;
import org.example.dao.AdopcionDao;
import org.example.modelos.Adoptante;
import org.example.modelos.Adopcion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Calendar;

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
    private JTextArea lista;

    private AdoptanteDao adoptanteDao;
    private AdopcionDao adopcionDao;
    // Guarda los registros en el mismo orden que el listado.
    private List<Adoptante> registrosMostrados = new ArrayList<>();

    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    // CONSTRUCTOR
    public VentanaAdoptantes(MongoDatabase db) {

        adoptanteDao = new AdoptanteDao(db);
        adopcionDao = new AdopcionDao(db);

        setTitle("Gestión de adoptantes");
        setContentPane(panelPrincipal);
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        formato.setLenient(false);

        // Criterios de búsqueda disponibles.
        criterioComboBox.addItem("DNI");
        criterioComboBox.addItem("Nombre");
        criterioComboBox.addItem("Apellidos");
        criterioComboBox.addItem("Preferencia");

        lista.setEditable(false);
        lista.setLineWrap(false);

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
        // Al pulsar una línea del listado, cargamos el registro.
        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarAdoptante(e);
            }
        });
    }

    // MOSTRAR ADOPTANTES EN EL LISTADO
    private void mostrarAdoptantes(List<Adoptante> adoptantes) {

        registrosMostrados = new ArrayList<>(adoptantes);
        lista.setText("");

        for (Adoptante adoptante : adoptantes) {

            String fecha = "";

            if (adoptante.getFechaRegistro() != null) {

                fecha = formato.format(adoptante.getFechaRegistro());
            }

            String texto = "DNI: " + adoptante.getDni() + " | Nombre: " + adoptante.getNombre()
                    + " " + adoptante.getApellidos() + " | Teléfono: " + adoptante.getTelefono()
                    + " | Dirección: " + adoptante.getDireccion() + " | Email: " + adoptante.getEmail()
                    + " | Registro: " + fecha + " | Preferencias: " + adoptante.getPreferencias();

            // Un registro ocupa una sola línea, aunque tenga saltos de línea.
            lista.append(texto.replace('\r', ' ').replace('\n', ' ') + "\n");
        }
        lista.setCaretPosition(0);
    }

    // LISTAR TODOS LOS ADOPTANTES
    private void listar() {

        // Una nueva consulta invalida la seleccion anterior.
        // Limpiar no borra los campos del buscador.
        limpiar();

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

        // Una nueva consulta invalida la seleccion anterior.
        // Limpiar no borra los campos del buscador.
        limpiar();

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

    // SELECCIONAR ADOPTANTE DEL LISTADO
    private void seleccionarAdoptante(MouseEvent e) {

        try {

            // Si pulsamos debajo de los registros, no seleccionamos ninguno.
            int altoLinea = lista.getFontMetrics(lista.getFont()).getHeight();
            int y = e.getY() - lista.getInsets().top;

            if (y < 0 || y >= altoLinea * registrosMostrados.size()) {
                return;
            }

            int posicion = lista.viewToModel2D(e.getPoint());
            int fila = lista.getLineOfOffset(posicion);

            if (fila >= 0 && fila < registrosMostrados.size()) {

                String dni = registrosMostrados.get(fila).getDni();

                Adoptante adoptante = adoptanteDao.find(dni);

                if (adoptante != null) {

                    lista.select(lista.getLineStartOffset(fila),
                            lista.getLineEndOffset(fila));

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

        } catch (Exception ex) {

            mostrarError(ex);
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

            String inicio = JOptionPane.showInputDialog(this, "Introduce fecha inicial (dd/MM/yyyy)");

            if (inicio == null) {
                return;
            }

            String fin = JOptionPane.showInputDialog(this, "Introduce fecha final (dd/MM/yyyy)");

            if (fin == null) {
                return;
            }

            if (!inicio.matches("\\d{2}/\\d{2}/\\d{4}") || !fin.matches("\\d{2}/\\d{2}/\\d{4}")) {

                throw new IllegalArgumentException("Formato de fecha incorrecto");
            }

            Date desde = formato.parse(inicio);
            Date hasta = formato.parse(fin);

            if (desde.after(hasta)) {

                throw new IllegalArgumentException("Intervalo incorrecto");
            }

// INCLUIR TODO EL ULTIMO DIA
            Calendar calendario = Calendar.getInstance();

            calendario.setTime(hasta);
            calendario.add(Calendar.DATE, 1);
            calendario.add(Calendar.MILLISECOND, -1);

            hasta = calendario.getTime();

            List<Adopcion> adopciones = adopcionDao.buscarPorAdoptanteYFecha(adoptante.getDni(), desde, hasta);

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

        lista.select(0, 0);

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
        panelPrincipal.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(16, 4, new Insets(12, 12, 12, 12), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("GESTIÓN DE ADOPTANTES");
        panelPrincipal.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("DNI/NIE");
        panelPrincipal.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Nombre");
        panelPrincipal.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dniField = new JTextField();
        panelPrincipal.add(dniField, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nombreField = new JTextField();
        panelPrincipal.add(nombreField, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Apellidos");
        panelPrincipal.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Teléfono");
        panelPrincipal.add(label5, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        apellidosField = new JTextField();
        panelPrincipal.add(apellidosField, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        telefonoField = new JTextField();
        panelPrincipal.add(telefonoField, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Dirección");
        panelPrincipal.add(label6, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Email");
        panelPrincipal.add(label7, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        direccionField = new JTextField();
        panelPrincipal.add(direccionField, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        emailField = new JTextField();
        panelPrincipal.add(emailField, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Fecha registro (dd/MM/yyyy)");
        panelPrincipal.add(label8, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fechaRegistroField = new JTextField();
        panelPrincipal.add(fechaRegistroField, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        anadirButton = new JButton();
        anadirButton.setText("Añadir");
        panelPrincipal.add(anadirButton, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modificarButton = new JButton();
        modificarButton.setText("Modificar");
        panelPrincipal.add(modificarButton, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eliminarButton = new JButton();
        eliminarButton.setText("Eliminar");
        panelPrincipal.add(eliminarButton, new com.intellij.uiDesigner.core.GridConstraints(9, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        limpiarButton = new JButton();
        limpiarButton.setText("Limpiar");
        panelPrincipal.add(limpiarButton, new com.intellij.uiDesigner.core.GridConstraints(9, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Buscar");
        panelPrincipal.add(label9, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        criterioComboBox = new JComboBox();
        panelPrincipal.add(criterioComboBox, new com.intellij.uiDesigner.core.GridConstraints(11, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buscarField = new JTextField();
        panelPrincipal.add(buscarField, new com.intellij.uiDesigner.core.GridConstraints(11, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buscarButton = new JButton();
        buscarButton.setText("Buscar");
        panelPrincipal.add(buscarButton, new com.intellij.uiDesigner.core.GridConstraints(11, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        anadirPreferenciaButton = new JButton();
        anadirPreferenciaButton.setText("Añadir preferencia");
        panelPrincipal.add(anadirPreferenciaButton, new com.intellij.uiDesigner.core.GridConstraints(12, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eliminarPreferenciaButton = new JButton();
        eliminarPreferenciaButton.setText("Eliminar preferencia");
        panelPrincipal.add(eliminarPreferenciaButton, new com.intellij.uiDesigner.core.GridConstraints(12, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        adopcionesButton = new JButton();
        adopcionesButton.setText("Ver adopciones");
        panelPrincipal.add(adopcionesButton, new com.intellij.uiDesigner.core.GridConstraints(12, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Listado de adoptantes (pulsa una línea para seleccionar)");
        panelPrincipal.add(label10, new com.intellij.uiDesigner.core.GridConstraints(13, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelPrincipal.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(14, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lista = new JTextArea();
        lista.setText("");
        scrollPane1.setViewportView(lista);
        listarButton = new JButton();
        listarButton.setText("Mostrar todos");
        panelPrincipal.add(listarButton, new com.intellij.uiDesigner.core.GridConstraints(15, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cerrarButton = new JButton();
        cerrarButton.setText("Cerrar");
        panelPrincipal.add(cerrarButton, new com.intellij.uiDesigner.core.GridConstraints(15, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}