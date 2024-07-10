
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;

namespace JasperReader.Helpers
{
  public class JasperReader
  {
    protected string executable = "java"; 
    protected string jar = "jasperreader.jar"; 
    protected string pathExecutable;
    protected string theCommand = "";
    protected bool windows = false;

    protected List<string> formats = new List<string> { "pdf", "rtf", "xlsx", "docx", "odt", "ods", "pptx", "csv", "html", "xml" };

    public JasperReader()
    {
      if (System.Environment.OSVersion.Platform == System.PlatformID.Win32NT)
      {
        windows = true;
        pathExecutable = Directory.GetCurrentDirectory() + @"/bin/"; //Path to executable
      }
      else
      {
        pathExecutable = "/usr/local/bin/";
      }
    }

    public JasperReader Process(string inputFile, string outputFile = "", string format = "pdf", Dictionary<string, string>? parameters = null, string dbConnection = "", string usr = "", string pass = "")
    {
      if (string.IsNullOrEmpty(inputFile))
      {
        throw new Exception("No input file");
      }

      if (!formats.Contains(format))
      {
        throw new Exception("Invalid format!");
      }

      StringBuilder command = new StringBuilder();
      command.Append("-jar ");
      command.Append($"\"{Path.Combine(pathExecutable, jar)}\"");
      command.Append(" -i ");
      command.Append($"\"{inputFile}\"");

      if (!string.IsNullOrEmpty(outputFile))
      {
        outputFile += $".{format}";
        command.Append(" -o ");
        command.Append($"\"{outputFile}\"");
      }

      command.Append(" -f ");
      command.Append($"\"{format}\"");

      if (!string.IsNullOrEmpty(dbConnection))
      {
        command.Append(" -c ");
        command.Append($"\"{dbConnection}\"");
      }

      if (parameters != null && parameters.Count > 0)
      {
        foreach (var parameter in parameters)
        {
          command.Append(" -p ");
          command.Append($"\"{parameter.Key}==>{parameter.Value}\"");
        }
      }

      if (!string.IsNullOrEmpty(usr))
      {
        command.Append(" -usr ");
        command.Append($"\"{usr}\"");
      }

      if (!string.IsNullOrEmpty(pass))
      {
        command.Append(" -pass ");
        command.Append($"\"{pass}\"");
      }

      theCommand = command.ToString();

      return this;
    }

    public string Output()
    {
      return this.theCommand;
    }

    public string Execute()
    {
      if (Directory.Exists(pathExecutable))
      {
        Console.WriteLine($"{windows} {theCommand}");

        var process = System.Diagnostics.Process.Start(new ProcessStartInfo
        {
          FileName = executable,
          Arguments = theCommand,
          RedirectStandardOutput = true,
          UseShellExecute = false,
          CreateNoWindow = true,
        });

        if (process != null) return process.StandardOutput.ReadToEnd();
        else throw new Exception("Failed to start the process.");
      }
      else
      {
        throw new Exception("Invalid resource directory.");
      }
    }
  }
}