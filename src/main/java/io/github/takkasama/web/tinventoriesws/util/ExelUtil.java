package io.github.takkasama.web.tinventoriesws.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author takkasama
 */
public class ExelUtil {

    
    private final static short TITLE_SIZE = (short) 18;
    private final static short SUB_TITLE_SIZE = (short) 13;
    private final static short NORMAL_SIZE = (short) 11;

    private final static String FORMAT_SUM = "\"SUMA(%s%d:%s%d)\"";

    private final static String FONT_NAME = "Liberation Sans";
    
    
    
    private static final System.Logger LOG = System.getLogger(ExelUtil.class.getName());

    private Workbook workbook;
    private Map<String, SXSSFSheet> sheets;

    public ExelUtil() {
        this.workbook = new SXSSFWorkbook();
        this.sheets = new HashMap<>();

    }

    public void createHeaderCell(Row headerRow, String value) {
        Cell cell = headerRow.createCell(headerRow.getRowNum());
        cell.setCellValue(value);

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();

        font.setBold(true);
        font.setFontHeightInPoints(TITLE_SIZE);
        font.setFontName(FONT_NAME);
        font.setColor(IndexedColors.WHITE.getIndex());

        style.setFont(font);

        cell.setCellStyle(style);

    }

    public void createSubTitleCell(Row row, String value, int colPos) {
        Cell cell = row.createCell(colPos);
        cell.setCellValue(value.toUpperCase());

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLUE_GREY.getIndex());

        Font font = workbook.createFont();

        font.setBold(true);
        font.setFontHeightInPoints(SUB_TITLE_SIZE);
        font.setFontName(FONT_NAME);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());

        style.setFont(font);

        cell.setCellStyle(style);
    }

    public void createInfoCell(Row row, String valueLetter, Double valueNumeric, int colPos) {
        Cell cell = row.createCell(colPos);

        if (valueLetter != null && !valueLetter.isBlank()) {
            cell.setCellValue(valueLetter);
        } else {
            cell.setCellValue(valueNumeric);
        }

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

        Font font = workbook.createFont();
        font.setFontName(FONT_NAME);
        font.setFontHeightInPoints(NORMAL_SIZE);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());

        style.setFont(font);

        cell.setCellStyle(style);
    }

    public void createDateCell(Row row, LocalDateTime date, int colPos) {
        Cell cell = row.createCell(colPos);
        cell.setCellValue(date);

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLUE_GREY.getIndex());

        Font font = workbook.createFont();
        font.setFontName(FONT_NAME);
        font.setFontHeightInPoints(NORMAL_SIZE);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);

        CreationHelper creationHelper = workbook.getCreationHelper();

        style.setDataFormat(creationHelper.createDataFormat().getFormat("dd / MM / yyyy - HH:mm"));

        cell.setCellStyle(style);

    }

    public void createSumCell(Row row, int rowA, int rowB, int colA, int colB, int colPos) {

        Cell cell = row.createCell(colPos);

        String cA = CellReference.convertNumToColString(colA);
        String cB = CellReference.convertNumToColString(colB);

        String formula = String.format(FORMAT_SUM, cA, rowA, cB, rowB);

        cell.setCellFormula(formula);

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
    private void fillCells(Row row, int fCol, Object object, List<String> atributesName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int currentCol = fCol;
        for (String s : atributesName) {

            String getName = "get" + s.substring(0, 1).toUpperCase() + s.substring(1);
            Method method = object.getClass().getMethod(getName);

            Object objectValue = method.invoke(object);

            if(objectValue == null){
                createInfoCell(row, "The value is empty", null, fCol);
            }
            else if (objectValue instanceof Number v) {
                createInfoCell(row, null, v.doubleValue(), currentCol);
            }
            else if (objectValue instanceof String v) {
                createInfoCell(row, v, null, currentCol);
            }
            else if (objectValue instanceof LocalDateTime v) {
                createDateCell(row, v, currentCol);
            }
            else if (objectValue instanceof LocalDate v) {
                LocalDateTime date = LocalDateTime.of(v, LocalTime.NOON);
                createDateCell(row, date, currentCol);
            }else {
                createInfoCell(row, "The type is not supported", null, currentCol);
            }

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
            if (workbook == null) {
                workbook = new SXSSFWorkbook();
            }

            SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(title);

            mergeCells(sheet, 2, 3, 1, atributesName.size());

            Row headerRow = sheet.createRow(2);
            createHeaderCell(headerRow, title);

            Row subtitlesRow = sheet.createRow(4);
            int currentCol = 1;
            for (String p : atributesName) {
                createSubTitleCell(subtitlesRow, p, currentCol);
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
        }
        return null;
    }

}
