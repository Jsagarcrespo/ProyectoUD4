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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
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
    private JTextField observacionesField;
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

    private JTable tablaAdopciones;
    private DefaultTableModel modeloTabla;

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
        setSize(1150, 650);
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

        tablaAdopciones.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting()) {

                    seleccionarAdopcion();
                }
            }
        });
    }

    // PREPARAR LA TABLA
    private void prepararTabla() {

        String[] columnas = {"ID", "ID Animal", "DNI Adoptante", "Fecha solicitud", "Fecha resolución", "Estado", "Observaciones"};

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        tablaAdopciones.setModel(modeloTabla);

        tablaAdopciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // MOSTRAR ADOPCIONES EN LA TABLA
    private void mostrarAdopciones(List<Adopcion> adopciones) {

        modeloTabla.setRowCount(0);

        for (Adopcion adopcion : adopciones) {

            String fechaSolicitud = "";
            String fechaResolucion = "";

            if (adopcion.getFechaSolicitud() != null) {

                fechaSolicitud = formato.format(adopcion.getFechaSolicitud());
            }

            if (adopcion.getFechaResolucion() != null) {

                fechaResolucion = formato.format(adopcion.getFechaResolucion());
            }

            modeloTabla.addRow(new Object[]{adopcion.getId(), adopcion.getIdAnimal(), adopcion.getDniAdoptante(), fechaSolicitud, fechaResolucion, adopcion.getEstado(), adopcion.getObservaciones()});
        }
    }

    // LISTAR TODAS LAS ADOPCIONES
    private void listar() {

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

            if (adopcionDao.find(adopcion.getId()) != null) {

                throw new IllegalArgumentException("Ya existe una adopción con ese ID");
            }

            // Inicializamos el seguimiento.
            adopcion.setSeguimiento(new ArrayList<>());

            // El DAO comprueba que existan el animal
            // y el adoptante asociados.
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

    // SELECCIONAR ADOPCIÓN DE LA TABLA
    private void seleccionarAdopcion() {

        try {

            int fila = tablaAdopciones.getSelectedRow();

            if (fila != -1) {

                int id = (int) modeloTabla.getValueAt(fila, 0);

                Adopcion adopcion = adopcionDao.find(id);

                if (adopcion != null) {

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

        } catch (Exception e) {

            mostrarError(e);
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

        tablaAdopciones.clearSelection();

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
}