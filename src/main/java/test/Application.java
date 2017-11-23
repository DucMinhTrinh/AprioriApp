/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.mrapp.apriori.Apriori;
import de.mrapp.apriori.AssociationRule;
import de.mrapp.apriori.Filter;
import de.mrapp.apriori.FrequentItemSets;
import de.mrapp.apriori.ItemSet;
import de.mrapp.apriori.Output;
import de.mrapp.apriori.RuleSet;
import de.mrapp.apriori.Transaction;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author MinhPC
 */
public class Application {

    public static void main(String[] args) {
        String filePath = "data.txt";
        File inputFile = new File(filePath);
        double minSupport = 0.125;
        double minConfidence = 1;
        Apriori<NamedItem> apriori =  new Apriori.Builder<NamedItem>(minSupport).generateRules(minConfidence).create();
        Iterator<Transaction<NamedItem>> iterator = new DataIterator(inputFile);
        
        Output<NamedItem> output = apriori.execute(iterator);
        
        
//        Filter<ItemSet> filter = Filter.forItemSets().bySize(2);
        FrequentItemSets<NamedItem> frequentItemSets = output.getFrequentItemSets();
//        frequentItemSets = frequentItemSets.filter(filter);
        long timeExcute = output.getRuntime();
        System.out.println("Time Running:"+timeExcute + " ms.") ;
        System.out.println("Frequent Items Number is : "+frequentItemSets.size());
        
        
        //In ra các dánh sách phổ biên
        Iterator<ItemSet<NamedItem>> it = frequentItemSets.iterator();
        
        while (it.hasNext()) {
            ItemSet<NamedItem> next = it.next();
            System.out.println(next.toString()+" - " +next.getSupport());
        }
        
        //Luật liên kết
        RuleSet<NamedItem> ruleSet = output.getRuleSet();
        Iterator<AssociationRule<NamedItem>> itRule = ruleSet.iterator();
         HashMap<String, String> hmap = new HashMap<String, String>(); // hashmap lưu luật liên kết
        while(itRule.hasNext()){
            AssociationRule<NamedItem> a =itRule.next();
            System.out.println(a.getBody().toString()+"====>"+a.getHead().toString());
            hmap.put(a.getBody().toString(),a.getHead().toString());
        }

        // tìm kiếm
        String keyword = "22752, 84029E, 84029G, 85123A";
        System.out.println("Search result: ");
        for (Entry<String, String> e : hmap.entrySet()) {
            if (e.getKey().toLowerCase().contains(keyword.toLowerCase())) {
                //add to my result list
                System.out.println(e.getValue());
            }
        }

    }

    public File getInputFile(String fileName) {

        try {
            URL url = getClass().getClassLoader().getResource(fileName);

            if (url != null) {
                File file = Paths.get(url.toURI()).toFile();
                return file;
            }
            return null;
        } catch (URISyntaxException e) {
            System.out.println(e.getCause());
            return null;
        }
    }
}
