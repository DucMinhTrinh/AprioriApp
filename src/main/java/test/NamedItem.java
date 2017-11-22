/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import de.mrapp.apriori.Item;

/**
 *
 * @author MinhPC
 */
public class NamedItem implements Item{
    /**
     * The name of the item.
     */
    private final String name;
    
    /**
     * Creates a new implementation of the type {@link Item}.
     *
     * @param name The name of the item as a {@link String}. The name may neither be null, nor
     *             empty
     */
    public NamedItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    

    @Override
    public int compareTo(Item o) {
        
        return toString().compareTo(o.toString());
    }
    
    
    @Override
    public String toString() {
        return getName();
    }


    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NamedItem other = (NamedItem) obj;
        return name.equals(other.name);
    }

    
}
