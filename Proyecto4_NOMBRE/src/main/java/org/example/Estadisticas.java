package org.example;

import com.mongodb.client.MongoDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.example.dao.AnimalDao;
import org.example.dao.AdopcionDao;
import org.example.dao.AdoptanteDao;

import org.example.modelos.Animal;
import org.example.modelos.Adopcion;
import org.example.modelos.Adoptante;
import org.bson.Document;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Estadisticas {

    public static void mostrarEstadisticas(MongoDatabase db) {

        try {

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            formato.setLenient(false);

            String inicio = JOptionPane.showInputDialog("Introduce fecha inicial (dd/MM/yyyy)");

            if (inicio == null) {
                return;
            }

            String fin = JOptionPane.showInputDialog("Introduce fecha final (dd/MM/yyyy)");

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
            calendario.add(Calendar.DAY_OF_MONTH, 1);
            Date limite = calendario.getTime();

            AnimalDao animalDao = new AnimalDao(db);
            AdoptanteDao adoptanteDao = new AdoptanteDao(db);
            AdopcionDao adopcionDao = new AdopcionDao(db);

            List<Animal> animales = animalDao.listar();
            List<Adoptante> adoptantes = adoptanteDao.listar();
            List<Adopcion> adopciones = adopcionDao.listar();

            int ingresados = 0;
            int disponibles = 0;
            int reservados = 0;
            int adoptados = 0;
            int tratamiento = 0;

            int nuevosAdoptantes = 0;

            int solicitudes = 0;
            int aprobadas = 0;
            int rechazadas = 0;
            int pendientes = 0;

            int adopcionesRealizadas = 0;
            int adopcionesConFecha = 0;

            double totalDias = 0;

            Document especies = new Document();

            // ESTADISTICAS DE ANIMALES

            for (Animal animal : animales) {

                if (entreFechas(animal.getFechaIngreso(), desde, limite)) {

                    ingresados++;

                    if (animal.getEstado().equals("disponible")) {
                        disponibles++;
                    }

                    if (animal.getEstado().equals("reservado")) {
                        reservados++;
                    }

                    if (animal.getEstado().equals("adoptado")) {
                        adoptados++;
                    }

                    if (animal.getEstado().equals("en tratamiento")) {
                        tratamiento++;
                    }

                    String especie = animal.getEspecie();

                    if (especies.containsKey(especie)) {

                        int cantidad = especies.getInteger(especie);
                        especies.put(especie, cantidad + 1);

                    } else {

                        especies.put(especie, 1);
                    }
                }
            }

            // ESTADISTICAS DE ADOPTANTES

            for (Adoptante adoptante : adoptantes) {

                if (entreFechas(adoptante.getFechaRegistro(), desde, limite)) {

                    nuevosAdoptantes++;
                }
            }

            // ESTADISTICAS DE ADOPCIONES

            for (Adopcion adopcion : adopciones) {

                if (entreFechas(adopcion.getFechaSolicitud(), desde, limite)) {

                    solicitudes++;

                    if (adopcion.getEstado().equals("pendiente")) {
                        pendientes++;
                    }

                    if (adopcion.getEstado().equals("aprobada")) {
                        aprobadas++;
                    }

                    if (adopcion.getEstado().equals("rechazada")) {
                        rechazadas++;
                    }
                }

                // ADOPCIONES REALIZADAS EN EL PERIODO

                if (adopcion.getEstado().equals("aprobada") && entreFechas(adopcion.getFechaResolucion(), desde, limite)) {

                    adopcionesRealizadas++;

                    Animal animal = animalDao.find(adopcion.getIdAnimal());

                    if (animal != null && animal.getFechaIngreso() != null && !adopcion.getFechaResolucion().before(animal.getFechaIngreso())) {

                        long diferencia = adopcion.getFechaResolucion().getTime() - animal.getFechaIngreso().getTime();

                        totalDias += Math.round(diferencia / 86400000.0);

                        adopcionesConFecha++;
                    }
                }
            }

            // CALCULAR PORCENTAJES

            Double porcentajeAprobadas = null;
            Double porcentajeRechazadas = null;
            Double mediaDias = null;

            if (solicitudes > 0) {

                porcentajeAprobadas = aprobadas * 100.0 / solicitudes;
                porcentajeRechazadas = rechazadas * 100.0 / solicitudes;
            }

            if (adopcionesConFecha > 0) {

                mediaDias = totalDias / adopcionesConFecha;
            }

            // MOSTRAR RESULTADOS

            String texto = "ESTADISTICAS DEL REFUGIO\n\n"

                    + "Desde: " + inicio + "\n" + "Hasta: " + fin + "\n\n"

                    + "ANIMALES\n" + "Total registrados: " + animales.size() + "\n" + "Ingresados en el periodo: " + ingresados + "\n" + "Disponibles: " + disponibles + "\n" + "Reservados: " + reservados + "\n" + "Adoptados: " + adoptados + "\n" + "En tratamiento: " + tratamiento + "\n\n"

                    + "ADOPTANTES\n" + "Total registrados: " + adoptantes.size() + "\n" + "Nuevos adoptantes: " + nuevosAdoptantes + "\n\n"

                    + "ADOPCIONES\n" + "Solicitudes: " + solicitudes + "\n" + "Pendientes: " + pendientes + "\n" + "Aprobadas: " + aprobadas + "\n" + "Rechazadas: " + rechazadas + "\n" + "Adopciones realizadas: " + adopcionesRealizadas + "\n\n"

                    + "Porcentaje aprobadas: " + redondear(porcentajeAprobadas) + "\n" + "Porcentaje rechazadas: " + redondear(porcentajeRechazadas) + "\n" + "Media de dias hasta la adopcion: " + redondear(mediaDias) + "\n\n"

                    + "ESPECIES\n";

            for (String especie : especies.keySet()) {

                texto += especie + ": " + especies.getInteger(especie) + "\n";
            }

            JTextArea area = new JTextArea(texto, 25, 45);
            area.setEditable(false);

            JOptionPane.showMessageDialog(null, new JScrollPane(area), "Estadisticas", JOptionPane.INFORMATION_MESSAGE);

            // EXPORTAR RESULTADOS

            Document datos = new Document("desde", inicio).append("hasta", fin).append("totalAnimales", animales.size()).append("animalesIngresados", ingresados).append("disponibles", disponibles).append("reservados", reservados).append("adoptados", adoptados).append("enTratamiento", tratamiento).append("nuevosAdoptantes", nuevosAdoptantes).append("solicitudes", solicitudes).append("pendientes", pendientes).append("aprobadas", aprobadas).append("rechazadas", rechazadas).append("adopcionesRealizadas", adopcionesRealizadas).append("porcentajeAprobadas", porcentajeAprobadas).append("porcentajeRechazadas", porcentajeRechazadas).append("mediaDiasAdopcion", mediaDias).append("especies", especies);

            int opcion = JOptionPane.showConfirmDialog(null, "¿Quieres exportar las estadisticas a JSON?", "Exportar", JOptionPane.YES_NO_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {

                Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

                new File("ficheros").mkdirs();

                try (FileWriter writer = new FileWriter("ficheros/estadisticas.json")) {

                    gson.toJson(datos, writer);
                }

                JOptionPane.showMessageDialog(null, "Estadisticas exportadas correctamente");
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // COMPROBAR INTERVALO DE FECHAS

    private static boolean entreFechas(Date fecha, Date desde, Date limite) {

        return fecha != null && !fecha.before(desde) && fecha.before(limite);
    }

    // MOSTRAR DOS DECIMALES

    private static String redondear(Double valor) {

        if (valor == null) {
            return "Sin datos";
        }

        return String.format("%.2f", valor);
    }
}