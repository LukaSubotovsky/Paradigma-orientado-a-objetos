# Plan de pruebas funcionales — Etapa 2

Se probaron los recorridos con JUnit 5 y una fecha fija (11/11/2026 a las 12:00) para que los plazos sean reproducibles. Se ejecutó `mvn clean verify` con Java 17 y Maven 3.9.9. Los casos de consola usan `MenuConsola` con entradas reales, guardan el archivo, y algunos vuelven a abrirlo. Las pruebas de negocio cubren además límites de fechas, roles y saldos. Resultado total: **47 pruebas, 0 fallas, 0 errores, 0 omitidas**. Se ejecutó el JAR aparte con `--demo` y se observó el menú de administrador.

| Caso | Datos de entrada | Esperado | Obtenido |
|---|---|---|---|
| Alta y seguimiento de reclamo | Vecino titular lote 1, Seguridad, «Portón roto»; admin asigna guardia 1; guardia inicia y comenta | Alta ABIERTO, ASIGNADO y EN_CURSO con historial y comentario | Coincide; se recuperó comentario después de volver a leer el archivo. |
| Autorización de reclamo | Persona sin vínculo con lote 1 intenta registrar | Rechazo sin crear reclamo | Rechazado; lista sin cambios. |
| Sexto reclamo en curso | Técnico 2 tiene cinco reclamos EN_CURSO | Rechazo del sexto; se acepta si resuelve uno | Coincide en ambos pasos. |
| Reapertura | Vecino pide reabrir exactamente a los 5 días de resolver; luego pasa el límite un segundo | A los 5 días vuelve a ASIGNADO; después se cierra y no se reabre | Coincide. |
| Escalación urgente | Seguridad sin llegar a EN_CURSO durante 24 horas | Un solo aviso al administrador | 0 avisos antes, 1 a las 24 horas, sin duplicación. |
| Emisión | 150 lotes de 100 m², presupuesto 100,00, período noviembre | 150 expensas, total 100,00, centavos ajustados | Lote 1: 0,67; lote 150: 0,66; suma 100,00. |
| Cálculo de mora | Expensa de 1000 vence 10/10; consulta 10/10 y 11/10 | 0 y 1 de interés, respectivamente | Coincide. |
| Pago parcial | Expensa 1000; pago 510 el 20/10; consulta 30/10 | Se imputan 10 a interés, 500 a capital; luego capital 500 e interés 5 | Coincide. |
| Pago solo a interés | Expensa 1000; pago 5 el 20/10; consulta 30/10 | Capital 1000 e interés pendiente 15 | Coincide. |
| Período más antiguo | Septiembre y octubre emitidos, pago el 20/10 | Primera aplicación a septiembre; luego octubre | Coincide. |
| Morosidad | Cuatro expensas vencidas, luego se paga toda la deuda | Una notificación, marca moroso; al pagar se quita | Coincide. |
| Ajuste | Pago original 510, ajuste -100 con motivo | Original 510 trazable, pago corregido 410, capital 600 | Coincide; se conservan aplicaciones originales y actuales. |
| Sobrepago | Intento de pago mayor que la deuda, o ajuste incompatible con otro pago posterior | Rechazo atómico, sin cambiar saldos | Coincide. |
| Cuarta luminaria | Cuatro fallas distintas en el mismo sector | Prioridad alta para las cuatro órdenes abiertas | Coincide. |
| Cámara | C1 sin funcionar 48 h; revisar a las 49 h y después | Sin aviso a las 48; uno a las 49; ninguno duplicado | Coincide. |
| Cierre de orden | Fecha/técnico/reparación faltantes; luego los tres completos | Impedir cierre incompleto; luego OPERATIVO | Coincide. |
| Ingreso sin autorización | Guardia intenta ingresar DNI 500 | Denegado, sin registro | Coincide. |
| Ingreso duplicado | Dos autorizaciones válidas para DNI 500, un ingreso abierto | Segundo ingreso denegado hasta registrar egreso | Coincide, incluso con autorización distinta. |
| Recurrente y puntual | Puntual ya usado; recurrente de 30 y de 31 días | Puntual un ingreso; recurrente máximo 30 días y uno por día | Coincide. |
| Reportes | Dos reclamos pendientes de distinta prioridad, filtro de fecha/tipo; equipo retirado | Ordenar y filtrar reclamos; mostrar solo FUERA_DE_SERVICIO | Coincide. |
| Permisos | Vecino emite expensas o lee morosidad; guardia intenta avanzar reclamo ajeno | Denegar y no alterar datos | Coincide. |
| Archivo | Guardar con reclamos, pagos, ajustes y accesos, abrir de nuevo | Mismos valores y relaciones; crear respaldo | Coincide. |
| Archivo dañado / error al guardar | Archivo inválido o bloqueo del respaldo | No reemplazar datos previos; informar problema | Coincide. |
| Menú | Usuario y clave inválidos, período mal formado; luego salir | Informar y continuar | Coincide. |

**Alcance del resultado:** la demo en vivo de todas las opciones queda para la defensa grupal; los recorridos principales se ejercitaron con entradas de consola y las reglas restantes con pruebas unitarias. La hora de producción depende del reloj local. No se simularon correos externos: las notificaciones se conservan para consulta del administrador.
