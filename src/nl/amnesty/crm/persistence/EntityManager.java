/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.persistence;

import nl.amnesty.crm.entity.Role;

/**
 *
 * @author evelzen
 */
public interface EntityManager {

    public <T> T persist(T t);

    public <T> T find(long id);

    public <T> T find(String name);

    public <T> boolean merge(T t);

    public <T> boolean remove(long id);

    public <T> T match(T t);
    
}
