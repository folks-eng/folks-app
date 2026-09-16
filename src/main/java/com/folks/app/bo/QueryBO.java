package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.dao.QueryDAO;
import com.folks.app.model.AnalyticReq;
import com.folks.app.model.AnalyticRes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.javalabs.jpa.DAOProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sudip
 */
public class QueryBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryBO.class);
    
    private final Map<String, String> dataset = Map.ofEntries(
            Map.entry("User", "fks_users"),
            Map.entry("Professional", "fks_professionals"),
            Map.entry("Document", "fks_documents"),
            Map.entry("Address", "fks_addresses"),
            Map.entry("Booking", "fks_bookings"),
            Map.entry("Payment", "fks_payments"),
            Map.entry("Coupon", "fks_coupons"),
            Map.entry("CouponUsage", "fks_coupon_usage"),
            Map.entry("Category", "fks_categories"),
            Map.entry("Service", "fks_services"),
            Map.entry("JonStatus", "fks_job_status"));
    
    private final QueryDAO queryDAO;
    
    public QueryBO() {
        this.queryDAO = DAOProxy.get(QueryDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Query Business Object: {}. QueryDAO: {}", getClass().getSimpleName(), queryDAO);
        }
    }
    
    public AnalyticRes execute(AppUser usr, AnalyticReq analytic) throws IllegalAccessException {
        // Only admin has permission to execute query api.
        ensureAdmin(usr);
        
        StringBuilder buff = new StringBuilder(1024);
        List<Object> params = new ArrayList<>();
        
        buff.append("SELECT");
        
        // Add dimension (status, etc)
        if (analytic.getDimension() != null && analytic.getDimension().trim().length() > 0) {
            buff.append(" ").append(analytic.getDimension()).append(",");
        }
        // Add the metric (COUNT(*), MAX(salary), etc)
        if (analytic.getMetric() == null) {
            throw new IllegalArgumentException("Must provide a valid metric");
        }
        buff.append(" ")
                .append(analytic.getMetric() == AnalyticReq.Metric.COUNT_DISTINCT ? "COUNT" : analytic.getMetric())
                .append("(").append(analytic.getMetric() == AnalyticReq.Metric.COUNT_DISTINCT ? "(DISTINCT" : "")
                .append(analytic.getMeasure() != null ? analytic.getMeasure() : "*")
                .append(")");
        
        if (analytic.getMetric() == AnalyticReq.Metric.MIN
                || analytic.getMetric() == AnalyticReq.Metric.MAX
                || analytic.getMetric() == AnalyticReq.Metric.AVG
                || analytic.getMetric() == AnalyticReq.Metric.SUM
                || analytic.getMetric() == AnalyticReq.Metric.COUNT_DISTINCT) {
            
            if (analytic.getMeasure() == null) {
                throw new IllegalArgumentException("Must provide a measure for " + analytic.getMetric() + " metric");
            }
        }
        
        // Add the table name
        String table = dataset.get(analytic.getDataset());
        if (table == null) {
            throw new IllegalArgumentException("Invalid dataset name. Valid values are: " + dataset.keySet());
        }
        buff.append("\n").append("  FROM ").append(table);
        
        // Add the lower and upper bound.
        if (analytic.getBound() != null) {
            buff.append("\n WHERE").append(" ")
                    .append(analytic.getBound().getCol())
                    .append(" BETWEEN ")
                    .append("?")
                    .append(" AND ")
                    .append("?");
            
            params.add(analytic.getBound().getLb());
            params.add(analytic.getBound().getUb());
        }
        
        // Add the filter(s)
        if (analytic.getFilters() != null) {
            String st = analytic.getBound() != null ? "\n   AND " : "\n WHERE ";
            AnalyticReq.Filter[] filters = analytic.getFilters();
            
            for (int i  = 0; i < filters.length; i ++) {
                AnalyticReq.Filter filter = filters[i];
                buff.append(st)
                        .append(filter.getCol())
                        .append(filter.getOp() != null ? filter.getOp() : " = ")
                        .append("?");
                
                if (i < filters.length - 1) {
                    buff.append("\n   AND ");
                }
                params.add(filter.getVal());
            }
        }
        
        // Add the grouping
        if (analytic.getDimension() != null) {
            buff.append("\n").append(" GROUP BY ")
                    .append(analytic.getDimension());
        }
        
        List<Object> list = queryDAO.execute(buff.toString(), params);
        
        AnalyticRes response = new AnalyticRes();
        List<AnalyticRes.Column> cols = new ArrayList<>();
        
        if (analytic.getDimension() != null && analytic.getDimension().trim().length() > 0) {
            cols.add(new AnalyticRes.Column(analytic.getDimension(), "DIMENSION"));
        }
        cols.add(new AnalyticRes.Column(analytic.getMetric().name(), "METRIC"));
        response.setColumns(cols);
        
        List<Map<String, Object>> rows = new ArrayList<>();
        
        if (list.get(0).getClass().isArray()) {
            for (int i = 0; i < list.size(); i ++) {
                Object[] res = (Object[])list.get(i);
                if (analytic.getDimension() != null) {
                    rows.add(Map.of(analytic.getDimension(), res[0], "count", res[1]));
                }
                else {
                    rows.add(Map.of("count", res[0]));
                }
            }
        }
        else {
            rows.add(Map.of("count", list.get(0)));
        }
        response.setRows(rows);
        
        return response;
    }
}
