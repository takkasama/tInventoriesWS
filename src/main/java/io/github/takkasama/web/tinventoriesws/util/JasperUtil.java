package io.github.takkasama.web.tinventoriesws.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;

/**
 *
 * @author takkasama
 */
public class JasperUtil {

    private Map<String, JasperReport> reports;

    public JasperUtil() {
        this.reports = new HashMap<>();
    }

    public void addReport(String key, InputStream jasperFile) {
        try {
            reports.put(key, (JasperReport) JRLoader.loadObject(jasperFile));

        } catch (JRException e) {
            Logger.getLogger(JasperUtil.class.getName()).log(Level.WARNING, "Error to generate the file .jasper : " + e.getMessage(), e);
        }
    }

    public void removeReport(String key) {
        if (reports.containsKey(key)) {
            reports.remove(key);
        }

    }

    //necesario para la lectura de laguno graficos 
    private static void initProperties() {
        DefaultJasperReportsContext.getInstance().setProperty(
                "net.sf.jasperreports.deserialization.class.whitelist.sicobu.charts",
                "net.sf.jasperreports.charts.**"
        );
    }

    public byte[] generateJasperReport(String key, Map<String, Object> parameters, List<Object> dsToTable, Boolean isPDF) {
        if (!reports.containsKey(key)) {
            String keyA = key;

            return null;
        }
        if (parameters == null) {
            parameters = new HashMap<>();
        }

        try {
            JasperPrint report;

            //TIPO GENERAL PARA LOS ELEMETOS DE DATASOURCE SIMILAR A LA CLASE BASE ABSTRACTA DE O PADRE DE UNA QUE PUEDE CONTENER CUALQUIER HIJO
            //DE MODO QUE NOS PERMITE GENERAR UNA COLECION DE DATOS DE DATA SOURCE O UN DATA SOURCE VACIO POR SI NO HAY ELEMENTOS.
            JRDataSource ds = dsToTable != null ? new JRBeanCollectionDataSource(dsToTable)
                    : new JREmptyDataSource();

            report = JasperFillManager.fillReport(reports.get(key), parameters, ds);
            if (isPDF) {
                return JasperExportManager.exportReportToPdf(report);
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                JRSaver.saveObject(report, out);

                return out.toByteArray();
            }
        } catch (JRException e) {
            Logger.getLogger(JasperUtil.class.getName()).log(Level.WARNING, "Error to generate the file .jrprint : " + e.getMessage(), e);
        }

        return null;
    }

    public static byte[] generateJasperReportByList(byte[] jasperFile, Map<String, Object> parameters, List<Object> dsToTable, Boolean isPDF) {
        if (jasperFile == null) {
            return null;
        }

        if (parameters == null) {
            parameters = new HashMap<>();
        }

        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(jasperFile));

            JasperPrint report;

            JRDataSource ds = new JREmptyDataSource(0);

            if (dsToTable != null) {
                ds = new JRBeanArrayDataSource(dsToTable.toArray());
                parameters.put("ds", ds);
            }

            report = JasperFillManager.fillReport(jasperReport, parameters, ds);

            if (isPDF) {
                return JasperExportManager.exportReportToPdf(report);
            } else {
                return toByteFile(report);
            }
        } catch (JRException e) {
            Logger.getLogger(JasperUtil.class.getName()).log(Level.WARNING, "Error to generate the file .jrprint : " + e.getMessage(), e);
        }

        return null;
    }

    /*
        PARA EL USO DE ESTE METODO ES NECESARIO QUE EL ARCHIVO DE JRXML TENGA LA CONFIGURACION DE LAS CONSULTAS POR CADA CAMPO , ADEMAS DE QUE 
        LA CONEXION CON LA BASE DE DATOS , O MEJOR DICHO EL PARAMETRO CONEECTION SE PUEDE CONSEGUIR CON em ENTITYMANAGER CON :
                Connection c = em.unwrap(Connection.class); 
    
        ADEMAS CABE RESALTAR QUE LOS PARAMETROS DEBEN TENER EL NOMBRE EXACTO DE LOS ATRIBUTOS DE LAS BASE DE DATOS ES DEBIR SI SE TIENE QUE EL ID ES : "USER_ID" EL PARAMETRO DEBER TNER EL MISMO NOMBRE
        PARA LOS VALORES DE LOS PARAMETROS SE PUEDEN MANTENER LOS TIPOS BASE , ES DECIR LOS DE LA ENTIDAD NO LOS DEL DTO , YA QUE COMO SE SABE , LAS BASES DE DATOS NO RECONOCEN O NO ADMITEN VALORES BOLEANOS
    
        NOTA (IMPORTANTE) : PARA EL DADO CASO QUE SE QUIERA BUSCAR ATRAVES DEL ID U OTRO ELEMENTO DE LA ENTIDAD SE BUSCARA O FILTRARA ATRAVEZ DE LOS PARAMETROS, ES DECIR SI SE PASA UN ID 1 BUSCARA Y HARA LAS CONSULTAS
                            FILTRANDOLAS POR ESTE ID.
    NOTA : SI NO SE INDICA IDIOMA EL REPORTE SE GENERA EN ESPANNOL POR DEFECTO
                           
     */
    public static byte[] generateJasperReportByDataSource(byte[] jasperFile,
            Map<String, Object> parameters,
            Connection connectionDataBase,
            String langauge,
            Boolean isPDF) {
        try {
            if (jasperFile == null || connectionDataBase == null || parameters == null) {
                return null;
            }
            langauge = langauge.toLowerCase();

            String country;
            switch (langauge) {
                case "es" ->
                    country = "CR";
                case "en" ->
                    country = "US";
                default -> {
                    country = "CR";
                    langauge = "es";
                }
            }

            initProperties();

            parameters.put(JRParameter.REPORT_LOCALE, Locale.of(langauge, country));

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(jasperFile));
            JasperPrint report;
            report = JasperFillManager.fillReport(jasperReport, parameters, connectionDataBase);

            if (isPDF) {
                return JasperExportManager.exportReportToPdf(report);
            } else {
                return toByteFile(report);
            }

        } catch (JRException e) {
            Logger.getLogger(JasperUtil.class.getName()).log(Level.WARNING, "Error to generate the file .jrprint : " + e.getMessage(), e);
        }

        return null;
    }

    private static byte[] toByteFile(JasperPrint report) throws JRException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JRSaver.saveObject(report, out);
        return out.toByteArray();
    }
}
