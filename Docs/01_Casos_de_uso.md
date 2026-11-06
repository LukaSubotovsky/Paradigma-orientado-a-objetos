# Descripciones de casos de uso - Los Aromos

Propuesta A. Integrantes: Perez Calle Maribel (1172744), Juan Ignacio Vara (1216613), Luka Subotovsky (1207995).

Complemento del diseño del repositorio revisado. Los casos desglosan los RF1-RF24; varios subprocesos se agrupan en un mismo caso. Los casos automáticos describen comportamiento del sistema, sin convertir al sistema en actor externo. Los roles de edición y algunas validaciones no definidos en el enunciado se indican como supuestos complementarios y deben ser aceptados por el grupo.

## CU01. Registrar reclamo

**Actor:** Titular o persona autorizada.

**Precondiciones:** Lote y persona registrados; vínculo habilitado.

**Flujo principal:**
1. Seleccionar lote.
2. ingresar tipo y descripción.
3. validar autorización.
4. asignar número, fecha, prioridad y estado abierto.
5. guardar reclamo e historial inicial.

**Alternativas / errores:** Sin autorización: rechazar. Datos incompletos: solicitar completar.

**Postcondición:** Reclamo abierto y vinculado al lote y al solicitante.

## CU02. Asignar reclamo

**Actor:** Administrador.

**Precondiciones:** Reclamo abierto y miembro del personal registrado.

**Flujo principal:**
1. Seleccionar reclamo y responsable.
2. registrar asignación.
3. avanzar a asignado.
4. guardar fecha y observación.

**Alternativas / errores:** Responsable inexistente: rechazar. Estado incompatible: no modificar. El máximo de cinco se controla al pasar a en curso, según S5.

**Postcondición:** Un único responsable asignado y cambio registrado.

## CU03. Consultar y seguir reclamo

**Actor:** Vecino, administrador o personal responsable.

**Precondiciones:** Reclamo existente y acceso permitido.

**Flujo principal:**
1. Buscar reclamo.
2. mostrar estado, prioridad, responsable, historial y comentarios.

**Alternativas / errores:** Reclamo inexistente o acceso no permitido: informar sin mostrar datos.

**Postcondición:** Información consultada sin modificar el reclamo.

## CU04. Avanzar estado de reclamo

**Actor:** Administrador o personal responsable.

**Precondiciones:** Reclamo asignado o en curso.

**Flujo principal:**
1. Seleccionar reclamo.
2. solicitar siguiente estado.
3. validar transición.
4. si pasa a en curso, contar reclamos del responsable.
5. registrar estado, fecha y observación.

**Alternativas / errores:** Salto de estado: rechazar. Cinco reclamos ya en curso: impedir un sexto.

**Postcondición:** Estado actualizado y trazabilidad conservada.

## CU05. Reabrir reclamo

**Actor:** Titular o persona autorizada.

**Precondiciones:** Reclamo resuelto del lote; dentro de cinco días corridos.

**Flujo principal:**
1. Solicitar reapertura.
2. validar permiso y plazo.
3. volver a asignado con el mismo responsable.
4. registrar observación e historial.

**Alternativas / errores:** Plazo vencido o estado distinto de resuelto: rechazar.

**Postcondición:** Reclamo asignado nuevamente, conservando el historial.

## CU06. Agregar comentario

**Actor:** Vecino habilitado o personal.

**Precondiciones:** Reclamo existente; usuario autorizado.

**Flujo principal:**
1. Seleccionar reclamo.
2. ingresar texto.
3. registrar comentario con autor y fecha.

**Alternativas / errores:** Texto vacío o falta de permiso: no guardar.

**Postcondición:** Comentario agregado sin alterar comentarios previos.

## CU07. Cerrar reclamo

**Actor:** Administrador; sistema en cierre automático.

**Precondiciones:** Reclamo resuelto.

**Flujo principal:**
1. Verificar estado y plazo.
2. registrar cierre e historial. Para cierre automático, comprobar cinco días corridos sin reapertura.

**Alternativas / errores:** Reclamo reabierto o aún no resuelto: no cerrar.

**Postcondición:** Reclamo cerrado y evento de cierre registrado.

## CU08. Escalar reclamos urgentes

**Actor:** Sistema, con destinatario administrador.

**Precondiciones:** Reclamos de seguridad abiertos o asignados.

**Flujo principal:**
1. Revisar fechas.
2. detectar prioridad alta sin pasar a en curso en 24 horas.
3. informar al administrador y dejar constancia.

