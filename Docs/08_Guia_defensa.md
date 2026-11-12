# Guía breve para explicar el programa

Esta guía sirve para estudiar el código y practicar. No reemplaza comprenderlo.

## Recorrido de una operación

Al registrar un reclamo: `MenuConsola` pide los datos, `ControladorReclamos` comprueba el rol, `GestionReclamos` pregunta al lote si la persona está habilitada y crea el reclamo. El menú guarda el barrio mediante `ArchivoBarrio`.

## Preguntas para practicar

1. **¿Por qué MVC?** Para separar la entrada/salida de los casos de uso y de las reglas. Cambiar el menú no debería cambiar el cálculo de interés.
2. **¿Dónde hay herencia y polimorfismo?** `Camara` y `Luminaria` heredan de `Equipo`. El gestor llama `prioridadOrden` y cada tipo devuelve su prioridad.
3. **¿Qué es la composición?** Un reclamo conserva sus cambios de estado y comentarios. Se crean y se consultan desde ese reclamo.
4. **¿Por qué BigDecimal?** Porque las expensas son dinero. Se necesitan centavos y un redondeo explícito.
5. **¿Cómo se calcula un pago parcial?** Primero se calcula el interés de la expensa más antigua, se paga ese interés y después capital. Se sigue con el período siguiente si sobra dinero.
6. **¿Qué pasa si el pago estaba mal?** No se elimina. Se registra un ajuste y se reconstruyen las imputaciones; queda el original para consultar.
7. **¿Dónde se usa Comparable?** En lotes por número y en expensas por período. `Comparator` sirve para ordenar reclamos por prioridad y fecha, sin cambiar su clase.
8. **¿Para qué sirve la excepción propia?** Para informar que una operación viola una regla, como intentar ingresar sin autorización. El menú la captura y continúa.
9. **¿Cómo se conserva la información?** Se serializa el barrio completo. Los objetos recuperados mantienen sus referencias. Se guarda un archivo de respaldo.
10. **¿Por qué un reloj en los controladores?** En uso normal toma la hora del equipo. En las pruebas se fija una fecha para comprobar exactamente 24 horas, cinco días y otros límites.
11. **¿Qué evita que un vecino emita expensas?** El controlador exige el rol administrador. Ocultar la opción en el menú no sería suficiente.
12. **¿Dónde se ven los patrones?** Consultar la tabla del informe y abrir las clases mencionadas; explicar un método concreto, no solo la definición.

## Demostración de unos diez minutos

1. Ejecutar `mvn test` y mostrar resultados.
2. Iniciar con `--demo --archivo=datos/defensa.dat` para usar una copia de prueba.
3. Vecino: cargar reclamo, anotar número. Admin: asignar al legajo 1. Guardia: avanzar, comentar y mostrar historial.
4. Admin: emitir el mes actual con presupuesto 150000; registrar un pago y consultar capital/interés.
5. Guardia: informar falla en C1. Admin: iniciar reparación con legajo 2. Técnico: cerrar con descripción.
6. Vecino: autorizar DNI 500 para hoy en una franja que incluya la hora actual. Guardia: registrar ingreso, intentar repetirlo y registrar egreso.
7. Admin: consultar reportes. Cerrar y volver a abrir el mismo archivo para demostrar persistencia.

Cada integrante debe practicar cambios pequeños: agregar una validación, explicar una prueba y ubicar una responsabilidad. Las claves y las personas de demostración son ficticias.
