# JasperReader Project

## Descripción
Este proyecto es una aplicación Java que genera informes usando JasperReports a partir de archivos JRXML (JasperSoft Studio).
En los siguientes formatos: xlsx, docx, pdf, txt, rtf, xml, pptx, html, odt, ods y csv

## Limitaciones
- No es capaz de generar subreportes

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

### Cadenas de conexión

- SQL SERVER
> "jdbc:sqlserver://<HOST>:<PORT>;databaseName=<DATABASE>;user=<USERNAME>;password=<PASSWORD>;encrypt=false";

- POSTGRESQL
> "jdbc:postgresql://<HOST>:<PORT>/<DATABASE>";

- MYSQL - Parameters (-usr, -pass) any value
> "jdbc:mysql://<HOST>:<PORT>/<DATABASE>?user=<USERNAME>&password=<PASSWORD>";

- MONGO DB - Parameters (-usr, -pass) any value
> "mongodb://<USERNAME>:<PASSWORD>@<HOST>:<PORT>/<DATABASE>";


## CMD
java -jar jasperreader.jar -i "C:/reports/Report.jrxml" -o "C:/Users/you/Desktop/report.pdf" -f pdf -c "jdbc:sqlserver://127.0.0.1;databaseName=DBName;encrypt=true;trustServerCertificate=true" -p Param1==>"GP" -p LogoEmpresa==>"C:/Users/you/Desktop/logo_empresa.png" -usr sa -pass 1234567