**Alternativas / errores:** Si ya fue atendido, no escalar. Revisión repetida: evitar avisos duplicados (supuesto operativo complementario).

**Postcondición:** Administrador informado de los reclamos urgentes pendientes.

## CU09. Emitir expensas

**Actor:** Administrador.

**Precondiciones:** Lotes y superficies cargados; presupuesto del período disponible.

**Flujo principal:**
1. Ingresar período y presupuesto.
2. calcular coeficientes por superficie.
3. emitir una expensa por lote.
4. fijar vencimiento el día 10.
5. guardar resultados.

**Alternativas / errores:** Superficie total inválida: impedir emisión. Período ya emitido: evitar duplicación (supuesto complementario).

**Postcondición:** Expensas del período registradas.

## CU10. Registrar pago e imputarlo

**Actor:** Administrador.

**Precondiciones:** Lote con expensas; importe positivo y medio válido.

**Flujo principal:**
1. Ingresar fecha, importe y medio.
2. calcular intereses.
3. ordenar períodos del más antiguo al más reciente.
4. imputar primero intereses y luego capital.
5. guardar pago y aplicaciones.
6. actualizar saldos y morosidad.

**Alternativas / errores:** Importe inválido: rechazar. Pago mayor que la deuda: solicitar un importe igual o menor (supuesto complementario, sin saldo a favor).

**Postcondición:** Pago y aplicaciones conservados; saldos actualizados.

## CU11. Calcular intereses por mora

**Actor:** Administrador o proceso de pagos/reportes.

**Precondiciones:** Expensa con vencimiento y saldo de capital conocidos.

**Flujo principal:**
1. Tomar fecha de cálculo.
2. contar días desde el día 11.
3. aplicar interés simple diario de 3 % / 30 al capital pendiente, respetando fechas de pagos.

**Alternativas / errores:** Hasta el día 10 o sin capital pendiente: interés cero.

**Postcondición:** Interés disponible para imputación o consulta.

## CU12. Verificar y notificar morosidad

**Actor:** Administrador o sistema.

**Precondiciones:** Expensas y aplicaciones de pago actualizadas.

**Flujo principal:**
1. Contar períodos vencidos con saldo.
2. marcar moroso con cuatro o más.
3. emitir notificación al titular al alcanzar la condición.
4. quitar marca cuando deje de cumplirla.

**Alternativas / errores:** Condición ya notificada: no duplicar aviso. Menos de cuatro períodos: no marcar moroso.

**Postcondición:** Condición y notificaciones consistentes con la deuda.

## CU13. Registrar ajuste de pago

**Actor:** Administrador.

**Precondiciones:** Pago existente con un error identificado.

**Flujo principal:**
1. Seleccionar pago.
2. ingresar motivo e importe de corrección.
3. registrar asiento relacionado.
4. recalcular aplicaciones, saldos y morosidad afectados.

**Alternativas / errores:** Motivo vacío o ajuste incompatible: rechazar (validación complementaria). No eliminar el pago original.

**Postcondición:** Ajuste trazable y pago original conservado.

## CU14. Registrar equipo

**Actor:** Administrador.

**Precondiciones:** Código de equipo disponible.

**Flujo principal:**
1. Elegir cámara o luminaria.
2. ingresar código, sector, fecha y datos específicos.
3. guardar equipo y estado inicial.

**Alternativas / errores:** Código duplicado o datos inválidos: rechazar. Estado inicial operativo salvo indicación expresa (supuesto complementario).

**Postcondición:** Equipo registrado con datos de su tipo.

## CU15. Registrar falla de equipo

**Actor:** Vecino o guardia de seguridad.

**Precondiciones:** Equipo existente.

**Flujo principal:**
1. Seleccionar equipo.
2. ingresar descripción.
3. registrar fecha y reportante.
4. pasar equipo a con falla.
5. dejar falla disponible para mantenimiento.

**Alternativas / errores:** Equipo inexistente: rechazar. Falla repetida: informar el registro vigente antes de duplicar (supuesto complementario).

**Postcondición:** Falla registrada y estado actualizado.

## CU16. Generar orden de mantenimiento

**Actor:** Administrador.

**Precondiciones:** Falla registrada y personal disponible.

**Flujo principal:**
1. Seleccionar equipo y responsable.
2. crear orden con apertura y descripción.
3. cámara: prioridad alta.
4. luminaria: normal, alta si hay más de tres falladas o en reparación en el sector.
5. actualizar órdenes abiertas del sector.

