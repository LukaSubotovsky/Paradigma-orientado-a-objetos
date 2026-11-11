package Controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EstructuraProyectoTest {
    @Test
    void controladorVisitasEstaEnSuPaquete() {
        assertEquals("Controlador", ControladorVisitas.class.getPackageName());
    }
}
