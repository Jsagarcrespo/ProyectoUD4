package org.example;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.dao.AdopcionDao;
import org.example.dao.AdoptanteDao;
import org.example.dao.AnimalDao;
import org.example.modelos.Adopcion;
import org.example.modelos.Adoptante;
import org.example.modelos.Animal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VentanaAdopciones extends JFrame {

    private JPanel panelPrincipal;
    private JTextField idField;
    private JTextField idAnimalField;
    private JTextField dniAdoptanteField;
    private JTextField fechaSolicitudField;
    private JTextField fechaResolucionField;
    private JTextArea observacionesField;
    private JComboBox<String> estadoComboBox;

    private JButton anadirButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JButton buscarButton;
    private JButton listarButton;
    private JButton asociadosButton;
    private JButton seguimientoButton;
    private JButton registroButton;
    private JButton cerrarButton;

    private JComboBox<String> criterioComboBox;
    private JTextField buscarField;
    private JTextField hastaField;

    private JTextArea lista;
    // Guarda los registros en el mismo orden que el listado.
    private List<Adopcion> registrosMostrados = new ArrayList<>();

    private AdopcionDao adopcionDao;
    private AnimalDao animalDao;
    private AdoptanteDao adoptanteDao;

    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    // CONSTRUCTOR
    public VentanaAdopciones(MongoDatabase db) {

        adopcionDao = new AdopcionDao(db);
        animalDao = new AnimalDao(db);
        adoptanteDao = new AdoptanteDao(db);

        setTitle("Gestión de adopciones");
        setContentPane(panelPrincipal);
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        formato.setLenient(false);

        // Estados de una adopción.
        estadoComboBox.addItem("pendiente");
        estadoComboBox.addItem("aprobada");
        estadoComboBox.addItem("rechazada");

        // Criterios de búsqueda.
        criterioComboBox.addItem("ID");
        criterioComboBox.addItem("Animal");
        criterioComboBox.addItem("Adoptante");
        criterioComboBox.addItem("Estado");
        criterioComboBox.addItem("Fecha");

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

        asociadosButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                consultarAsociados();
            }
        });

        seguimientoButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                verSeguimiento();
            }
        });

        registroButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                anadirSeguimiento();
            }
        });

        cerrarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
            }
        });

        // Al pulsar una línea del listado, cargamos el registro.
        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarAdopcion(e);
            }
        });
    }

    // MOSTRAR ADOPCIONES EN EL LISTADO
    private void mostrarAdopciones(List<Adopcion> adopciones) {

        registrosMostrados = new ArrayList<>(adopciones);
        lista.setText("");

        for (Adopcion adopcion : adopciones) {

            String fechaSolicitud = "";
            String fechaResolucion = "";

            if (adopcion.getFechaSolicitud() != null) {

                fechaSolicitud = formato.format(adopcion.getFechaSolicitud());
            }

            if (adopcion.getFechaResolucion() != null) {

                fechaResolucion = formato.format(adopcion.getFechaResolucion());
            }

            String texto = "ID: " + adopcion.getId() + " | Animal: " + adopcion.getIdAnimal()
                    + " | Adoptante: " + adopcion.getDniAdoptante()
                    + " | Solicitud: " + fechaSolicitud + " | Resolución: " + fechaResolucion
                    + " | Estado: " + adopcion.getEstado() + " | Observaciones: " + adopcion.getObservaciones();

            // Un registro ocupa una sola línea, aunque tenga saltos de línea.
            lista.append(texto.replace('\r', ' ').replace('\n', ' ') + "\n");
        }
        lista.setCaretPosition(0);
    }

    // LISTAR TODAS LAS ADOPCIONES
    private void listar() {

        // Una nueva consulta invalida la seleccion anterior.
        // Limpiar no borra los campos del buscador.
        limpiar();

        try {

            mostrarAdopciones(adopcionDao.listar());

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONVERTIR UN TEXTO EN UNA FECHA
    private Date leerFecha(String texto) throws Exception {

        if (!texto.matches("\\d{2}/\\d{2}/\\d{4}")) {

            throw new IllegalArgumentException("Introduce la fecha en formato dd/MM/yyyy");
        }

        return formato.parse(texto);
    }

    // RECOGER LOS DATOS DE LA VENTANA
    private Adopcion recogerDatos() throws Exception {

        if (idField.getText().trim().isEmpty() || idAnimalField.getText().trim().isEmpty() || dniAdoptanteField.getText().trim().isEmpty() || fechaSolicitudField.getText().trim().isEmpty()) {

            throw new IllegalArgumentException("Rellena todos los campos obligatorios");
        }

        int id = Integer.parseInt(idField.getText().trim());

        int idAnimal = Integer.parseInt(idAnimalField.getText().trim());

        if (id <= 0 || idAnimal <= 0) {

            throw new IllegalArgumentException("Los identificadores deben ser positivos");
        }

        String dni = dniAdoptanteField.getText().trim().toUpperCase();

        String estado = (String) estadoComboBox.getSelectedItem();

        String observaciones = observacionesField.getText().trim();

        Date fechaSolicitud = leerFecha(fechaSolicitudField.getText().trim());

        Date fechaResolucion = null;

        // La fecha de resolución puede estar vacía
        // mientras la solicitud esté pendiente.
        if (!fechaResolucionField.getText().trim().isEmpty()) {

            fechaResolucion = leerFecha(fechaResolucionField.getText().trim());
        }

        // La resolución no puede ser anterior a la solicitud.
        if (fechaResolucion != null && fechaResolucion.before(fechaSolicitud)) {

            throw new IllegalArgumentException("La resolución no puede ser anterior a la solicitud");
        }

        if (estado.equals("pendiente") && fechaResolucion != null) {

            throw new IllegalArgumentException("Una solicitud pendiente no debe tener fecha de resolución");
        }

        if (!estado.equals("pendiente") && fechaResolucion == null) {

            throw new IllegalArgumentException("Introduce la fecha de resolución");
        }

        return new Adopcion(id, idAnimal, dni, fechaSolicitud, fechaResolucion, estado, observaciones);
    }

    // AÑADIR ADOPCIÓN
    private void anadir() {

        try {

            Adopcion adopcion = recogerDatos();

            adopcionDao.create(adopcion);

            JOptionPane.showMessageDialog(this, "Adopción añadida correctamente");

            limpiar();
            listar();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // MODIFICAR ADOPCIÓN
    private void modificar() {

        try {

            Adopcion adopcion = recogerDatos();

            if (adopcionDao.find(adopcion.getId()) == null) {

                throw new IllegalArgumentException("La adopción no existe");
            }

            adopcionDao.update(adopcion);

            JOptionPane.showMessageDialog(this, "Adopción modificada correctamente");

            listar();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // ELIMINAR ADOPCIÓN
    private void eliminar() {

        try {

            Adopcion adopcion = obtenerAdopcion();

            int respuesta = JOptionPane.showConfirmDialog(this, "¿Quieres eliminar esta adopción?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {

                adopcionDao.delete(adopcion.getId());

                JOptionPane.showMessageDialog(this, "Adopción eliminada correctamente");

                limpiar();

                listar();
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // BUSCAR ADOPCIONES
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

            List<Adopcion> adopciones = new ArrayList<>();

            switch (criterio) {

                case "ID":

                    Adopcion adopcion = adopcionDao.find(Integer.parseInt(valor));

                    if (adopcion != null) {

                        adopciones.add(adopcion);
                    }

                    break;

                case "Animal":

                    adopciones = adopcionDao.buscarPorAnimal(Integer.parseInt(valor));

                    break;

                case "Adoptante":

                    adopciones = adopcionDao.buscarPorAdoptante(valor.toUpperCase());

                    break;

                case "Estado":

                    adopciones = adopcionDao.buscarPorEstado(valor.toLowerCase());

                    break;

                case "Fecha":

                    Date desde = leerFecha(valor);

                    Date hasta = leerFecha(hastaField.getText().trim());

                    if (hasta.before(desde)) {

                        throw new IllegalArgumentException("El intervalo de fechas no es válido");
                    }

                    // Incluimos todo el último día.
                    Calendar calendario = Calendar.getInstance();

                    calendario.setTime(hasta);
                    calendario.add(Calendar.DATE, 1);
                    calendario.add(Calendar.MILLISECOND, -1);

                    adopciones = adopcionDao.buscarPorFecha(desde, calendario.getTime());

                    break;
            }

            mostrarAdopciones(adopciones);

            if (adopciones.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No se han encontrado adopciones");
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // SELECCIONAR ADOPCIÓN DEL LISTADO
    private void seleccionarAdopcion(MouseEvent e) {

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

                int id = registrosMostrados.get(fila).getId();

                Adopcion adopcion = adopcionDao.find(id);

                if (adopcion != null) {

                    lista.select(lista.getLineStartOffset(fila),
                            lista.getLineEndOffset(fila));

                    idField.setText(String.valueOf(adopcion.getId()));

                    idAnimalField.setText(String.valueOf(adopcion.getIdAnimal()));

                    dniAdoptanteField.setText(adopcion.getDniAdoptante());

                    estadoComboBox.setSelectedItem(adopcion.getEstado());

                    observacionesField.setText(adopcion.getObservaciones());

                    if (adopcion.getFechaSolicitud() != null) {

                        fechaSolicitudField.setText(formato.format(adopcion.getFechaSolicitud()));

                    } else {

                        fechaSolicitudField.setText("");
                    }

                    if (adopcion.getFechaResolucion() != null) {

                        fechaResolucionField.setText(formato.format(adopcion.getFechaResolucion()));

                    } else {

                        fechaResolucionField.setText("");
                    }

                    idField.setEditable(false);
                }
            }

        } catch (Exception ex) {

            mostrarError(ex);
        }
    }

    // OBTENER UNA ADOPCIÓN EXISTENTE
    private Adopcion obtenerAdopcion() {

        int id = Integer.parseInt(idField.getText().trim());

        Adopcion adopcion = adopcionDao.find(id);

        if (adopcion == null) {

            throw new IllegalArgumentException("La adopción no existe");
        }

        return adopcion;
    }

    // CONSULTAR EL ANIMAL Y ADOPTANTE ASOCIADOS
    private void consultarAsociados() {

        try {

            Adopcion adopcion = obtenerAdopcion();

            Animal animal = animalDao.find(adopcion.getIdAnimal());

            Adoptante adoptante = adoptanteDao.find(adopcion.getDniAdoptante());

            if (animal == null || adoptante == null) {

                throw new IllegalArgumentException("No se encuentra el animal o el adoptante asociado");
            }

            String texto = "DATOS DEL ANIMAL\n\n" + "ID: " + animal.getId() + "\n" + "Nombre: " + animal.getNombre() + "\n" + "Especie: " + animal.getEspecie() + "\n" + "Raza: " + animal.getRaza() + "\n" + "Estado: " + animal.getEstado() + "\n\n"

                    + "DATOS DEL ADOPTANTE\n\n" + "DNI: " + adoptante.getDni() + "\n" + "Nombre: " + adoptante.getNombre() + "\n" + "Apellidos: " + adoptante.getApellidos() + "\n" + "Teléfono: " + adoptante.getTelefono() + "\n" + "Email: " + adoptante.getEmail();

            JOptionPane.showMessageDialog(this, texto, "Datos asociados", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // AÑADIR UN REGISTRO DE SEGUIMIENTO
    private void anadirSeguimiento() {

        try {

            Adopcion adopcion = obtenerAdopcion();

            if (!adopcion.getEstado().equals("aprobada")) {

                throw new IllegalArgumentException("La adopción debe estar aprobada");
            }

            String descripcion = JOptionPane.showInputDialog(this, "Introduce el seguimiento");

            if (descripcion == null) {

                return;
            }

            if (descripcion.trim().isEmpty()) {

                throw new IllegalArgumentException("Introduce una descripción");
            }

            // Documento embebido en el array seguimiento.
            Document registro = new Document("fecha", new Date()).append("descripcion", descripcion.trim());

            adopcionDao.anadirSeguimiento(adopcion.getId(), registro);

            JOptionPane.showMessageDialog(this, "Seguimiento añadido correctamente");

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONSULTAR EL SEGUIMIENTO
    private void verSeguimiento() {

        try {

            Adopcion adopcion = obtenerAdopcion();

            List<Document> seguimiento = adopcionDao.consultarSeguimiento(adopcion.getId());

            if (seguimiento == null || seguimiento.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No hay registros de seguimiento");

                return;
            }

            StringBuilder texto = new StringBuilder();

            for (Document registro : seguimiento) {

                texto.append(registro.toJson());

                texto.append("\n\n");
            }

            JTextArea area = new JTextArea(texto.toString(), 15, 50);

            area.setEditable(false);
            area.setLineWrap(true);

            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Seguimiento de la adopción", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // LIMPIAR LOS CAMPOS
    private void limpiar() {

        lista.select(0, 0);

        idField.setEditable(true);

        idField.setText("");
        idAnimalField.setText("");
        dniAdoptanteField.setText("");
        fechaSolicitudField.setText("");
        fechaResolucionField.setText("");
        observacionesField.setText("");

        estadoComboBox.setSelectedIndex(0);
    }

    // MOSTRAR ERRORES
    private void mostrarError(Exception e) {

        String mensaje = e.getMessage();

        if (e instanceof NumberFormatException) {

            mensaje = "Introduce un identificador numérico";
        }

        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
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
        panelPrincipal.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(17, 4, new Insets(12, 12, 12, 12), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("GESTIÓN DE ADOPCIONES");
        panelPrincipal.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("ID adopción");
        panelPrincipal.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("ID animal");
        panelPrincipal.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idField = new JTextField();
        panelPrincipal.add(idField, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idAnimalField = new JTextField();
        panelPrincipal.add(idAnimalField, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("DNI adoptante");
        panelPrincipal.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Estado");
        panelPrincipal.add(label5, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dniAdoptanteField = new JTextField();
        panelPrincipal.add(dniAdoptanteField, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        estadoComboBox = new JComboBox();
        panelPrincipal.add(estadoComboBox, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Fecha solicitud (dd/MM/yyyy)");
        panelPrincipal.add(label6, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Fecha resolución (dd/MM/yyyy)");
        panelPrincipal.add(label7, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fechaSolicitudField = new JTextField();
        panelPrincipal.add(fechaSolicitudField, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fechaResolucionField = new JTextField();
        panelPrincipal.add(fechaResolucionField, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Observaciones");
        panelPrincipal.add(label8, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        label9.setText("Buscar (fecha inicial si buscas por fecha)");
        panelPrincipal.add(label9, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        criterioComboBox = new JComboBox();
        panelPrincipal.add(criterioComboBox, new com.intellij.uiDesigner.core.GridConstraints(11, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buscarField = new JTextField();
        panelPrincipal.add(buscarField, new com.intellij.uiDesigner.core.GridConstraints(11, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buscarButton = new JButton();
        buscarButton.setText("Buscar");
        panelPrincipal.add(buscarButton, new com.intellij.uiDesigner.core.GridConstraints(11, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Fecha hasta (dd/MM/yyyy)");
        panelPrincipal.add(label10, new com.intellij.uiDesigner.core.GridConstraints(12, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        hastaField = new JTextField();
        panelPrincipal.add(hastaField, new com.intellij.uiDesigner.core.GridConstraints(12, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        asociadosButton = new JButton();
        asociadosButton.setText("Ver animal y adoptante");
        panelPrincipal.add(asociadosButton, new com.intellij.uiDesigner.core.GridConstraints(13, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        seguimientoButton = new JButton();
        seguimientoButton.setText("Ver seguimiento");
        panelPrincipal.add(seguimientoButton, new com.intellij.uiDesigner.core.GridConstraints(13, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registroButton = new JButton();
        registroButton.setText("Añadir seguimiento");
        panelPrincipal.add(registroButton, new com.intellij.uiDesigner.core.GridConstraints(13, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Listado de adopciones (pulsa una línea para seleccionar)");
        panelPrincipal.add(label11, new com.intellij.uiDesigner.core.GridConstraints(14, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelPrincipal.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(15, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lista = new JTextArea();
        scrollPane1.setViewportView(lista);
        listarButton = new JButton();
        listarButton.setText("Mostrar todos");
        panelPrincipal.add(listarButton, new com.intellij.uiDesigner.core.GridConstraints(16, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cerrarButton = new JButton();
        cerrarButton.setText("Cerrar");
        panelPrincipal.add(cerrarButton, new com.intellij.uiDesigner.core.GridConstraints(16, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panelPrincipal.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        observacionesField = new JTextArea();
        observacionesField.setText("");
        scrollPane2.setViewportView(observacionesField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}