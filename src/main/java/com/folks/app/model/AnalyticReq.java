package com.folks.app.model;

import java.sql.Timestamp;

/**
 *
 * @author schan280
 */
public class AnalyticReq {
    
    private String dataset;         // Table name
    private String dimension;       // status, role, etc, or null
    private Metric metric;          // MIN, MAX, COUNT, SUM
    private String measure;         // Measure column name
    
    private Filter[] filters;       // Query filter(s)
    private Bound bound;            // From and To Timestamps
    
    public static enum Metric {
        COUNT,
        COUNT_DISTINCT,
        MIN,
        MAX,
        AVG,
        SUM
    };
    
    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public Filter[] getFilters() {
        return filters;
    }

    public void setFilters(Filter[] filters) {
        this.filters = filters;
    }
    
    public static class Bound {
        
        private String col;
        private Timestamp lb;
        private Timestamp ub;

        public String getCol() {
            return col;
        }

        public void setCol(String col) {
            this.col = col;
        }

        public Timestamp getLb() {
            return lb;
        }

        public void setLb(Timestamp lb) {
            this.lb = lb;
        }

        public Timestamp getUb() {
            return ub;
        }

        public void setUb(Timestamp ub) {
            this.ub = ub;
        }
    }
    
    public static class Filter {
        
        private String col;
        private String op;
        private Object val;

        public String getCol() {
            return col;
        }

        public void setCol(String col) {
            this.col = col;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public Object getVal() {
            return val;
        }

        public void setVal(Object val) {
            this.val = val;
        }
    }
}
