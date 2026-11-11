# Etapa 2, ítem 1: estructura y correspondencia con el UML

Referencia principal: `Docs/UML/POO-TPO-Diag-Final.jpg`. También se revisaron `Docs/UML/03_clases_final.puml`, `Docs/01_Casos_de_uso.md` y `Docs/02_Nota_de_cambios.md`.

## Estado del código al iniciar esta rama

El repositorio tenía solamente `src/Controlador/ControladorVisitas.java`, una clase vacía con declaración de paquete `Controlador`. No había descriptor de compilación, pruebas, clases del modelo ni vista. Se trasladó esa clase, sin agregarle comportamiento, a la estructura estándar de Maven. Los archivos `package-info.java` reservan los paquetes `Modelo` y `Vista`; no representan clases del dominio.

## Correspondencia pendiente

| Ubicación prevista | Clases del UML final | Estado |
| --- | --- | --- |
| `Modelo` | `Persona`, `Administrador`, `Personal`, `GuardiaDeSeguridad`, `EmpleadoDeMantenimiento`, `Barrio`, `Lote`, `VinculoLote` | Pendientes |
| `Modelo` | `Reclamo`, `GestionReclamos`, `CambioEstado`, `Comentario`, `Expensa`, `Pago`, `AplicacionPago`, `AsientoAjuste`, `CalculadorIntereses`, `Configuracion`, `NotificacionMora`, `FilaMorosidad` | Pendientes |
| `Modelo` | `Equipo`, `Camara`, `Luminaria`, `FallaEquipo`, `OrdenMantenimiento`, `Autorizacion`, `RegistroAcceso` | Pendientes |
| `Modelo` | `RolLote`, `EstadoReclamo`, `PrioridadReclamo`, `TipoReclamo`, `EstadoExpensa`, `MedioDePago`, `PrioridadOrden`, `EstadoEquipo`, `Sector`, `PuestoGuardia`, `Especialidad` | Enumeraciones pendientes |
| `Controlador` | `ControladorReclamos`, `ControladorExpensas`, `ControladorEquipos`, `ControladorReportes` | Pendientes |
| `Controlador` | `ControladorVisitas` | Existe como esqueleto, sin métodos |
| `Vista` | No hay una clase de vista especificada en el UML | Paquete reservado; diseño pendiente |

El JPG y la fuente PlantUML reconstruida difieren en algunos nombres y firmas: el JPG muestra `ControladorExpensa` y `Comentarios`, mientras que la fuente editable usa `ControladorExpensas` y `Comentario`. La nota de cambios favorece los nombres de la fuente editable. Antes de implementar esas clases, el equipo debe unificar el nombre elegido en código y diagramas. También hay diferencias de firmas entre ambas representaciones; no se asumió que la fuente `.puml` sea una copia literal del JPG.

Esta revisión establece el inventario estructural. La implementación de entidades, reglas de negocio, controladores, interfaz y pruebas funcionales corresponde a los ítems posteriores de la etapa 2.
