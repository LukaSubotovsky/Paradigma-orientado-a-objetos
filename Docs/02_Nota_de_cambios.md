# Nota de cambios del diagrama inicial al final

Propuesta A: Gestión del barrio cerrado Los Aromos.

Esta comparación documenta los diagramas JPG existentes en el repositorio. No afirma que las correcciones pendientes ya se hayan aplicado.

| Aspecto | Diseño inicial | Diseño final | Motivo |
|---|---|---|---|
| Coordinación | Gestión concentrada en entidades y GestionDeReclamo | Controladores de Reclamos, Expensa, Equipos, Visitas y Reportes | Separar interacción y coordinación; GRASP Controlador. |
| Reclamos | GestionDeReclamo y Solicitud separada | GestionReclamos, CambioEstado y Comentarios | Agrupar solicitudes y reclamos conforme S1 y conservar trazabilidad. |
| Roles de vecinos | Vecino y titular como atributo de Lote | Persona, VinculoLote y RolLote | Permitir distintos roles por persona y lote sin multiplicar subclases. |
| Expensas | RegistroDeExpensas y Pago con interés | Expensa, Pago, AplicacionPago, AsientoAjuste y CalculadorIntereses | Separar cálculo, imputación y correcciones; preservar pagos. |
| Parámetros | Valores no centralizados | Configuracion | Concentrar tasa, vencimiento y plazos; variaciones protegidas. |
| Morosidad | Sin representación específica del reporte | NotificacionMora y FilaMorosidad | Registrar notificaciones y transportar filas del reporte. |
| Accesos | Visita y RegistroDeVisita | Autorizacion y RegistroAcceso | Separar permiso previo del hecho de ingreso/egreso y permitir recurrencia. |
| Equipos | Equipo sin abstracción explícita y conexiones de herencia ambiguas | Equipo abstracto, Camara y Luminaria con prioridadOrden | Hacer explícito el polimorfismo para prioridades. |
| Fallas | RegistroDeFalla | FallaEquipo vinculada a Equipo | Registrar hechos de falla separados del estado actual. |
| Estados | Estados y tipos parcialmente definidos | Enumeraciones específicas para reclamos, expensas y equipos | Evitar valores ambiguos y distinguir ciclos de vida. |

## Aclaraciones para la defensa

- Los controladores coordinan; las clases con información y los servicios aplican las reglas correspondientes.
- La composición se usa para partes cuyo ciclo de vida depende del objeto que las contiene, por ejemplo historial y comentarios del reclamo.
- La comparación no demuestra una eliminación de herencia Administrador-Personal: esa herencia no está dibujada en el inicial. La justificación de Liskov debe basarse en relaciones efectivamente presentes.
- Persisten diferencias de nombres y firmas entre las imágenes y el informe. En particular, GestorReclamos/GestionReclamos y prioridadOrden con/sin parámetro requieren una decisión común del grupo.
- S23 excluye el cambio de titular, aunque el diagrama final muestra cambiarTitular; se debe resolver esa diferencia antes de cerrar el diseño.
