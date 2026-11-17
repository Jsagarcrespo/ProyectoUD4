package org.example;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.dao.AnimalDao;
import org.example.modelos.Animal;

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

public class VentanaAnimales extends JFrame {

    private JPanel panelPrincipal;
    private JTextField idField;
    private JTextField nombreField;
    private JTextField especieField;
    private JTextField razaField;
    private JTextField edadField;
    private JTextField sexoField;
    private JTextField descripcionField;
    private JTextField fechaIngresoField;
    private JComboBox<String> estadoComboBox;

    private JButton anadirButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private JButton limpiarButton;
    private JButton buscarButton;
    private JButton listarButton;
    private JButton historialButton;
    private JButton registroButton;
    private JButton cerrarButton;

    private JComboBox<String> criterioComboBox;
    private JTextField buscarField;
    private JTable tablaAnimales;

    private AnimalDao animalDao;
    private DefaultTableModel modeloTabla;

    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    // CONSTRUCTOR
    public VentanaAnimales(MongoDatabase db) {

        animalDao = new AnimalDao(db);

        setTitle("Gestión de animales");
        setContentPane(panelPrincipal);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        formato.setLenient(false);

        // Posibles estados de los animales.
        estadoComboBox.addItem("disponible");
        estadoComboBox.addItem("reservado");
        estadoComboBox.addItem("adoptado");
        estadoComboBox.addItem("en tratamiento");

        // Criterios de búsqueda.
        criterioComboBox.addItem("ID");
        criterioComboBox.addItem("Nombre");
        criterioComboBox.addItem("Especie");
        criterioComboBox.addItem("Raza");
        criterioComboBox.addItem("Edad");
        criterioComboBox.addItem("Estado");

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

        buscarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                buscar();
            }
        });

        listarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                listar();
            }
        });

        historialButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                verHistorial();
            }
        });

        registroButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                anadirRegistroMedico();
            }
        });

        cerrarButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
            }
        });

        // Al seleccionar una fila cargamos los datos del animal.
        tablaAnimales.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting()) {

                    seleccionarAnimal();
                }
            }
        });
    }

    // PREPARAR LA TABLA
    private void prepararTabla() {

        String[] columnas = {"ID", "Nombre", "Especie", "Raza", "Edad", "Sexo", "Descripción", "Fecha ingreso", "Estado"};

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {

                return false;
            }
        };

        tablaAnimales.setModel(modeloTabla);

        tablaAnimales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // MOSTRAR LOS ANIMALES EN LA TABLA
    private void mostrarAnimales(List<Animal> animales) {

        // Eliminamos las filas anteriores.
        modeloTabla.setRowCount(0);

        for (Animal animal : animales) {

            String fecha = "";

            if (animal.getFechaIngreso() != null) {

                fecha = formato.format(animal.getFechaIngreso());
            }

            modeloTabla.addRow(new Object[]{animal.getId(), animal.getNombre(), animal.getEspecie(), animal.getRaza(), animal.getEdad(), animal.getSexo(), animal.getDescripcion(), fecha, animal.getEstado()});
        }
    }

    // LISTAR TODOS LOS ANIMALES
    private void listar() {

        try {

            mostrarAnimales(animalDao.listar());

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // RECOGER LOS DATOS INTRODUCIDOS
    private Animal recogerDatos() throws Exception {

        if (idField.getText().trim().isEmpty() || nombreField.getText().trim().isEmpty() || especieField.getText().trim().isEmpty() || razaField.getText().trim().isEmpty() || edadField.getText().trim().isEmpty() || sexoField.getText().trim().isEmpty() || fechaIngresoField.getText().trim().isEmpty()) {

            throw new IllegalArgumentException("Rellena todos los campos obligatorios");
        }

        int id = Integer.parseInt(idField.getText().trim());

        int edad = Integer.parseInt(edadField.getText().trim());

        if (id <= 0 || edad < 0) {

            throw new IllegalArgumentException("El ID o la edad no son válidos");
        }

        String fechaTexto = fechaIngresoField.getText().trim();

        if (!fechaTexto.matches("\\d{2}/\\d{2}/\\d{4}")) {

            throw new IllegalArgumentException("Introduce la fecha en formato dd/MM/yyyy");
        }

        Date fechaIngreso = formato.parse(fechaTexto);

        return new Animal(id, nombreField.getText().trim(), especieField.getText().trim(), razaField.getText().trim(), edad, sexoField.getText().trim(), descripcionField.getText().trim(), fechaIngreso, (String) estadoComboBox.getSelectedItem());
    }

    // AÑADIR ANIMAL
    private void anadir() {

        try {

            Animal animal = recogerDatos();

            animalDao.create(animal);

            JOptionPane.showMessageDialog(this, "Animal añadido correctamente");

            limpiar();
            listar();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // MODIFICAR ANIMAL
    private void modificar() {

        try {

            Animal animal = recogerDatos();

            if (animalDao.find(animal.getId()) == null) {

                throw new IllegalArgumentException("El animal no existe");
            }

            animalDao.update(animal);

            JOptionPane.showMessageDialog(this, "Animal modificado correctamente");

            listar();

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // ELIMINAR ANIMAL
    private void eliminar() {

        try {

            int id = Integer.parseInt(idField.getText().trim());

            if (animalDao.find(id) == null) {

                throw new IllegalArgumentException("El animal no existe");
            }

            int respuesta = JOptionPane.showConfirmDialog(this, "¿Quieres eliminar este animal?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {

                animalDao.delete(id);

                JOptionPane.showMessageDialog(this, "Animal eliminado correctamente");

                limpiar();

                listar();
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // BUSCAR ANIMALES
    private void buscar() {

        try {

            String criterio = (String) criterioComboBox.getSelectedItem();

            String valor = buscarField.getText().trim();

            if (valor.isEmpty()) {

                throw new IllegalArgumentException("Introduce un valor para buscar");
            }

            List<Animal> animales = new ArrayList<>();

            switch (criterio) {

                case "ID":

                    Animal animal = animalDao.find(Integer.parseInt(valor));

                    if (animal != null) {

                        animales.add(animal);
                    }

                    break;

                case "Nombre":

                    animales = animalDao.buscarPorNombre(valor);
                    break;

                case "Especie":

                    animales = animalDao.buscarPorEspecie(valor);
                    break;

                case "Raza":

                    animales = animalDao.buscarPorRaza(valor);
                    break;

                case "Edad":

                    animales = animalDao.buscarPorEdad(Integer.parseInt(valor));
                    break;

                case "Estado":

                    animales = animalDao.buscarPorEstado(valor.toLowerCase());
                    break;
            }

            mostrarAnimales(animales);

            if (animales.isEmpty()) {

                JOptionPane.showMessageDialog(this, "No se han encontrado animales");
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // SELECCIONAR ANIMAL DE LA TABLA
    private void seleccionarAnimal() {

        try {

            int fila = tablaAnimales.getSelectedRow();

            if (fila != -1) {

                int id = (int) modeloTabla.getValueAt(fila, 0);

                Animal animal = animalDao.find(id);

                if (animal != null) {

                    idField.setText(String.valueOf(animal.getId()));
                    nombreField.setText(animal.getNombre());
                    especieField.setText(animal.getEspecie());
                    razaField.setText(animal.getRaza());
                    edadField.setText(String.valueOf(animal.getEdad()));
                    sexoField.setText(animal.getSexo());
                    descripcionField.setText(animal.getDescripcion());

                    if (animal.getFechaIngreso() != null) {

                        fechaIngresoField.setText(formato.format(animal.getFechaIngreso()));

                    } else {

                        fechaIngresoField.setText("");
                    }

                    estadoComboBox.setSelectedItem(animal.getEstado());

                    // El identificador no se puede modificar.
                    idField.setEditable(false);
                }
            }

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // CONSULTAR HISTORIAL MÉDICO
    private void verHistorial() {

        try {

            int id = Integer.parseInt(idField.getText().trim());

            List<Document> historial = animalDao.consultarHistorialMedico(id);

            if (historial == null || historial.isEmpty()) {

                JOptionPane.showMessageDialog(this, "El animal no tiene registros médicos");

                return;
            }

            StringBuilder texto = new StringBuilder();

            for (Document registro : historial) {

                texto.append(registro.toJson());
                texto.append("\n\n");
            }

            JTextArea area = new JTextArea(texto.toString(), 15, 50);

            area.setEditable(false);
            area.setLineWrap(true);

            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Historial médico", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // AÑADIR REGISTRO MÉDICO
    private void anadirRegistroMedico() {

        try {

            int id = Integer.parseInt(idField.getText().trim());

            if (animalDao.find(id) == null) {

                throw new IllegalArgumentException("El animal no existe");
            }

            String descripcion = JOptionPane.showInputDialog(this, "Introduce el registro médico");

            if (descripcion == null) {

                return;
            }

            if (descripcion.trim().isEmpty()) {

                throw new IllegalArgumentException("El registro médico está vacío");
            }

            // Creamos un documento que se guardará
            // dentro del array historialMedico.
            Document registro = new Document("fecha", new Date()).append("descripcion", descripcion.trim());

            animalDao.anadirRegistroMedico(id, registro);

            JOptionPane.showMessageDialog(this, "Registro médico añadido correctamente");

        } catch (Exception e) {

            mostrarError(e);
        }
    }

    // LIMPIAR LOS CAMPOS
    private void limpiar() {

        tablaAnimales.clearSelection();

        idField.setEditable(true);

        idField.setText("");
        nombreField.setText("");
        especieField.setText("");
        razaField.setText("");
        edadField.setText("");
        sexoField.setText("");
        descripcionField.setText("");
        fechaIngresoField.setText("");
        estadoComboBox.setSelectedIndex(0);
    }

    // MOSTRAR LOS ERRORES
    private void mostrarError(Exception e) {

        String mensaje = e.getMessage();

        if (e instanceof NumberFormatException) {

            mensaje = "Introduce un ID o una edad numérica";
        }

        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}