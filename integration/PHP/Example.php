



<?php

  /*
    In laravel: use App\Helpers\JasperConnection;
    In Native php: require_once('JasperConnection.php');
  */

  $name_file = "Demo";
  $jasper = new JasperConnection($name_file, "pdf");

  // Definir los parámetros
  $params = array(
    "LogoEmpresa" => getcwd() . "/imgs/google_logo.png",
    "Param1" => $param
  );

  $jasper->setFileReport("ReportDemoGrafic");
  $jasper->parameters = $params;
  $jasper->executeReport();