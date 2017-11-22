/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thread;

import com.monitorjbl.xlsx.StreamingReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
        Sheet mysheet = workbook.getSheetAt(0);

        JSONArray list = new JSONArray();
        for (Row r : mysheet) {
            JSONObject json = new JSONObject();
            for (Cell c : r) {
                if (c.getColumnIndex() == indexOfIds) {
                    json.put("idItem", c.getStringCellValue());
                }
                if (c.getColumnIndex() == indexOfItems) {
                    json.put("valueItem", c.getStringCellValue());
                }
            }
            list.put(json);
        }

        System.out.println("============>" + list.length());

        JSONArray result = new JSONArray();
        int transactionNumber = 0;
        int current = 0;
        while (current < (list.length() - 2)) {
            JSONObject _new = new JSONObject();
            JSONArray itemList = new JSONArray();
            JSONObject json = list.getJSONObject(current);
            _new.put("id", json.getString("idItem"));
            itemList.put(json.getString("valueItem"));
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
            System.out.println(_new);
            result.put(_new);
            current = temp;
            transactionNumber++;
        }
        this.transactionNumber = transactionNumber;
        //items là id của item
        return result;
    }

    public static void main(String[] args) {
        String filePath = "data/OnlineRetail.xlsx";
        ProcessingExcelFile demo = new ProcessingExcelFile(filePath);
        System.out.println("Number instance : " + demo.getInstanceNumber());
        System.out.println("Table of columns");
        JSONArray list = demo.listColumn();
        for (int i = 0; i < list.length(); i++) {
            JSONObject obj = list.getJSONObject(i);
            System.out.println(obj);
        }
        demo.listTransaction(list.getJSONObject(0), list.getJSONObject(1));

    }
}
