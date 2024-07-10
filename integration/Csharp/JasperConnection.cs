
using Microsoft.Extensions.Configuration;
using SistemaContable2024.Services.Interfaces;
using System.IO;
using System.Reflection.Metadata;

namespace JasperReader.Helpers;
public class JasperConnection
{
  private string inputFile;
  private bool SetValueFile;
  private string nameReport = "Default";
  private string outputFile;
  public string format {get; set;}
  public Dictionary<string, string> parameters {get; set;}
  private Dictionary<string, string> Connection;
  private JasperSettings jasperSettings;
  private readonly IConfiguration _configuration;
  private readonly ICloudinaryService _cloud;
  public JasperConnection
  (
    string inputFile, string outputFile, 
    string format, IConfiguration configuration, ICloudinaryService cloud
  )
  {
    // FORMATO DE REPORTE
    if(format == null)
      this.format = "pdf";
    else
      this.format = format;

    // LECTURA DEL APPSETTINGS.JSON
    _configuration  = configuration;
    _cloud = cloud;

    this.jasperSettings = new JasperSettings();
    _configuration.GetSection("JasperSettings").Bind(this.jasperSettings);

    string dir_proyect = Directory.GetCurrentDirectory();
    // LUGAR DE LOS REPORTES DE ENTRADA Y DE SALIDA
    this.SetValueFile = false;
    if(inputFile != null)
      this.inputFile = dir_proyect + inputFile;
    else
      this.inputFile = dir_proyect + @"/Reports/";

    string nameFile = 
      DateTime.Now.Year.ToString() + 
      DateTime.Now.Month.ToString() + 
      DateTime.Now.Day.ToString() + 
      DateTime.Now.Hour.ToString() + 
      DateTime.Now.Minute.ToString() + 
      DateTime.Now.Second.ToString();
    if(outputFile != null)
      this.outputFile = dir_proyect + outputFile;
    else
      this.outputFile = dir_proyect + @"/wwwroot/reports/" + nameFile  + "_";

    // CONECTAR A LA BD
    this.SetDataBase();
  }

  public string GetOutputFile()
  {
    return this.outputFile+ "."+ this.format;
  }

  private void SetDataBase()
  {
    this.Connection = new Dictionary<string, string>
    {
      { "username", this.jasperSettings.User },
      { "password", this.jasperSettings.Password },
      { "jdbc_url", $"jdbc:sqlserver://{this.jasperSettings.Server};databaseName={this.jasperSettings.Database};encrypt=true;trustServerCertificate=true" },
    };
  }

  public void SetFileReport(string nameReport)
  {
    this.nameReport = nameReport;
    this.outputFile += nameReport;
    this.inputFile += nameReport + ".jrxml";
    this.SetValueFile = true;
  }

  public bool ExecuteReport()
  {
    if(this.SetValueFile)
    {
      var jasperReader = new JasperReader();

      try
      {
        if(this.Connection == null)
          return false;
        
        // Procesar el archivo de entrada
        jasperReader.Process(
          this.inputFile, 
          this.outputFile, 
          this.format, 
          this.parameters, 
          this.Connection.GetValueOrDefault("jdbc_url") ?? "jdbc:sqlserver://localhost;databaseName=master;encrypt=false",
          this.Connection.GetValueOrDefault("username") ?? "sa",
          this.Connection.GetValueOrDefault("password") ?? "12345"
          );
          
        // Console.WriteLine(jasperReader.Output());
        // Ejecutar el proceso
        jasperReader.Execute();
        return true;
      }
      catch (Exception ex)
      {
        Console.WriteLine("Error: " + ex.Message);
        return false;
      }
    }
    else
      return false;
  }

  public string unionDocs()
  {
    return "";
  }
  public string publicCloudinary()
  {
    
    string uploadFile = "";
    
    using (var stream = File.OpenRead(this.GetOutputFile()))
    {
      var file = new FormFile(stream, 0, stream.Length, "defaultName", this.GetOutputFile());
      var route = $"Report/{this.nameReport}";

      uploadFile = _cloud.UploadFile(file, route);
    }

    return uploadFile;
  }
}