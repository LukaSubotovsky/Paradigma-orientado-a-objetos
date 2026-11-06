# Cierre y creación del tag etapa-1

Un tag es una referencia de Git: subir este archivo no crea el tag.

## Antes de marcar la entrega

1. Subir los casos de uso y la nota de cambios a Docs.
2. Subir el PDF de diseño a Docs y las fuentes a Docs/UML.
3. Revisar y resolver las diferencias pendientes entre diagramas y texto. El PDF compilado conserva las imágenes del repositorio; no certifica que esas diferencias estén corregidas.
4. Integrar mediante un pull request revisado por otro integrante; verificar aportes propios de todos los integrantes.
5. Confirmar que main contiene la versión que se quiere entregar.

## Desde la página de GitHub

1. Abrir https://github.com/LukaSubotovsky/Paradigma-orientado-a-objetos/releases/new
2. En el selector de tag, escribir etapa-1 y elegir crear un nuevo tag.
3. Elegir como destino main solo después de verificar su contenido.
4. Título: Etapa 1 - Modelado y patrones.
5. Describir los documentos incluidos y publicar la versión.

## Alternativa por terminal, dentro de una copia local del repositorio

```bash
git switch main
git pull --ff-only origin main
git status
git tag -a etapa-1 -m "Entrega etapa 1: modelado y patrones"
git push origin etapa-1
```

Si el tag ya existe, no borrarlo ni moverlo automáticamente: comprobar a qué commit apunta.

## Comprobación

```bash
git ls-remote --tags origin etapa-1
```

Debe devolver una referencia refs/tags/etapa-1. En GitHub, abrir el tag y comprobar que contiene el PDF, las descripciones, la nota y los diagramas.

Este archivo no crea ramas, commits, tags ni releases. No se hicieron modificaciones en GitHub al generar estos entregables.
