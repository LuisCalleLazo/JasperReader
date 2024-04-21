<?php

namespace App\Helpers;

class JasperReader
{
  protected $executable = 'java -jar';
  protected $jar = "jasperreader.jar";
  protected $path_executable;
  protected $the_command;
  protected $windows = false;

  protected $formats = ['pdf', 'rtf', 'xlsx', 'docx', 'odt', 'ods', 'pptx', 'csv', 'html', 'xml'];

  function __construct()
  {
    $this->path_executable = base_path() . '/vendor/bin/'; // In Laravel, in case of native php specify the path of the .jar folder

    if (strtoupper(substr(PHP_OS, 0, 3)) === 'WIN') {
        $this->windows = true;
    }
  }

  public function process($input_file, $output_file = false, $format = "pdf", $parameters = [], $db_connection = "", $usr = "", $pass = "")
  {

    if (is_null($input_file) || empty($input_file)) {
        throw new \Exception('No input file', 1);
    }

    if (!in_array($format, $this->formats)) {
        throw new \Exception('Invalid format!', 1);
    }

    $command = $this->executable .' '. escapeshellarg($this->path_executable. $this->jar);

    $command .= ' -i ';

    $command .= "\"$input_file\"";

    if ($output_file !== false) {
        $output_file .= '.'.$format;
        $command .= ' -o ' . escapeshellarg($output_file);
    }

    $command .= ' -f ' . escapeshellarg($format);



    if(isset($db_connection)){
        $command .= ' -c ' . escapeshellarg($db_connection);
    }


    if (count($parameters) > 0) {
        foreach ($parameters as $key => $value) {
            $command .= ' -p '. escapeshellarg("$key==>$value");
        }
    }

    if (isset($usr)) {
        $command .= " -usr " . escapeshellarg($usr);
    }

    if (isset($pass)) {
        $command .= ' -pass ' . escapeshellarg($pass);
    }

    $this->the_command = $command;

    return $this;
  }

  public function output()
  {
    return $this->the_command;
  }

  public function execute($run_as_user = false)
  {
    if ($run_as_user !== false && strlen($run_as_user > 0) && !$this->windows) {
      $this->the_command = 'su -u ' . escapeshellarg($run_as_user) . " -c " . escapeshellarg($this->the_command);
    }

    $output = [];
    $return_var = 0;

    if (is_dir($this->path_executable)) {
      chdir($this->path_executable);
      exec($this->the_command, $output, $return_var);
    } else {
      throw new \Exception('Invalid resource directory.', 1);
    }

    if ($return_var != 0) {
      throw new \Exception('Your report has an error and couldn \'t be processed!\ Try to output the command using the function `output();` and run it manually in the console.', 1);
    }

    return $output;
  }
}
