package io.github.takkasama.web.tinventoriesws.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

/**
 *
 * @author takkasama
 */
public class ExelUtil {

    private enum TypeCell {
        TITLE_CELL( new XSSFColor(new byte[]{(byte) 72, (byte) 56, (byte) 53})
                , new XSSFColor(new byte[]{(byte) 190, (byte) 226, (byte) 194})
                , null
                , (short) 18, 
                "Liberation Sans"),
        SUBTITLE_CELL(new XSSFColor(new byte[]{(byte) 240, (byte) 238, (byte) 228})
                , new XSSFColor(new byte[]{(byte) 39, (byte) 27, (byte) 16})
                , null, 
                (short) 18, "Liberation Sans"),
        INFO_CELL(new XSSFColor(new byte[]{(byte) 240, (byte) 238, (byte) 228})
                , new XSSFColor(new byte[]{(byte) 39, (byte) 27, (byte) 16})
                , null, 
                (short) 18, "Liberation Sans");

        private XSSFColor foregroundColor;
        private XSSFColor backgroundColor;
        private XSSFColor borderColor;

        private short fontSize;
        private String fontName;

        private TypeCell(XSSFColor textColor, XSSFColor foregroundColor, XSSFColor borderColor, short fontSize, String fontName) {
            this.foregroundColor = textColor;
            this.backgroundColor = foregroundColor;
            this.borderColor = borderColor;

            this.fontSize = fontSize;
            this.fontName = fontName;
        }

        public XSSFColor getForegroundColor() {
            return foregroundColor;
        }

        public void setForegroundColor(XSSFColor foregroundColor) {
            this.foregroundColor = foregroundColor;
        }

        public XSSFColor getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(XSSFColor backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public XSSFColor getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(XSSFColor borderColor) {
            this.borderColor = borderColor;
        }

        public short getFontSize() {
            return fontSize;
        }

        public void setFontSize(short fontSize) {
            this.fontSize = fontSize;
        }

        public String getFontName() {
            return fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public void createCell(Workbook workbook, Row owner, int column, Object value) {
            Cell cell = owner.createCell(column);
            CellStyle style = buildStyle(workbook);

            switch (value) {
                case null -> {
                    cell.setCellValue("- - -");
                    throw new NullPointerException("Null Pointer Exeption : The value to cell is Null");
                }
                case Number n -> {
                    cell.setCellValue(n.doubleValue());
                }
                case String s -> {
                    cell.setCellValue(s.isBlank() ? "- - -" : s);
                }
                case LocalDate d -> {
                    cell.setCellValue(d);
                    style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd - MM - yyyy"));

                }
                case LocalDateTime t -> {
                    cell.setCellValue(t);
                    style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd - MM - yyyy : hh.mm.ss"));
                }
                case Boolean b -> {
                    cell.setCellValue(b);
                }
                default -> {
                    cell.setCellValue(" - - - ");
                    throw new UnsupportedOperationException("Unsupported Type : The value to Cell is not supported");
                }

            }
        }

        private CellStyle buildStyle(Workbook workbook) {
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setFillBackgroundColor(backgroundColor);
            style.setFillForegroundColor(foregroundColor);

            Font font = workbook.createFont();
            font.setFontName(fontName);
            font.setFontHeightInPoints(fontSize);
            font.setColor(foregroundColor.getIndex());

            style.setFont(font);

            return style;
        }
    };
    
    private static final System.Logger LOG = System.getLogger(ExelUtil.class.getName());

    private Workbook workbook;
    private Map<String, SXSSFSheet> sheets;

    public ExelUtil() {
        this.workbook = new SXSSFWorkbook();
        this.sheets = new HashMap<>();

    }
    
    private void reSizeColumns(Sheet sheet, int firstCol, int lastCol) {
        for (int i = firstCol; i <= lastCol; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }
    }

    public byte[] fileToByte() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return out.toByteArray();
    }

