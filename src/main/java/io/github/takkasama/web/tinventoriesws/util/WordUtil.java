/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.takkasama.web.tinventoriesws.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 *
 * @author takkasama
 */
public class WordUtil {

    private static final System.Logger LOG = System.getLogger(WordUtil.class.getName());

    private static XWPFDocument readFIle(byte[] fileBytes) throws IOException {
        if (fileBytes == null || fileBytes.length == 0) {
            return null;
        }

        InputStream inputStream = new ByteArrayInputStream(fileBytes);
        if (inputStream == null) {
            return null;
        }

        return new XWPFDocument(inputStream);

    }

    private static byte[] documentToByte(XWPFDocument document) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);
        out.close();
        document.close();

        return out.toByteArray();
    }

    /**
     * NOTA: ESTE METODO TIENE LE OBJETIVO DE LEER LOS PARRAFOS DE UN WORD Y
     * PASARLOS A UNA CADENA DE TEXTO MANIPULABLE, SIN EMBARGO NO MODIFICA LOS
     * PARRAFO DEL DOCUMENTO
     */
    public String getDocumentAsString(byte[] fileBytes) {
        try {
            XWPFDocument document = readFIle(fileBytes);

            if (document == null) {
                return null;
            }

            String stringFile = "";

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                stringFile += paragraph.getText() + " \n ";
            }

            document.close();
            
            return stringFile;
        } catch (IOException ex) {
            LOG.log(System.Logger.Level.ERROR, "Error : Read file bytes", ex);
            return null;
        }
    }

    /**
     * NOTA: ESTE METODO TIENE LE OBJETIVO DE SUSTITUIR LOS COMODINES QUE SE LE
     * INDIQUEN, O LA COINCIDENCIAS INDICADAS EN LAS LISTA DE COMODINES
     *
     * NOTA: SE MANEJA BYTE EN LUGAR INPUTSTREAM DEBIDO A LA FACILIDAD DE
     * MANIPULACION SIN LA DEPENDENCIA UNA UBICACION FISICA.
     *
     * NOTA: LOS COMODINES NO TIENEN FORMATO ESPECIFICO, SOLO BUSCARA AQUELLOS
     * QUE CONTENGAN LO QUE INDICA EL COMODIN
     *
     * NOTA: SE MANEJARA UN MAP PARA LOS COMODINES DONDE LOS COMODINES SERAN LA
     * LLAVE Y EL VALOR DE EL VALOR A REMPLAZAR PARA EL DOC
     */
    public static byte[] replaceWildcardsInFile(byte[] fileBytes, Map<String, String> wildCards) {
        try {
            if (wildCards == null || wildCards.isEmpty()) {
                return null;
            }

            XWPFDocument document = readFIle(fileBytes);

            if (document == null) {
                return null;
            }

            List<String> keys = new ArrayList<>(wildCards.keySet());

            for (String k : keys) {
                String value = wildCards.get(k);
                findAndReplace(document, k, value == null ? "NOT_SPECIFIED" : value);
            }

            return documentToByte(document);
        } catch (IOException ex) {
            LOG.log(System.Logger.Level.ERROR, "Error : Read file bytes", ex);
        }
        return null;
    }

    private static void findAndReplace(XWPFDocument document, String wildCard, String value) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {

            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null && text.contains(wildCard)) {
                    String replace = text.replace(wildCard, value);
                    run.setText(replace, 0);
                }
            }

        }
    }
}
