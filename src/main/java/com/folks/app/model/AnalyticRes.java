package com.folks.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class AnalyticRes {
    
    private List<Column> columns;
    private List<Map<String, Object>> rows = new ArrayList<>();
    
    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }
    
    public void addRow(Map<String, Object> row) {
        rows.add(row);
    }
    
    public static class Column {
        
        private String name;
        private String type;
        
        public Column() {}

        public Column(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
