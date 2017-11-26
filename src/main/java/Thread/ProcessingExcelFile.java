/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thread;

import com.monitorjbl.xlsx.StreamingReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author MinhPC
 */
public class ProcessingExcelFile implements ProcessExcel {

    private String filePath;
    private File file;
    private int instanceNumber;
    private JSONArray listColumn;
    private int transactionNumber;
    private JSONArray listTransaction;
    Workbook workbook = null;
    HSSFWorkbook wb_xls = null;

    public ProcessingExcelFile() {
    }

    ;

    public ProcessingExcelFile(String filePath) {
        this.filePath = filePath;
        try {
            file = new File(filePath);
            if (file.getName().contains(".xlsx")) {
                /*Xử lý nếu file là dạng xlsx*/
                InputStream is = null;
                try {
                    is = new FileInputStream(file);
                    workbook = StreamingReader.builder()
                            .rowCacheSize(100) // number of rows to keep in memory (defaults to 10)
                            .bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to 1024)
                            .open(is);            // InputStream or File for XLSX file (required)
                    Sheet mysheet = workbook.getSheetAt(0);
                    instanceNumber = mysheet.getLastRowNum() - 1;
                    JSONArray listColumn = new JSONArray();
                    int index = 0;
                    for (Row r : mysheet) {
                        String str = "";
                        for (Cell c : r) {
                            JSONObject json = new JSONObject();
                            json.put("index", index++);
                            json.put("value", c.getStringCellValue());
                            listColumn.put(json);
                        }
                        break;
                    }

                    this.listColumn = listColumn;
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                }
            } else if (file.getName().contains(".xls")) {
                /*Xử lý file khi là dạng xls*/
                try {
                    POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
                    wb_xls = new HSSFWorkbook(fs);
                    HSSFSheet sheet = wb_xls.getSheetAt(0);
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
                    JSONArray listColumn = new JSONArray();

                    int index = 0;
                    for (int r = 0; r < rows; r++) {
                        row = sheet.getRow(r);
                        if (row != null) {
                            for (int c = 0; c < cols; c++) {
                                cell = row.getCell((short) c);
                                if (cell != null) {
                                    JSONObject json = new JSONObject();
                                    json.put("index", index++);
                                    json.put("value", cell.toString());
                                    listColumn.put(json);
                                }
                            }
                        }
                        break;
                    }
                    this.listColumn = listColumn;

                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public JSONArray listColumn() {
        return listColumn;

    }

    @Override
    public int getTransactionNumber() {
        return transactionNumber;
    }

    @Override
    public int getInstanceNumber() {
        return instanceNumber;
    }

    @Override
    public JSONArray listTransaction(JSONObject ids, JSONObject items) {
        //ids là để xác định ids transaction
        int indexOfIds = ids.getInt("index");
        int indexOfItems = items.getInt("index");

        JSONArray list = new JSONArray();

        if (file.getName().contains(".xlsx")) {
            Sheet mysheet = workbook.getSheetAt(0);
            for (Row r : mysheet) {
                JSONObject json = new JSONObject();
                for (Cell c : r) {
                    if (c.getColumnIndex() == indexOfIds) {
                        json.put("idItem", c.getStringCellValue());
                    }
                    if (c.getColumnIndex() == indexOfItems) {
                        if(c.getStringCellValue()!=null && !c.getStringCellValue().equals(""))
                        json.put("valueItem", c.getStringCellValue().replaceAll(" ", "_"));
                        else json.put("valueItem", "null");
                    }
                }
                list.put(json);
            }
        } else if (file.getName().contains(".xls")) {
            HSSFSheet sheet_xls = wb_xls.getSheetAt(0);
            int rows; // No of rows
            rows = sheet_xls.getPhysicalNumberOfRows();
            HSSFRow row;
            HSSFCell cell;

            int cols = 0; // No of columns
            int tmp = 0;

            // This trick ensures that we get the data properly even if it doesn't start from first few rows
            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet_xls.getRow(i);
                if (row != null) {
                    tmp = sheet_xls.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        cols = tmp;
                    }
                }
            }

            for (int r = 0; r < rows; r++) {
                JSONObject json = new JSONObject();
                row = sheet_xls.getRow(r);
                if (row != null) {
                    for (int c = 0; c < cols; c++) {
                        cell = row.getCell((short) c);
                        if (cell != null) {
                            if (cell.getColumnIndex() == indexOfIds) {
                                json.put("idItem", cell.toString());
                            }
                            if (cell.getColumnIndex() == indexOfItems) {
                                json.put("valueItem", cell.toString().replaceAll(" ", "_"));
                            }
                        }
                    }
                }
                list.put(json);
            }
        }

        System.out.println("============>" + list.length());
        
        JSONArray result = new JSONArray();
        int transactionNumber = 0;
        int current = 0;
        while (current < (list.length() - 2)) {
            JSONObject _new = new JSONObject();
            JSONArray itemList = new JSONArray();
            JSONObject json = list.getJSONObject(current);
            System.out.println(json.toString());
            _new.put("id", json.getString("idItem"));
            if(json.getString("valueItem")!=null&&json.getString("valueItem")!="")
                itemList.put(json.getString("valueItem"));
            else itemList.put(" ");
            int temp = current + 1;
            try {
                while (list.getJSONObject(temp).getString("idItem").equals(json.getString("idItem"))) {
                    itemList.put(list.getJSONObject(temp).getString("valueItem"));
                    temp++;
                }
            } catch (Exception e) {
                return result;
            }
            _new.put("list", itemList);
//            System.out.println(_new);
            result.put(_new);
            current = temp;
            transactionNumber++;
        }
        this.transactionNumber = transactionNumber;
        //items là id của item
        return result;
    }

    public void convertToTxt(int collumId, int collumItem) {

        JSONArray listT = this.listTransaction(this.listColumn.getJSONObject(collumId), this.listColumn.getJSONObject(collumItem));
        this.transactionNumber = listT.length();
        try {
            //Bước 1: Tạo đối tượng luồng và liên kết nguồn dữ liệu
            File f = new File(System.getProperty("user.dir") + "/data.txt");
            FileWriter fw = new FileWriter(f);

            //Bước 2: Ghi dữ liệu
            for (int i = 0; i < listT.length(); i++) {
                JSONObject obj = listT.getJSONObject(i);
                JSONArray itemSet = obj.getJSONArray("list");
                for (int j = 0; j < itemSet.length(); j++) {
//                    System.out.println(itemSet.get(j));
                    fw.write(itemSet.get(j).toString());
                    if (j < itemSet.length() - 1) {
                        fw.write("   ");
                    }
                }
                fw.write("\r\n");
//                fw.write(obj[0]);
            }

            //Bước 3: Đóng luồng
            fw.close();
        } catch (IOException ex) {
            System.out.println("Loi ghi file: " + ex);
        }
    }
    
    public void convertToTxtFColumn(DefaultTableModel model) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        int j = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            boolean val = (boolean) model.getValueAt(i, 1);
            if (val == true) {
                System.out.println(i);
                array.add(i);
                j++;
            }
        }
        System.out.println(array.toString());
        try {
            //Bước 1: Tạo đối tượng luồng và liên kết nguồn dữ liệu
            File f = new File(System.getProperty("user.dir") + "/data.txt");
            FileWriter fw = new FileWriter(f);

            //Bước 2: Ghi dữ liệu
            if (file.getName().contains(".xlsx")) {
                Sheet mysheet = workbook.getSheetAt(0);
                System.out.println("array length :  " + array.size());
                for (Row r : mysheet) {
                    for (int i = 0; i < array.size(); i++) {
                        Cell cell = r.getCell(array.get(i));
                        String value = cell.getStringCellValue();
                        fw.write(value.replaceAll(" ", "_"));
                        fw.write("   ");
                    }
                    fw.write("\r\n");
                }
            } else if (file.getName().contains(".xls")) {
                HSSFSheet sheet_xls = wb_xls.getSheetAt(0);
                int rows; // No of rows
                rows = sheet_xls.getPhysicalNumberOfRows();
                HSSFRow row;
                HSSFCell cell;

                int cols = 0; // No of columns
                int tmp = 0;

                // This trick ensures that we get the data properly even if it doesn't start from first few rows
                for (int i = 0; i < 10 || i < rows; i++) {
                    row = sheet_xls.getRow(i);
                    if (row != null) {
                        tmp = sheet_xls.getRow(i).getPhysicalNumberOfCells();
                        if (tmp > cols) {
                            cols = tmp;
                        }
                    }
                }

                for (int r = 1; r < rows; r++) {
                    row = sheet_xls.getRow(r);
                    if (row != null) {
                        for (int i = 0; i < array.size(); i++) {
                            HSSFCell cel = row.getCell(array.get(i));
                            String value = cel.toString().replaceAll(" ", "_");
                            
                            fw.write(value);
                            fw.write("   ");
                        }
                    }
                    fw.write("\r\n");
                }
            }


            //Bước 3: Đóng luồng
            fw.close();
        } catch (IOException ex) {
            System.out.println("Loi ghi file: " + ex);
        }
    }
    
    public static void main(String[] args) {
//        String filePath = "data/OnlineRetail2.xlsx";
//        ProcessingExcelFile demo = new ProcessingExcelFile(filePath);
//        demo.convertToTxt(0, 1);
//        System.out.println("Number instance : " + demo.getInstanceNumber());
//        System.out.println("Table of columns");
//        JSONArray list = demo.listColumn();
//        for (int i = 0; i < list.length(); i++) {
//            JSONObject obj = list.getJSONObject(i);
////            System.out.println(obj);
//        }
//        JSONArray listT = demo.listTransaction(list.getJSONObject(7), list.getJSONObject(1));
//         for (int i = 0; i < listT.length(); i++) {
//            JSONObject obj = listT.getJSONObject(i);
//            System.out.println(obj);
//        }

    }
}
