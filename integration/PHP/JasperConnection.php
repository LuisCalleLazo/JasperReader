<?php
namespace App\Helpers;

/*
  In laravel: use App\Helpers\JasperReader;
  In Native php: require_once('JasperReader.php');
*/


class JasperConnection
{
  private string $inputFile;
  private bool $setValueFile;

  private string $outputFile;
  private string $format;

  public? array $parameters;
  private? array $connection;

  public function __construct(
    string $init_name_file,
    string $format = null
  ) {
    // FORMATO DE REPORTE
    if ($format === null) {
      $this->format = "pdf";
    } else {
      $this->format = $format;
    }

    // LECTURA DEL APPSETTINGS.JSON
    $dir_proyect = base_path();

    // LUGAR DE LOS REPORTES DE ENTRADA Y DE SALIDA
    $name_file = time() . '_'. $init_name_file;
    $this->setValueFile = false;
    $this->inputFile = $dir_proyect . "/app/Reports/";
    $this->outputFile = $dir_proyect . "/public/documents/$name_file";

    // CONECTAR A LA BD
    $this->setDatabase();
  }

  public function getOutputFile() : string
  {
    return $this->outputFile.'.'.$this->format;
  }

  private function setDatabase(): void
  {
    $db_server = env('DB_HOST_JASPER', '127.0.0.1');
    $db_name = env('DB_JASPER', 'master');
    $db_user = env('DB_USER_JASPER', 'sa');
    $db_pass = env('DB_PASS_JASPER', '1234');
    $db_encript = (env('DB_ENC_JASPER', 'true') == 1 ? "true" : "false");
    $db_cert = (env('DB_TRUST_SERVER_CERTIFICATE', 'true') == 1 ? "true" : "false");


    $this->connection = [
      "username" => $db_user,
      "password" => $db_pass,
      "jdbc_url" => "jdbc:sqlserver://$db_server;databaseName=$db_name;encrypt=$db_encript;trustServerCertificate=$db_cert"
    ];
  }

  public function setFileReport(string $nameReport): void
  {
    $this->inputFile .= $nameReport . ".jrxml";
    $this->setValueFile = true;
  }

  public function executeReport(): bool
  {
    if ($this->setValueFile) {
      $jasperReader = new JasperReader();

      try {
        // Procesar el archivo de entrada
        $jasperReader->process(
          $this->inputFile,
          $this->outputFile,
          $this->format,
          $this->parameters,
          $this->connection['jdbc_url'],
          $this->connection['username'],
          $this->connection['password'],
        );

        // Ejecutar el proceso
        $jasperReader->execute();
        return true;
      } catch (Exception $ex) {
        echo "Error: " . $ex->getMessage();
        return false;
      }
    } else {
      return false;
    }
  }
}
