package com.ifclass.ifclass.usuario.excel;

import java.util.ArrayList;
import java.util.List;

public class BatchExcelResult {
    private int createdCount;
    private List<RowError> errors = new ArrayList<>();

    public static class RowError {
        private int row;
        private String message;
        public RowError() {}
        public RowError(int row, String message) { this.row = row; this.message = message; }
        public int getRow() { return row; }
        public void setRow(int row) { this.row = row; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public int getCreatedCount() { return createdCount; }
    public void setCreatedCount(int createdCount) { this.createdCount = createdCount; }
    public List<RowError> getErrors() { return errors; }
    public void setErrors(List<RowError> errors) { this.errors = errors; }
    public void addError(int row, String message) { this.errors.add(new RowError(row, message)); }
}