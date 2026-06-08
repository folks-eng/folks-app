package io.opns.app.dao;

import org.javalabs.jpa.annotation.Dao;
import org.javalabs.jpa.annotation.NotSupported;
import io.opns.app.model.Booking;
import io.opns.app.util.SearchCriteria;
import java.util.List;

/**
 * Data Access Object for Booking table.
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
public interface BookingDAO {
    
    /**
     * Insert a new record in the designated table.
     * @param record    Booking entry to be created
     */
    void insert(Booking record);
    
    /**
     * A bulk insert method to create multiple records at once.
     * @param records 
     */
    void insert(List<Booking> records);
    
    /**
     * Update the entry in the DB.
     * 
     * Update operation will change the state of the record present in the table
     * with the one provided as an argument.
     * 
     * @param record    Booking entry to be updated
     */
    void update(Booking record);
    
    /**
     * Bulk update multiple records.
     * @param records 
     */
    void update(List<Booking> records);
    
    /**
     * Delete the specific entry from the database.
     * @param record    Booking entry to be deleted.
     */
    void delete(Booking record);
    
    /**
     * Retrieve the entry from the database, as identified by this primary key.
     * If no matching record is found in the DB, then this api will return null.
     * 
     * @param   pk    Primary key of the record to be fetched
     * @return  Booking
     */
    @NotSupported
    Booking find(Booking.BookingPK pk);
    
    /**
     * Query all the entries from the underlying db.
     * This is an extension of the {@link #query(SearchCriteria)} method, just that this method
     * will return all the attributes associated with the record.
     * 
     * @param search  Query criteria.
     * @return List
     */
    @NotSupported
    List<Booking> query(SearchCriteria search);
}

