# Nota de cambios del diagrama inicial al final

**Propuesta A: Gestión del barrio cerrado Los Aromos.** La comparación se basa en los diagramas inicial y final existentes, y en las reglas y supuestos del informe.

| Aspecto | Diseño inicial | Diseño final | Motivo |
|---|---|---|---|
| Coordinación | Responsabilidades concentradas en gestores y entidades | Controladores de reclamos, expensas, equipos, visitas y reportes | Separar la interacción del usuario de la coordinación del sistema (GRASP Controlador). |
| Reclamos | Gestión de reclamos y solicitudes separadas | `GestionReclamos`, `Reclamo`, `CambioEstado` y `Comentario` | S1 unifica reclamo y solicitud; el historial conserva trazabilidad. |
| Roles del lote | Vecino y titular representados directamente en el lote | `Persona`, `VinculoLote` y `RolLote` | Representar titulares, residentes y autorizados sin crear una subclase por rol. |
| Expensas | Registro de expensas y pago con cálculo asociado | `Expensa`, `Pago`, `AplicacionPago`, `AsientoAjuste` y `CalculadorIntereses` | Separar deuda, pagos, imputación y correcciones; cumplir RN10–RN13. |
| Parámetros | Valores de reglas dispersos | `Configuracion` | Centralizar tasa y plazos para facilitar cambios. |
| Morosidad | Sin objeto para cada fila del informe | `NotificacionMora` y `FilaMorosidad` | Separar el aviso formal del dato que presenta el reporte. La consulta aplica RN12 (>3 períodos). |
| Accesos | Visita y registro de visita | `Autorizacion` y `RegistroAcceso` | Distinguir permiso previo del ingreso/egreso; contemplar recurrencia según RN18–RN20. |
| Equipos | Tipo y comportamiento de equipos poco explícitos | `Equipo` abstracto con `Camara` y `Luminaria` | Aplicar prioridad polimórfica, con la misma firma en la clase abstracta y sus subclases, según RN15. |
| Fallas y órdenes | Registro de falla y órdenes menos diferenciados | `FallaEquipo` y `OrdenMantenimiento` vinculados al equipo | Conservar el historial de fallas y órdenes (RN23). |
| Estados | Valores representados de forma parcial | Enumeraciones específicas para reclamos, expensas y equipos | Evitar mezclar estados de ciclos de vida diferentes. |

## Coherencia aplicada

- `GestionReclamos.registrar(...)` devuelve un único `Reclamo`, como lo declara `ControladorReclamos.registrarReclamo(...)`.
- Los nombres se normalizaron como `GestionReclamos`, `Comentario`, `ControladorExpensas` y `OrdenMantenimiento`.
- `Equipo.prioridadOrden(fallasEnSector)` es abstracto; `Camara` y `Luminaria` declaran la misma firma para permitir sobrescritura. La cámara devuelve prioridad alta; la luminaria la eleva si hay más de tres fallas de ese tipo en el sector (RN15).
- `Barrio.obtenerLotes()` se incorpora porque el reporte debe recorrer los lotes. `Lote.calcularDeudaIntereses(hasta)` recibe la fecha de corte. Solo se reportan lotes con más de tres períodos impagos (RN12).
- `ControladorVisitas` crea el `RegistroAcceso` después de validar autorización, franja horaria y que no haya un ingreso abierto (RN18–RN20). El egreso cierra ese registro.
- Se mantiene `Lote.cambiarTitular(...)` porque RF24 contempla gestionar titulares y RN21 establece que cada lote tiene uno solo. La operación debe preservar esa unicidad.

## Alcance de la comparación

La herencia `Administrador`–`Personal` no aparece en el diagrama inicial; por eso la nota no afirma que se haya eliminado. La justificación de Liskov debe apoyarse en una relación de herencia que sí figure en los diagramas. Los PNG/JPG de referencia se conservan; las fuentes PlantUML permiten revisar el diseño alineado antes de reemplazar las imágenes originales.
