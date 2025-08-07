package com.rms;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRSaver;

import java.io.File;

public class CompileReport {

    private static final String JRXML_FILE_PATH = "/home/ahosain/JaspersoftWorkspace/MyReports/category_wise_report.jrxml";

    public  void compileSingleReport() {
        try {
            File jrxmlFile = new File(JRXML_FILE_PATH);
            if (!jrxmlFile.exists()) {
                System.err.println("JRXML file not found: " + JRXML_FILE_PATH);
                return;
            }

            // Compile the report from file path
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFile.getAbsolutePath());

            // Save the compiled report as .jasper in the same directory
            String jasperFilePath = JRXML_FILE_PATH.replaceAll("\\.jrxml$", ".jasper");
            JRSaver.saveObject(jasperReport, jasperFilePath);

            System.out.println("Compiled jasper report saved to: " + jasperFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to compile report: " + e.getMessage());
        }
    }




    public static void main(String[] args) {
        new CompileReport().compileSingleReport();
    }
}
