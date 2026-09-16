package com.folks.app.dao;

import java.util.List;
import org.javalabs.jpa.annotation.Dao;

/**
 *
 * @author sudip
 */
@Dao
public interface QueryDAO {
    
    List<Object> execute(String sql, List<Object> params);
}
