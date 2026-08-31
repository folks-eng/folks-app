package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Document;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class DocumentDAOImpl extends AbstractDAO implements DocumentDAO {
    
    private final String TABLE = "fks_documents";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Document record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Document> records) {
        for (Document record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Document record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Document> records) {
        for (Document record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Document record) {
        em.remove(record);
    }

    @Override
    public Document find(Document.DocumentPK pk) {
        return em.find(Document.class, pk);
    }

    @Override
    public List<Document> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Document.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Document> result = q.getResultList();
        return result;
    }
    
}