    private void mergeCells(Sheet sheet, int fRow, int lRow, int fCol, int lCol) {
        sheet.addMergedRegion(new CellRangeAddress(fRow, lRow, fCol, lCol));
    }

    /**
     *
     *
     * Este metodo tiene el fin de leer los get del objeto que se esta
     * trabajando
     *
     *
     * -> La lectura de los objetos se hace atravez de la lista de
     * atributesName, en la que se indica los atributos a leer que pertenecen a
     * la clase que se trabaja, por ejemplo color (de Gato), lo que buscaremos
     * es un objetos que tenga el formato de getColor , si tiene otro formato,
     * por ejemplo , clor o COlor, por lo que se debe se claro con el nombre.
     *
     * -> para el manejo de los valores que se leeran de los atibutos unicamente
     * se aceptara los tipos : Double - Integer - Float || double - int - float
     * LocalDate o LocalDateTime String
     */
    private void fillCells(Row row, int fCol, Object object, List<String> atributesName) throws NoSuchMethodException, IllegalAccessException, 
            InvocationTargetException , NullPointerException, UnsupportedOperationException{
        int currentCol = fCol;
        for (String s : atributesName) {

            String getName = "get" + s.substring(0, 1).toUpperCase() + s.substring(1);
            Method method = object.getClass().getMethod(getName);

            Object objectValue = method.invoke(object);

            TypeCell.INFO_CELL.createCell(workbook, row, fCol, objectValue);
            
            currentCol++;
        }
    }

    /**
     *
     *
     *
     * -> Este metodo tiene el fin de leet una lista de objetos que se van
     * escirbir en el exel, esto de modo general, es decir si se tiene un objeto
     * x (Gato), este va leer gatos.
     *
     * -> La lectura de los objetos se hace atravez de la lista de
     * atributesName, en la que se indica los atributos a leer que pertenecen a
     * la clase que se trabaja, por ejemplo color (de Gato), lo que buscaremos
     * es un objetos que tenga el formato de getColor , si tiene otro formato,
     * por ejemplo , clor o COlor, por lo que se debe se claro con el nombre.
     *
     * -> para el manejo de los valores que se leeran de los atibutos unicamente
     * se aceptara los tipos : Double - Integer - Float || double - int - float
     * LocalDate o LocalDateTime String
     */
    public byte[] buildExelFile(List<Object> objects, List<String> atributesName, String title) {
        try {
            SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(title);

            mergeCells(sheet, 2, 3, 1, atributesName.size());

            Row headerRow = sheet.createRow(2);
            TypeCell.TITLE_CELL.createCell(workbook, headerRow, 1, title);

            Row subtitlesRow = sheet.createRow(4);
            int currentCol = 1;
            for (String p : atributesName) {
                TypeCell.SUBTITLE_CELL.createCell(workbook, subtitlesRow, currentCol, p);
                currentCol++;
            }

            int currentRow = subtitlesRow.getRowNum() + 1;
            for (Object o : objects) {
                Row row = sheet.createRow(currentRow);
                fillCells(row, 1, o, atributesName);
                currentRow++;
            }

            reSizeColumns(sheet, 1, atributesName.size());

            return fileToByte();
        } catch (NoSuchMethodException ex) {
            LOG.log(System.Logger.Level.ERROR, "Error to find Method of the Objects", ex);
        } catch (IOException ex) {
            LOG.log(System.Logger.Level.ERROR, "Error to write file bytes", ex);
        } catch (IllegalAccessException ex) {
            LOG.log(System.Logger.Level.ERROR, "Error to access in to he methods of the Objects, the method could be private or protected", ex);
        } catch (InvocationTargetException ex) {
            LOG.log(System.Logger.Level.ERROR, "Error to get value of method, could be a null value", ex);
        }  catch (NullPointerException ex) {
            LOG.log(System.Logger.Level.ERROR, (String)null, ex);
        } catch (UnsupportedOperationException ex) {
            LOG.log(System.Logger.Level.ERROR, (String)null, ex);
        } 
        return null;
    }

}
