/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.logging.Level;
import java.util.logging.Logger;

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
}
