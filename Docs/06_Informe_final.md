# Informe final — Los Aromos

Propuesta A. Responsable de la entrega de etapa 2: Luka Subotovsky (1207995). La etapa 1 se inició con Perez Calle Maribel (1172744) y Juan Ignacio Vara (1216613), que siguen identificados en los documentos originales.

## Resultado

Se implementaron los cinco grupos de casos de uso en Java 17, con menú de consola y persistencia local. Se mantuvieron los nombres y las responsabilidades principales de la etapa 1. No se usaron bases de datos, Spring ni una interfaz web.

## Cambios respecto del diseño original

| Cambio | Motivo |
|---|---|
| `LocalDate`, `LocalDateTime`, `LocalTime` y `YearMonth` reemplazan fechas de texto y `Date` | Comparar plazos, meses y franjas sin interpretar cadenas en el modelo. |
| `BigDecimal` reemplaza `float`/`double` en importes y superficies | Evitar diferencias por punto flotante al repartir el presupuesto y pagar. |
| `GestionExpensas`, `GestionEquipos`, `GestionVisitas` | Los controladores coordinan y verifican permisos; las reglas quedan fuera de la vista. |
| `Usuario`, `Sesion`, `RolUsuario` | Concretar el supuesto S22 de acceso por usuario y rol. |
| `ArchivoBarrio`, `Main`, `DatosDemo`, `MenuConsola` | Resolver persistencia, inicio y la interfaz de consola permitida por la consigna. |
| `ReglaNegocioException` y `Validacion` | Comunicar reglas incumplidas sin cerrar la aplicación. |
| `ControladorDatos` y `ControladorBase` | Altas de datos maestros y elementos comunes de sesión/reloj. |
| `Autorizacion` crea su `RegistroAcceso`; `GestionVisitas` verifica todos los accesos del mismo DNI | Aplicar Creador y detectar un ingreso abierto aunque se use otra autorización. Se actualiza la secuencia. |
| `AplicacionPago` identifica la expensa por período dentro del lote | No necesita una referencia mutable a la expensa; permite reconstruir saldos. |
| `Pago` conserva aplicaciones originales y actuales, además de ajustes | Mantener trazabilidad después de corregir un importe. |
| Estado de expensa calculado con una fecha de consulta | Evitar un estado guardado que quede viejo al cambiar el día. |
| Enumeraciones en mayúsculas | Convención de Java; no cambia su significado. |

Las fuentes e imágenes de la etapa 1 quedan como antecedentes. El diseño vigente de la implementación es el conjunto de láminas `03a` a `03g`, el índice `03_clases_final.puml` y las dos secuencias actualizadas.

## Reglas y supuestos de implementación

Se mantienen S1–S23 del informe de etapa 1, con las siguientes precisiones:

