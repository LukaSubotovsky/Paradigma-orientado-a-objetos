# Barrio cerrado Los Aromos

Trabajo práctico de Paradigma Orientado a Objetos, UADE. Propuesta A: gestión de un barrio de 150 lotes.

## Responsable de la entrega

- Luka Subotovsky — 1207995.

La etapa 1 se inició en grupo con Perez Calle Maribel (1172744) y Juan Ignacio Vara (1216613), cuyos nombres se conservan en los documentos originales. Luka continúa solo con esta entrega de la etapa 2. Los aportes efectivos se pueden consultar en el historial de Git.

## Qué hace

El programa permite gestionar reclamos, expensas y pagos, fallas de cámaras y luminarias, autorizaciones de visitas y reportes. Tiene un menú de consola con usuarios por rol y guarda la información en un archivo local. Está basado en el diseño de la etapa 1, con los cambios explicados en el [informe final](Docs/06_Informe_final.md).

## Requisitos

- JDK 17 o superior. Verificar con `java -version` y `javac -version`.
- Maven 3.8 o superior. Verificar con `mvn -version`.
- Internet en la primera compilación para descargar Maven/JUnit. La aplicación funciona sin conexión.

## Instalar, probar y ejecutar

Desde una terminal:

```sh
git clone https://github.com/LukaSubotovsky/Paradigma-orientado-a-objetos.git
cd Paradigma-orientado-a-objetos
mvn clean verify
java -jar target/los-aromos.jar --demo
```

La versión integrada está en `main`. El tag `etapa-2` identifica el commit de entrega.

`--demo` crea datos ficticios solamente si el archivo no existe. Si ya existe, lo carga: no borra el trabajo anterior. En las siguientes ejecuciones alcanza con:

```sh
java -jar target/los-aromos.jar
```

En IntelliJ se puede abrir `pom.xml` como proyecto Maven y ejecutar `Aplicacion.Main` con el argumento `--demo` la primera vez.

## Usuarios para probar

| Usuario | Clave de demostración | Permisos |
|---|---|---|
| admin | admin123 | Datos maestros, asignación, expensas, equipos y reportes |
| vecino | vecino123 | Titular: reclamos y autorizaciones |
| residente | residente123 | Residente del lote 1: autorizaciones de visitas |
| autorizado | autorizado123 | Autorizado del lote 1: reclamos |
| guardia | guardia123 | Accesos, fallas y reclamos que tiene asignados |
| tecnico | tecnico123 | Reclamos asignados y cierre de su mantenimiento |

Datos iniciales: 150 lotes de 100 m²; titular ficticio DNI 100, residente DNI 200 y autorizado DNI 300. Guardia legajo 1, técnico legajo 2. Cámara C1 y luminarias L1 a L4. No hay pagos ni reclamos precargados. El mismo titular en los 150 lotes simplifica la demo y está permitido por el enunciado. Para probar otra distribución se pueden crear datos desde Java o usar las pruebas unitarias; los lotes no cambian de titular (S23).

Las claves anteriores son públicas y se usan solo para la demo. Se guardan como hash SHA-256 con una sal por usuario. Es un control local educativo, no un sistema de autenticación para publicar en Internet.

## Uso del menú

- Escribir el número de opción y presionar Enter. Las opciones mostradas dependen del rol.
- Fechas: `AAAA-MM-DD`; períodos: `AAAA-MM`; horas: `HH:mm`.
- Importes: hasta dos decimales, con punto o coma. No usar separadores de miles.
- `90`: cambiar de usuario; `60`: guardar; `0`: guardar y salir.
- Los identificadores nuevos se muestran al registrarlos. El administrador consulta datos con `1`; cada usuario consulta sus reclamos con `16`.
- El sistema comprueba plazos al volver al menú. Si está cerrado, los avisos pendientes se generan al abrirlo. No funciona como servicio en segundo plano.

Recorrido sugerido: ingresar como vecino y cargar un reclamo (10); cambiar a admin y asignarlo (11); entrar como guardia o técnico responsable y avanzarlo (12). El [plan de pruebas](Docs/07_Plan_de_pruebas.md) incluye los demás recorridos.

## Archivos y recuperación

Por defecto se usa `datos/los-aromos.dat`. Se guarda después de cada operación exitosa y al salir. Antes de reemplazar el archivo se escribe uno temporal y se conserva la versión anterior en `datos/los-aromos.dat.bak`.

Para trabajar con otra copia de prueba:

```sh
java -jar target/los-aromos.jar --demo --archivo=datos/prueba.dat
```

Si el archivo está dañado, el programa informa el error y no lo sustituye por datos nuevos. Con la aplicación cerrada, conservar una copia del archivo dañado y restaurar `.bak` a `.dat`. No abrir dos instancias con el mismo archivo. El formato serializado corresponde a esta versión de clases; no se garantiza migración a versiones futuras.

## Organización

| Carpeta | Contenido |
|---|---|
| `src/main/java/Modelo` | Entidades, enumeraciones y reglas del barrio |
| `src/main/java/Controlador` | Operaciones y permisos de cada rol |
| `src/main/java/Vista` | Menú de consola |
| `src/main/java/Persistencia` | Lectura y escritura del barrio |
| `src/main/java/Aplicacion` | Inicio y datos ficticios |
| `src/test/java` | Pruebas JUnit 5 de negocio, permisos, archivos y menú |
| `Docs` | Diseño, informe, pruebas y guía de defensa |
| `Docs/UML` | Fuentes PlantUML e imágenes |

Pruebas: `mvn test`. Empaquetado y verificación: `mvn clean verify`. Resultados detallados: `target/surefire-reports`.

## Documentación de la entrega

- [Correspondencia UML y código](Docs/04_Etapa2_estructura_y_UML.md).
- [Informe final y decisiones](Docs/06_Informe_final.md).
- [Pruebas funcionales con resultados](Docs/07_Plan_de_pruebas.md).
- [Guía para explicar el programa](Docs/08_Guia_defensa.md).
- [Índice de diagramas](Docs/UML/LEEME.md).

La consigna original exige una revisión de los PR por otro integrante. Como esta etapa continúa con un solo integrante activo, los PR integrados no cuentan con esa revisión: corresponde informar esta situación al docente y consultar si acepta la entrega individual. La asistencia utilizada para preparar la implementación tampoco reemplaza la defensa oral individual.
