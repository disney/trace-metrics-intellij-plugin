package com.disney.idea.components;

import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.disney.idea.utils.Trace;

/**
 * Model backing the Trace Metrics Swing UI table for a project.
 */
public class TraceTableModel extends DefaultTableModel {

    static final String[] COLUMN_NAMES = {"Trace Name", "Num Hits", "File Name", "Line Number"};
    private final List<Trace> traces;

    public TraceTableModel(List<Trace> traces) {
        super(COLUMN_NAMES, traces.size());
        this.traces = traces;
        for (int i = 0; i < traces.size(); i++) {
            Trace trace = traces.get(i);
            this.setValueAt(trace.getMetricName(), i, 0);
            this.setValueAt(trace.getFile().getName(), i, 2);
            this.setValueAt(trace.getLineNumber(), i, 3);
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Trace getTrace(int forRow) {
        return traces.get(forRow);
    }

    /**
     * Populate the second column of the data table. If the query failed, the column will be populated with '--'s.
     * @param traceCounts Result from NR query
     */
    public void addTraceCounts(Map<String, Long> traceCounts) {
        boolean queryResultIsEmpty = traceCounts.isEmpty();
        int nRow = this.getRowCount();
        for (int i = 0; i < nRow; i++) {
            String traceCountString;
            if (queryResultIsEmpty) {
                traceCountString = "--";
            } else {
                String traceName = (String) this.getValueAt(i, 0);
                traceCountString = getFormattedTraceCountString(traceCounts, traceName);
            }
            this.setValueAt(traceCountString, i, 1);
        }
    }

    private static String getFormattedTraceCountString(Map<String, Long> traceCounts, String traceName) {
        Long traceCount = traceCounts.get(traceName);
        if (traceCount == null) {
            traceCount = 0L;
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(traceCount);
    }

}
