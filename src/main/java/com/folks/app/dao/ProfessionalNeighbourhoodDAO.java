package com.folks.app.dao;

import org.javalabs.jpa.annotation.Dao;
import org.javalabs.jpa.annotation.NotSupported;
import com.folks.app.model.ProfessionalNeighbourhood;
import com.folks.app.util.SearchCriteria;
import java.util.List;

/**
 * Data Access Object for ProfessionalNeighbourhood table.
 *
 * <p>
 * The DAO layer uses <code>jpa-lite</code> framework to manage persistence and
 * transaction. Invocation of a method in a DAO class will implicitly
 * start a transaction. If a method is annotated with {@link NotSupported}, then
 * no transaction will be started. But, if a client is still trying to make a database
 * write operation, it will fail. The transaction management is taken over by the
 * underlying jpa framework. If a database operation fails, then the current
 * {@link UserTransaction} will be marked us rollback-only.
 * 
 * @author Sudiptasish Chanda
 */
@Dao
public interface ProfessionalNeighbourhoodDAO {
    
    /**
     * Insert a new record in the designated table.
     * @param record    ProfessionalNeighbourhood entry to be created
     */
    void insert(ProfessionalNeighbourhood record);
    
    /**
     * A bulk insert method to create multiple records at once.
     * @param records 
     */
    void insert(List<ProfessionalNeighbourhood> records);
    
    /**
     * Update the entry in the DB.
     * 
     * Update operation will change the state of the record present in the table
     * with the one provided as an argument.
     * 
     * @param record    ProfessionalNeighbourhood entry to be updated
     */
    void update(ProfessionalNeighbourhood record);
    
    /**
     * Bulk update multiple records.
     * @param records 
     */
    void update(List<ProfessionalNeighbourhood> records);
    
    /**
     * Delete the specific entry from the database.
     * @param record    ProfessionalNeighbourhood entry to be deleted.
     */
    void delete(ProfessionalNeighbourhood record);
    
    /**
     * Retrieve the entry from the database, as identified by this primary key.
     * If no matching record is found in the DB, then this api will return null.
     * 
     * @param   pk    Primary key of the record to be fetched
     * @return  ProfessionalNeighbourhood
     */
    @NotSupported
    ProfessionalNeighbourhood find(ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK pk);
    
    /**
     * Query all the entries from the underlying db.
     * This is an extension of the {@link #query(SearchCriteria)} method, just that this method
     * will return all the attributes associated with the record.
     * 
     * @param search  Query criteria.
     * @return List
     */
    @NotSupported
    List<ProfessionalNeighbourhood> query(SearchCriteria search);
}

