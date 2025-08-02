package com.rms.setting;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRSaver;

import java.io.FileInputStream;
import java.io.InputStream;

public class JasperCompiler {



    public static void main(String[] args) {
        try {
            // Option 1: Using FileInputStream with absolute path
            InputStream employeeReportStream = new FileInputStream("/home/ahosain/Documents/personal/RMS/BILL_HISTORY.jrxml");

            // Option 2: Using FileInputStream with relative path (from project root)
            // InputStream employeeReportStream = new FileInputStream("reports/BILL_HISTORY.jrxml");

            // Option 3: Using current working directory
            // InputStream employeeReportStream = new FileInputStream("./BILL_HISTORY.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
            JRSaver.saveObject(jasperReport, "BILL_HISTORY.jasper");

            // Close the stream
            employeeReportStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}