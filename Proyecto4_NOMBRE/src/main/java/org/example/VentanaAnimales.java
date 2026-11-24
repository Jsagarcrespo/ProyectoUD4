package org.example;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.dao.AnimalDao;
import org.example.modelos.Animal;

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

public class VentanaAnimales extends JFrame {

    private JPanel panelPrincipal;
    private JTextField idField;
    private JTextField nombreField;
    private JTextField especieField;
    private JTextField razaField;
    private JTextField edadField;
    private JTextField sexoField;
    private JTextArea descripcionField;
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
    private JTextArea lista;

    private AnimalDao animalDao;
    // Guarda los registros en el mismo orden que el listado.
    private List<Animal> registrosMostrados = new ArrayList<>();

    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

    // CONSTRUCTOR
    public VentanaAnimales(MongoDatabase db) {

        animalDao = new AnimalDao(db);

        setTitle("Gestión de animales");
        setContentPane(panelPrincipal);
        setSize(850, 650);
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

        // Al seleccionar una línea cargamos los datos del animal.
        // Al pulsar una línea del listado, cargamos el registro.
        lista.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarAnimal(e);
            }
        });
    }

    // MOSTRAR LOS ANIMALES EN EL LISTADO
    private void mostrarAnimales(List<Animal> animales) {

        // Eliminamos los resultados anteriores.
        registrosMostrados = new ArrayList<>(animales);
        lista.setText("");

        for (Animal animal : animales) {

            String fecha = "";

            if (animal.getFechaIngreso() != null) {

                fecha = formato.format(animal.getFechaIngreso());
            }

            String texto = "ID: " + animal.getId() + " | Nombre: " + animal.getNombre()
                    + " | Especie: " + animal.getEspecie() + " | Raza: " + animal.getRaza()
                    + " | Edad: " + animal.getEdad() + " | Sexo: " + animal.getSexo()
                    + " | Descripción: " + animal.getDescripcion()
                    + " | Ingreso: " + fecha + " | Estado: " + animal.getEstado();

            // Un registro ocupa una sola línea, aunque tenga saltos de línea.
            lista.append(texto.replace('\r', ' ').replace('\n', ' ') + "\n");
        }
        lista.setCaretPosition(0);
    }

    // LISTAR TODOS LOS ANIMALES
    private void listar() {

        // Una nueva consulta invalida la seleccion anterior.
        // Limpiar no borra los campos del buscador.
        limpiar();

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

        // Una nueva consulta invalida la seleccion anterior.
        // Limpiar no borra los campos del buscador.
        limpiar();

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

    // SELECCIONAR ANIMAL DEL LISTADO
    private void seleccionarAnimal(MouseEvent e) {

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

                Animal animal = animalDao.find(id);

                if (animal != null) {

                    lista.select(lista.getLineStartOffset(fila),
                            lista.getLineEndOffset(fila));

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

        } catch (Exception ex) {

            mostrarError(ex);
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

        lista.select(0, 0);

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
        panelPrincipal.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(18, 4, new Insets(12, 12, 12, 12), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("GESTIÓN DE ANIMALES");
        panelPrincipal.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("ID");
        panelPrincipal.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Nombre");
        panelPrincipal.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idField = new JTextField();
        panelPrincipal.add(idField, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nombreField = new JTextField();
        panelPrincipal.add(nombreField, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Especie");
        panelPrincipal.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Raza");
        panelPrincipal.add(label5, new com.intellij.uiDesigner.core.GridConstraints(3, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        especieField = new JTextField();
        panelPrincipal.add(especieField, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        razaField = new JTextField();
        panelPrincipal.add(razaField, new com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Edad");
        panelPrincipal.add(label6, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Sexo");
        panelPrincipal.add(label7, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        edadField = new JTextField();
        panelPrincipal.add(edadField, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sexoField = new JTextField();
        panelPrincipal.add(sexoField, new com.intellij.uiDesigner.core.GridConstraints(6, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Fecha ingreso (dd/MM/yyyy)");
        panelPrincipal.add(label8, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Estado");
        panelPrincipal.add(label9, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fechaIngresoField = new JTextField();
        panelPrincipal.add(fechaIngresoField, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        estadoComboBox = new JComboBox();
        panelPrincipal.add(estadoComboBox, new com.intellij.uiDesigner.core.GridConstraints(8, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Descripción");
        panelPrincipal.add(label10, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        anadirButton = new JButton();
        anadirButton.setText("Añadir");
        panelPrincipal.add(anadirButton, new com.intellij.uiDesigner.core.GridConstraints(11, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modificarButton = new JButton();
        modificarButton.setText("Modificar");
        panelPrincipal.add(modificarButton, new com.intellij.uiDesigner.core.GridConstraints(11, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        eliminarButton = new JButton();
        eliminarButton.setText("Eliminar");
        panelPrincipal.add(eliminarButton, new com.intellij.uiDesigner.core.GridConstraints(11, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        limpiarButton = new JButton();
        limpiarButton.setText("Limpiar");
        panelPrincipal.add(limpiarButton, new com.intellij.uiDesigner.core.GridConstraints(11, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Buscar");
        panelPrincipal.add(label11, new com.intellij.uiDesigner.core.GridConstraints(12, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        criterioComboBox = new JComboBox();
        panelPrincipal.add(criterioComboBox, new com.intellij.uiDesigner.core.GridConstraints(13, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buscarField = new JTextField();
        panelPrincipal.add(buscarField, new com.intellij.uiDesigner.core.GridConstraints(13, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buscarButton = new JButton();
        buscarButton.setText("Buscar");
        panelPrincipal.add(buscarButton, new com.intellij.uiDesigner.core.GridConstraints(13, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        historialButton = new JButton();
        historialButton.setText("Ver historial");
        panelPrincipal.add(historialButton, new com.intellij.uiDesigner.core.GridConstraints(14, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registroButton = new JButton();
        registroButton.setText("Añadir registro médico");
        panelPrincipal.add(registroButton, new com.intellij.uiDesigner.core.GridConstraints(14, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Listado de animales (pulsa una línea para seleccionar)");
        panelPrincipal.add(label12, new com.intellij.uiDesigner.core.GridConstraints(15, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelPrincipal.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(16, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lista = new JTextArea();
        scrollPane1.setViewportView(lista);
        listarButton = new JButton();
        listarButton.setText("Mostrar todos");
        panelPrincipal.add(listarButton, new com.intellij.uiDesigner.core.GridConstraints(17, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cerrarButton = new JButton();
        cerrarButton.setText("Cerrar");
        panelPrincipal.add(cerrarButton, new com.intellij.uiDesigner.core.GridConstraints(17, 2, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panelPrincipal.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(10, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        descripcionField = new JTextArea();
        scrollPane2.setViewportView(descripcionField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrincipal;
    }
}