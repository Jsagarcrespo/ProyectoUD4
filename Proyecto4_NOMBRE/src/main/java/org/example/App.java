package org.example;

import com.mongodb.MongoException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                try {

                    VentanaPrincipal ventana = new VentanaPrincipal();
                    ventana.setVisible(true);

                } catch (MongoException e) {

                    JOptionPane.showMessageDialog(null, "No se puede conectar con MongoDB. " + "Comprueba que Docker está iniciado.");
                }
            }
        });
    }
}