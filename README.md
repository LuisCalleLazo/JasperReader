# JasperReader Project

## Descripción
Este proyecto es una aplicación Java que genera informes usando JasperReports a partir de archivos JRXML (JasperSoft Studio).
En los siguientes formatos: xlsx, docx, pdf, txt, rtf, xml, pptx, html, odt, ods y csv

## Ejecución
Para ejecutar esta aplicación, usa el siguiente comando:

java -jar jasperreader.jar 

### Parámetros
- `-i`: Ruta al archivo JRXML de entrada.
- `-o`: Ruta al archivo de salida.
- `-f`: Formato de salida (pdf, csv, html, etc.).
- `-c`: Cadena de conexión a la base de datos.
- `-p`: Parámetros adicionales requeridos por el informe.
- `-usr`: Usuario de la base de datos.
- `-pass`: Contraseña de la base de datos.