package com.rms;
/*
developer: Arif Hosain
mail: arif@innoweb.co
creation date: 05-03-18 02.40
*/

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRSaver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompileReport {
    private List<String> jrxml = new ArrayList();

    public CompileReport() {
        jrxml.add("BILL_HISTORY");
    }

    public void compile() {
        try {
            for (String aJrxml : jrxml) {
                System.out.println("Start compiling ..................." + aJrxml + ".jrxml");
                InputStream employeeReportStream = this.getClass().getResourceAsStream("/jrxml/" + aJrxml + ".jrxml");
                JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
                JRSaver.saveObject(jasperReport, aJrxml + ".jasper");
                System.out.println("Compilation successful !" + aJrxml + ".jrxml");
            }
            System.out.println("Done report compilation");

        } catch (Exception e) {
            System.out.println("Report failed to compile");
            //e.printStackTrace();
        }
    }
}
