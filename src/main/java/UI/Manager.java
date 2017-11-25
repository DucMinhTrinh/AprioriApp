/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import com.monitorjbl.xlsx.StreamingReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author MinhPC
 */
public class Manager extends Thread {

    public Main main;

    public Manager(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        while (main.changeAttribute) {

            try {
                main.setStatusUI("Converting excel to txt");
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        main.setStatusUI("Ready excute");

    }

    public void setFilePath(String str) {
        main.setFilePathUI(str);
    }
    
    public void convertCsvToExcel(String filePathCsv, String filePathExcelConverted) throws FileNotFoundException, IOException {
        ArrayList arList = null;
        ArrayList al = null;
        String fName = filePathCsv;
        String thisLine;
        int count = 0;
        FileInputStream fis = new FileInputStream(fName);
        DataInputStream myInput = new DataInputStream(fis);
        int i = 0;
        arList = new ArrayList();
        while ((thisLine = myInput.readLine()) != null) {
            al = new ArrayList();
            String strar[] = thisLine.split(",");
            for (int j = 0; j < strar.length; j++) {
                al.add(strar[j]);
            }
            arList.add(al);
            i++;
        }

        try {
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("new sheet");
            for (int k = 0; k < arList.size(); k++) {
                ArrayList ardata = (ArrayList) arList.get(k);
                HSSFRow row = sheet.createRow((short) 0 + k);
                for (int p = 0; p < ardata.size(); p++) {
                    HSSFCell cell = row.createCell((short) p);
                    String data = ardata.get(p).toString();
                    if (data.startsWith("=")) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        data = data.replaceAll("\"", "");
                        data = data.replaceAll("=", "");
                        cell.setCellValue(data);
                    } else if (data.startsWith("\"")) {
                        data = data.replaceAll("\"", "");
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        cell.setCellValue(data);
                    } else {
                        data = data.replaceAll("\"", "");
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellValue(data);
                    }
                    //*/
                    // cell.setCellValue(ardata.get(p).toString());
                }
                System.out.println();
            }
            FileOutputStream fileOut = new FileOutputStream(filePathExcelConverted);
            hwb.write(fileOut);
            fileOut.close();
            System.out.println("Your excel file has been generated");
        } catch (Exception ex) {
            ex.printStackTrace();
        } //main method ends
    }

    public void showCriteriaAndIdFromExcel(String filePath, JComboBox jComboBox1, JComboBox jComboBox2) {

        File file = new File(filePath);

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100) // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(is);
            Sheet mysheet = workbook.getSheetAt(0);

            for (Row r : mysheet) {
                for (Cell c : r) {
                    jComboBox1.addItem(c.getStringCellValue());
                    jComboBox2.addItem(c.getStringCellValue());
                }
                break;
            }
        } catch (OLE2NotOfficeXmlFileException exception) {
            try {
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sheet = wb.getSheetAt(0);
                HSSFRow row;
                HSSFCell cell;

                int rows; // No of rows
                rows = sheet.getPhysicalNumberOfRows();

                int cols = 0; // No of columns
                int tmp = 0;

                // This trick ensures that we get the data properly even if it doesn't start from first few rows
                for (int i = 0; i < 10 || i < rows; i++) {
                    row = sheet.getRow(i);
                    if (row != null) {
                        tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                        if (tmp > cols) {
                            cols = tmp;
                        }
                    }
                }

                int r = 0;
                row = sheet.getRow(r);
                if (row != null) {
                    for (int c = 0; c < cols; c++) {
                        cell = row.getCell((short) c);
                        if (cell != null) {
                            jComboBox1.addItem(cell.toString());
                            jComboBox2.addItem(cell.toString());
                        }
                    }
                }

            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showCriteria(String filePath, DefaultTableModel model) {
        File file = new File(filePath);

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100) // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(is);
            Sheet mysheet = workbook.getSheetAt(0);

            for (Row r : mysheet) {
                for (Cell c : r) {
                    model.addRow(new Object[]{
                        c.getStringCellValue(),
                        false
                    });
                }
                break;
            }
        } catch (OLE2NotOfficeXmlFileException exception) {
            try {
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sheet = wb.getSheetAt(0);
                HSSFRow row;
                HSSFCell cell;

                int rows; // No of rows
                rows = sheet.getPhysicalNumberOfRows();

                int cols = 0; // No of columns
                int tmp = 0;

                // This trick ensures that we get the data properly even if it doesn't start from first few rows
                for (int i = 0; i < 10 || i < rows; i++) {
                    row = sheet.getRow(i);
                    if (row != null) {
                        tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                        if (tmp > cols) {
                            cols = tmp;
                        }
                    }
                }

                int r = 0;
                row = sheet.getRow(r);
                if (row != null) {
                    for (int c = 0; c < cols; c++) {
                        cell = row.getCell((short) c);
                        if (cell != null) {
                            model.addRow(new Object[]{
                                cell.getStringCellValue(),
                                false
                            });
                        }
                    }
                }

            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
