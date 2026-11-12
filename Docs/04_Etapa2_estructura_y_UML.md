# Correspondencia entre UML e implementación

El proyecto dejó de ser una estructura vacía. La documentación vigente es el [informe final](06_Informe_final.md) y el [índice de diagramas](UML/LEEME.md).

| Área del modelo | Implementación |
|---|---|
| Personas y lotes | Persona, Administrador, Personal, GuardiaDeSeguridad, EmpleadoDeMantenimiento, Barrio, Lote, VinculoLote |
| Reclamos | Reclamo, CambioEstado, Comentario, GestionReclamos, ControladorReclamos |
| Expensas | Expensa, Pago, AplicacionPago, AsientoAjuste, CalculadorIntereses, Configuracion, NotificacionMora, FilaMorosidad, GestionExpensas, ControladorExpensas |
| Equipos | Equipo, Camara, Luminaria, FallaEquipo, OrdenMantenimiento, GestionEquipos, ControladorEquipos |
| Visitas | Autorizacion, RegistroAcceso, GestionVisitas, ControladorVisitas |
| Reportes | ControladorReportes usa Barrio y las consultas de las entidades |
| Interfaz y persistencia | MenuConsola, ArchivoBarrio, Main, DatosDemo |
| Acceso y validación | Usuario, Sesion, RolUsuario, Validacion, ReglaNegocioException, ControladorDatos, ControladorBase |

Todas las clases y enumeraciones del diseño de etapa 1 tienen implementación. Se agregaron servicios para conservar la separación de responsabilidades y las clases necesarias para ejecutar y guardar el programa. Los cambios de tipos, firmas y relaciones se justifican en el informe. Las láminas 03a–03g muestran atributos y operaciones de la versión implementada; se omiten getters y `toString` repetitivos para facilitar la lectura.