1. La reapertura vuelve a ASIGNADO con el responsable anterior. Se permite hasta exactamente cinco días después de la resolución; el cierre automático ocurre después de ese instante. Se valida nuevamente el máximo de cinco al volver a EN_CURSO.
2. El reclamo se considera atendido cuando llegó a EN_CURSO. Una reapertura no genera otra escalación por la apertura original. La alerta de seguridad se produce desde las 24 horas; la de cámara, después de las 48.
3. Los importes se manejan en centavos. El presupuesto se reparte según superficie: se truncan los importes a dos decimales y los centavos restantes se distribuyen por número de lote. La suma es exactamente el presupuesto.
4. La emisión exige los 150 lotes y se realiza en orden de períodos, sin duplicados ni meses futuros. No se inserta una emisión anterior a movimientos ya cargados. Las superficies no se modifican después del alta.
5. Interés simple: capital pendiente × 0,03 × días/30. El día 10 no genera mora y el día 11 suma un día. En cada pago se redondea el interés a centavos, se paga primero ese interés y luego capital, comenzando por la expensa más antigua. No se cobra interés sobre interés.
6. No se admite saldo a favor. Pagos con fecha futura, no positivos o anteriores al último movimiento del lote se rechazan. La consulta de deuda es actual o proyectada, no una reconstrucción histórica anterior al último movimiento; esas fechas se rechazan explícitamente.
7. El ajuste registra una diferencia con signo y un motivo. Corrige el importe del pago en su fecha original y reconstruye todas las imputaciones posteriores. El importe original, las aplicaciones originales y el historial de ajustes se conservan. Si la corrección vuelve negativo un pago o genera un sobrepago posterior, se rechaza sin modificar el estado. Se debe corregir primero ese pago posterior.
8. Moroso significa cuatro o más períodos vencidos con deuda. Cuando deja de cumplir ese umbral se quita la marca; al cruzarlo otra vez se registra una nueva notificación al titular. Las notificaciones se consultan en pantalla y se guardan; no se envían correos reales.
9. Una falla crea inmediatamente una orden abierta sin técnico. El administrador asigna mantenimiento al iniciar la reparación; el técnico asignado registra el cierre. Esto concreta la regla de generación de órdenes de cámaras y luminarias.
10. Se cuentan luminarias distintas CON_FALLA o EN_REPARACION del sector (S14). Al aparecer la cuarta, las órdenes abiertas se elevan a ALTA y no bajan automáticamente. FUERA_DE_SERVICIO es una decisión explícita del administrador (S15); no se confunde con una falla común.
11. Una autorización puntual vale un día y un ingreso. Una recurrente dura hasta 30 días inclusive y permite un ingreso por día por autorización. La franja diaria no cruza medianoche. El DNI identifica al visitante en todo el barrio: no puede entrar nuevamente mientras tenga un ingreso abierto.
12. El egreso manual requiere observación y queda ligado al guardia que lo registra. No se permite un egreso anterior al ingreso.
13. Se comprueban los plazos al iniciar o volver al menú, utilizando la hora del equipo. No se requiere ejecutar hilos para este alcance. `Clock` permite fijar una fecha en las pruebas sin cambiar la hora de la computadora.
14. Un residente puede recibir también un vínculo AUTORIZADO para reclamar. Ser residente por sí solo habilita visitas, no reclamos. El titular no se cambia (S23).
15. El reporte de equipos fuera de servicio muestra el estado exacto FUERA_DE_SERVICIO. Otros equipos con falla se consultan en el detalle; no aparecen incorrectamente como retirados.
16. Los archivos son para una única instancia local. El control de roles evita operaciones de menú ajenas al actor; no pretende proteger el archivo frente al propietario de la computadora.

## Patrones aplicados

Se desarrollan los tres SOLID elegidos y cuatro GRASP. Otros patrones mencionados como posibilidades en el primer informe no se cuentan como requisitos adicionales.

| Principio/patrón | Problema | Decisión y clases | Ventaja |
|---|---|---|---|
| SRP | Reclamo mezclaba datos, plazos y límite de trabajo; la expensa podía mezclar saldo e interés. | `Reclamo` conserva estado/historial, `GestionReclamos` aplica las reglas; `CalculadorIntereses` calcula mora. | Una regla cambia en un lugar identificable. |
| OCP | La prioridad dependía de preguntar el tipo de equipo en varios lugares. | `Equipo.prioridadOrden(int)` es abstracto; `Camara` y `Luminaria` lo sobrescriben. | El gestor pide prioridad al equipo y no decide con un condicional por cada tipo. El conteo contextual de luminarias sigue siendo propio del requisito S14. |
| LSP | Titular y residente no son clases excluyentes; el administrador no es un operario asignable. | Roles en `VinculoLote`; `Administrador` hereda de `Persona`. Guardias y mantenimiento son `Personal`; cámaras y luminarias son `Equipo`. | Se usan las subclases mediante los tipos base respetando sus contratos. |
| Experto | Los cálculos podían quedar en la interfaz. | `Lote` conoce deuda y vínculos; `Expensa` su saldo; `RegistroAcceso` si la visita está adentro. | La regla queda junto a los datos necesarios. |
| Creador | No estaba claro quién crea objetos dependientes. | `Barrio` crea lotes, `Lote` vínculos, `Reclamo` historial y comentarios, `Autorizacion` accesos; `Pago` ajustes. | Las partes se agregan mediante el objeto que conserva su historia. |
| Controlador | La consola podía modificar las entidades sin verificar al actor. | `ControladorReclamos`, `ControladorExpensas`, `ControladorEquipos`, `ControladorVisitas`, `ControladorReportes` reciben los casos de uso y verifican la sesión. | La vista se ocupa de pedir y mostrar datos. |
| Alta cohesión | Una clase única habría reunido reglas, archivos y menú. | Gestores por módulo, `ArchivoBarrio` para archivos y `MenuConsola` para entrada/salida. | Los cambios y las pruebas se ubican por responsabilidad. |

