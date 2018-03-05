/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz48.db.orm;

/**
 *
 * @author vasya
 * @param <EntityType>
 */
public interface EntityAccess<EntityType> {
    void getEntity(EntityType entity);
}
