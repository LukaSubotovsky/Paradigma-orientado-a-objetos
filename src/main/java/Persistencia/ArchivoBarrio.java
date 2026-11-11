package Persistencia;

import Modelo.Barrio;
import java.io.*;
import java.nio.file.*;
public class ArchivoBarrio {
    private final Path archivo;
    public ArchivoBarrio(Path archivo) { this.archivo = archivo.toAbsolutePath(); }
    public boolean existe() { return Files.exists(archivo); }
    public Barrio cargar() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
            entrada.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
                    "maxdepth=80;maxrefs=1000000;maxbytes=50000000;Modelo.*;java.base/*;!*"));
            Object dato = entrada.readObject();
            if (!(dato instanceof Barrio)) throw new IOException("El archivo no contiene un barrio");
            return (Barrio) dato;
        }
    }
    public void guardar(Barrio barrio) throws IOException {
        Files.createDirectories(archivo.getParent());
        Path temporal = Files.createTempFile(archivo.getParent(), "aromos-", ".tmp");
        try {
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(temporal))) {
                salida.writeObject(barrio);
            }
            if (Files.exists(archivo)) Files.copy(archivo, archivo.resolveSibling(archivo.getFileName() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            try { Files.move(temporal, archivo, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING); }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }
}
