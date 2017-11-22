/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Thread;

import java.io.File;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author MinhPC
 */
public interface ProcessExcel {
    /*
    * JSONArray :danh sách cột của file excel
    */
    public JSONArray listColumn();
    
    /*Số lượng giao dịch*/
    public int getTransactionNumber();
    
    /*Số lượng ItemSet*/
    public int getInstanceNumber();
    
    /*Danh sách transaction*/
    public JSONArray listTransaction(JSONObject ids,JSONObject items);
}
