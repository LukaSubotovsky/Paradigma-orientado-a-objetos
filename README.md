# Barrio Cerrado Los Aromos

TPO de Programación Orientada a Objetos, UADE. La rama `etapa-2` inicia la implementación a partir del [UML final](Docs/UML/POO-TPO-Diag-Final.jpg) y la documentación de la etapa 1.

## Requisitos y ejecución

- JDK 17 o superior y Maven 3.8 o superior.
- `mvn test` compila el proyecto y ejecuta las pruebas JUnit 5.
- En IntelliJ IDEA, abrir la carpeta raíz como proyecto Maven y seleccionar un JDK 17 o superior.

## Organización

- `src/main/java/Modelo`: entidades, enumeraciones y reglas de dominio.
- `src/main/java/Vista`: interfaz de usuario.
- `src/main/java/Controlador`: coordinación entre vista y modelo.
- `src/test/java`: pruebas JUnit 5 con la misma estructura de paquetes.

La correspondencia entre el código actual y el UML está en [Docs/04_Etapa2_estructura_y_UML.md](Docs/04_Etapa2_estructura_y_UML.md). La estructura inicial aún no contiene una aplicación ejecutable.
