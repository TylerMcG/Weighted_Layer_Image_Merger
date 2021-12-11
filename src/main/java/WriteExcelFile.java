import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;

import java.util.Map;

public class WriteExcelFile {

    public static void writeExcelFile(Map<Integer, Double> oddsMap)  {
        //Create workbook
        Workbook workbook = new XSSFWorkbook();
        //Create spreadsheet
        Sheet sheet = workbook.createSheet("Image Assets");
        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < Constants.columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(Constants.columns[i]);
            cell.setCellStyle(headerCellStyle);
        }
        int rowNum = 1;
        for(int i = 0; i < Constants.NUM_IMG_TO_GEN; i++) {
            Row row = sheet.createRow(rowNum++);
            row.setHeight((short) 1000);
            Object key = oddsMap.keySet().toArray()[i];
            Object value = oddsMap.values().toArray()[i];
            row.createCell(0).setCellValue("img" + key);
            row.createCell(1).setCellValue(String.valueOf(value));
            try {
                InputStream inputStream = new FileInputStream(Constants.outputFiles[i]);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                int pictureId = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                inputStream.close();
                XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
                XSSFClientAnchor anchor = new XSSFClientAnchor();
                anchor.setCol1(2);
                anchor.setRow1(rowNum-1);
                anchor.setCol2(3);
                anchor.setRow2(rowNum);
                XSSFPicture my_picture = drawing.createPicture(anchor, pictureId);
                // Resize all columns to fit the content size

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int j = 0; j < Constants.columns.length; j++) {
            sheet.autoSizeColumn(j);
        }
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("Rarity_SpreadSheet.xlsx");
            workbook.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