**Alternativas / errores:** Datos incompletos: no generar. Al iniciar la reparación, pasar equipo a en reparación.

**Postcondición:** Orden asignada con prioridad correspondiente.

## CU17. Cerrar orden de mantenimiento

**Actor:** Empleado de mantenimiento.

**Precondiciones:** Orden abierta con reparación terminada.

**Flujo principal:**
1. Seleccionar orden.
2. registrar fecha, técnico y descripción de reparación.
3. validar datos.
4. cerrar orden y actualizar estado del equipo según reparación.

**Alternativas / errores:** Si falta fecha, técnico o descripción: impedir cierre. Si continúa sin funcionar, conservar estado compatible y registrar la situación.

**Postcondición:** Cierre documentado e historial conservado.

## CU18. Consultar estado e historial de equipo

**Actor:** Administrador o guardia.

**Precondiciones:** Equipo registrado.

**Flujo principal:**
1. Buscar equipo.
2. mostrar estado, fallas y órdenes de mantenimiento.

**Alternativas / errores:** Código inexistente: informar sin modificar datos.

**Postcondición:** Estado e historial consultados.

## CU19. Informar cámara sin funcionar

**Actor:** Sistema, con destinatario administrador.

**Precondiciones:** Cámara con falla registrada y sin recuperación.

**Flujo principal:**
1. Contar horas desde la falla.
2. al superar 48 horas, informar al administrador y registrar aviso único.

**Alternativas / errores:** Menos de 48 horas, cámara reparada o aviso ya emitido: no generar otro.

**Postcondición:** Aviso de cámara fuera de funcionamiento registrado.

## CU20. Autorizar visita o proveedor

**Actor:** Titular o residente del lote.

**Precondiciones:** Persona habilitada y lote registrados.

**Flujo principal:**
1. Ingresar nombre y documento, lote, fechas y franja.
2. elegir puntual o recurrente.
3. validar vigencia.
4. guardar autorización.

**Alternativas / errores:** Recurrente mayor a 30 días o franja inválida: rechazar. Puntual: un ingreso; recurrente: uno por día según S18.

**Postcondición:** Autorización disponible para portería.

## CU21. Registrar ingreso y egreso

**Actor:** Guardia de seguridad.

**Precondiciones:** Visitante identificado por documento.

**Flujo principal:**
1. Consultar autorización para fecha y hora.
2. verificar vigencia y ausencia de ingreso abierto.
3. registrar ingreso y guardia.
4. al salir registrar egreso.

**Alternativas / errores:** Sin autorización o fuera de franja: denegar. Si sigue adentro: impedir otro ingreso. Egreso omitido: permitir egreso manual con observación según S19.

**Postcondición:** Acceso trazable con ingreso y posterior egreso.

## CU22. Emitir reportes

**Actor:** Administrador.

**Precondiciones:** Datos del sistema disponibles.

**Flujo principal:**
1. Seleccionar pendientes, morosidad o equipos fuera de servicio.
2. indicar filtros aplicables.
3. recuperar datos.
4. filtrar.
5. calcular capital e intereses para morosidad.
6. mostrar resultado.

**Alternativas / errores:** Sin coincidencias: mostrar reporte vacío. Filtro inválido: solicitar corrección.

**Postcondición:** Reporte generado sin modificar registros de negocio.

## CU23. Gestionar datos maestros

**Actor:** Administrador.

**Precondiciones:** Acceso al módulo de administración.

**Flujo principal:**
1. Registrar y consultar lotes, personas, vínculos y personal.
2. validar identificadores y superficies.
3. conservar un titular por lote y roles vigentes.

**Alternativas / errores:** Datos inválidos o identificadores duplicados: rechazar. Cambio de titular excluido por S23.

**Postcondición:** Datos maestros disponibles para los demás casos.

## Trazabilidad

RF1→CU01; RF2→CU02; RF3→CU04/CU07; RF4→CU05; RF5→CU06; RF6→CU03; RF7→CU08; RF8→CU09; RF9→CU10; RF10→CU11; RF11→CU10; RF12→CU12; RF13→CU13; RF14→CU14; RF15→CU15; RF16→CU16; RF17→CU17; RF18→CU18; RF19→CU19; RF20→CU20; RF21-RF22→CU21; RF23→CU22; RF24→CU23.
