package com.jasper;

import java.util.HashMap;
// import java.util.Map;
// import java.io.File;
// import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleTextReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class App 
{
    private static String inputFile = "";
    private static String connectString = "";
    private static String outputFile = "";
    private static String formatFile = "";
    private static String user = "";
    private static String pass = "";
    private static String[] params;
    
    public static void main( String[] args )
    {
        
        Options options = new Options();
        options.addRequiredOption("i", "input", true, "input file .jrxml");
        options.addRequiredOption("o", "output", true, "output file");
        options.addRequiredOption("f", "format", true, "format");
        options.addOption("c", "connect", true, "string connect db");
        options.addOption("usr", true, "User");
        options.addOption("pass", true, "Password");
        options.addOption("p", "param", true, "Parameters for inform (clave y valor separados por ==>)");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        Connection connection = null;
        try {
            CommandLine cmd = parser.parse(options, args);
            SetOptionsCmd(cmd);

            if(connectString != null || user != null || pass != null)
            {
                try {
                    connection = getConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            processJrxml(connection);


        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("JasperStarter", options);
            System.exit(1);
        }

    }

    private static void SetOptionsCmd(CommandLine cmd)
    {

        inputFile = cmd.getOptionValue("i");
        outputFile = cmd.getOptionValue("o");
        formatFile = cmd.getOptionValue("f");

        // options to connect to the database
        connectString = cmd.getOptionValue("c");
        user = cmd.getOptionValue("usr");
        pass = cmd.getOptionValue("pass");

        // Process 'p' (param) option with key-value pairs
        params = cmd.getOptionValues("p");
    }


    private static void processJrxml(Connection connection)
    {
        try
        {
            connection = getConnection();
            System.out.println("Loading the. JRXML file .....");
            JasperDesign jasperDesign = JRXmlLoader.load(inputFile);

            System.out.println("Compiling the .JRXML file to .JASPER file ....");
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            HashMap<String, Object> hm  = new HashMap<String, Object>();
            
            if (params != null) {
                for (String param : params) {
                    String[] keyValue = param.split("==>");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        hm.put(key, value);
                    }
                }
            }

            System.out.println("Filling parameteres to .JASPER file...");
            JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(jasperReport, hm, connection);
            
            System.out.println("Exporting the JASPER file to "+ formatFile +" file....");

            switch (formatFile) {
                case "pdf":
                    JasperExportManager.exportReportToPdfFile(jprint, outputFile);
                    break;
                
                case "html":
                    JasperExportManager.exportReportToHtmlFile(jprint, outputFile);
                    break;
                    
                case "pptx":
                    JRPptxExporter exp_pptx = new JRPptxExporter();
                    exp_pptx.setExporterInput(new SimpleExporterInput(jprint));
                    exp_pptx.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
                    exp_pptx.exportReport();
                    break;
                
                case "xlsx":
                    JRXlsxExporter exp_xlsx = new JRXlsxExporter();
                    exp_xlsx.setExporterInput(new SimpleExporterInput(jprint));
                    exp_xlsx.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
                    exp_xlsx.exportReport();
                    break;
                
                case "odt":
                    JROdtExporter exp_odt = new JROdtExporter();
                    exp_odt.setExporterInput(new SimpleExporterInput(jprint));
                    exp_odt.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
                    exp_odt.exportReport();
                    break;

                case "ods":
                    JROdsExporter exp_ods = new JROdsExporter();
                    exp_ods.setExporterInput(new SimpleExporterInput(jprint));
                    exp_ods.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
                    exp_ods.exportReport();
                    break;
                    
                case "docx":
                    JRDocxExporter exp_docx = new JRDocxExporter();
                    exp_docx.setExporterInput(new SimpleExporterInput(jprint));
                    exp_docx.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
                    exp_docx.exportReport();
                    break;

                case "txt":
                    JRTextExporter exp_txt = new JRTextExporter();
                    
                    // Configuración de las propiedades del exportador
                    SimpleTextReportConfiguration reportConfig = new SimpleTextReportConfiguration();
                    reportConfig.setCharWidth(7f);
                    reportConfig.setCharHeight(11f);
                    reportConfig.setPageWidthInChars(0);
                    reportConfig.setPageHeightInChars(0);

                    exp_txt.setConfiguration(reportConfig);
                    exp_txt.setExporterInput(new SimpleExporterInput(jprint));
                    exp_txt.setExporterOutput(new SimpleWriterExporterOutput(outputFile));
                    exp_txt.exportReport();
                    break;


                case "rtf":
                    JRRtfExporter exp_rtf = new JRRtfExporter();
                    exp_rtf.setExporterInput(new SimpleExporterInput(jprint));
                    exp_rtf.setExporterOutput(new SimpleWriterExporterOutput(outputFile));
                    exp_rtf.exportReport();
                    break;

                case "xml":
                    JRXmlExporter exp_xml = new JRXmlExporter();
                    exp_xml.setExporterInput(new SimpleExporterInput(jprint));
                    exp_xml.setExporterOutput(new SimpleXmlExporterOutput(outputFile));
                    exp_xml.exportReport();
                    break;


                case "csv":
                    JRCsvExporter exp_csv = new JRCsvExporter();
                    exp_csv.setExporterInput(new SimpleExporterInput(jprint));
                    exp_csv.setExporterOutput(new SimpleWriterExporterOutput(outputFile));
                    exp_csv.exportReport();
                    break;
            
                default:
                    JasperExportManager.exportReportToPdfFile(jprint, outputFile);
                    break;
            }


            System.out.println("Successfully completed the export");
        }catch(Exception e)
        {
            System.out.println("Exception" + e.getMessage());
        }
    }

    
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectString, user, pass);
    }
}