## Relación con los contenidos de la materia

Se usaron clases y encapsulamiento, relaciones de composición y asociación, herencia, enumeraciones, clases abstractas, sobreescritura, colecciones, UML, SOLID/GRASP, excepciones, MVC y lectura/escritura de archivos. `Comparable` aparece en `Lote` y `Expensa`; `Comparator` ordena reportes por prioridad/fecha o código. La persistencia usa `try` con recursos y `finally` para eliminar temporales; el menú captura por separado errores de reglas, de formato y de archivo.

La elección de consola está permitida por la sección 7 de la consigna. Swing, hilos y Singleton aparecen en el material, pero no son obligatorios para esta propuesta y no se agregan solo para aumentar la complejidad.

El material recibido incluye contenidos de las clases 1, 2, 4, 5, 6, 8, 9, 10, 11, 12 y 14. Los ZIP de clases 3 y 7 estaban vacíos; no se recibió clase 13. Se utilizó la consigna completa como referencia del alcance.

## Dificultades y resolución

- **Intereses con pagos parciales:** multiplicar la deuda final por todos los días produce resultados incorrectos. Se conserva la fecha de cálculo y el saldo de cada tramo. Las pruebas incluyen pagar 510 sobre una deuda de 1000 después de diez días: 10 van a interés y 500 a capital; los siguientes diez días generan 5 de interés.
- **Ajustes contables:** un cambio puede volver inválido un pago posterior. Primero se reconstruye sobre copias; solo si todo es válido se registra el ajuste y se actualiza el original.
- **Ingreso repetido:** consultar únicamente la autorización elegida no alcanza. Se revisan los accesos de todas las autorizaciones del mismo documento.
- **Persistencia y errores:** se escribe un archivo temporal antes de reemplazar el anterior. Las pruebas comprueban que un archivo dañado no se borre y que un fallo al guardar conserve la última versión.
- **Coherencia documental:** algunas firmas del UML estaban expresadas de forma conceptual. Se actualizaron con los tipos y operaciones de Java y se dividió el diagrama final para que pueda leerse.

## Pruebas y puesta a punto

Se ejecutó `mvn clean verify` con JDK 17 y Maven 3.9.9. Los resultados verificables están en el [plan de pruebas](07_Plan_de_pruebas.md). También se ejecutó el JAR en un proceso separado, con creación inicial y reapertura de datos.

## Trabajo realizado y revisión

La etapa 1 y la estructura inicial se recibieron ya existentes en el repositorio. Luka continúa solo con la etapa 2. Esta implementación se preparó a su pedido, con asistencia de una herramienta de IA, en cambios separados de modelo y pruebas, aplicación y archivos, y documentación. El historial de Git conserva los aportes reales de la etapa 1; no se atribuyen a otros integrantes commits de esta implementación.

Los PR de implementación y documentación se integraron a `main`. Ninguno registra una revisión de otro integrante. La consigna original exige esa revisión y commits propios de todos los integrantes: al continuar la entrega de manera individual, Luka debe explicar el cambio de situación al docente y consultar si acepta esa excepción. Fusionar los PR no equivale a una revisión independiente. La versión integrada se identifica con el tag `etapa-2`; queda la demostración y defensa oral individual.

## Reflexión técnica

Separar el menú de las reglas permitió probar los casos de error sin ingresar todos los datos manualmente. Los errores más difíciles estuvieron en las fechas y en los importes, no en crear las clases. Para una versión posterior sería útil una interfaz gráfica y una base de datos; para este trabajo, consola y archivo permiten mostrar las reglas de forma directa.

En la defensa, Luka deberá poder explicar y modificar el código que presenta, en particular las reglas de pagos, los cambios frente al UML inicial, los permisos y la persistencia.
