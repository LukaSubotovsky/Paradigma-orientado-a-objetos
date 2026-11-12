package Aplicacion;

import Modelo.*;
import Persistencia.ArchivoBarrio;
import Vista.MenuConsola;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Arrays;
public class Main {
    public static void main(String[] args) {
        boolean demo = Arrays.asList(args).contains("--demo");
        String ruta = "datos/los-aromos.dat";
        for (String argumento : args) if (argumento.startsWith("--archivo=")) ruta = argumento.substring(10);
        ArchivoBarrio archivo = new ArchivoBarrio(Path.of(ruta));
        try {
            Barrio barrio;
            if (archivo.existe()) barrio = archivo.cargar();
            else if (demo) { barrio = DatosDemo.crear(); archivo.guardar(barrio); }
            else {
                System.out.println("No hay datos. Para probar: java -jar target/los-aromos.jar --demo"); return;
            }
            new MenuConsola(barrio, archivo, Clock.systemDefaultZone(), System.in, System.out).ejecutar();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("No se pudieron cargar o guardar los datos. No se reemplazó el archivo por una demo. " + e.getMessage());
            System.exit(1);
        } catch (ReglaNegocioException e) {
            System.err.println("No se pudo iniciar: " + e.getMessage()); System.exit(1);
        }
    }
}
