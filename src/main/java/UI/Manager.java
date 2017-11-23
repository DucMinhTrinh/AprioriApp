/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

/**
 *
 * @author MinhPC
 */
public class Manager extends Thread{
    public Main main;

    public Manager(Main main) {
        this.main = main;
    }
    
    public void setFilePath(String str){
        main.setFilePathUI(str);
    }
}
