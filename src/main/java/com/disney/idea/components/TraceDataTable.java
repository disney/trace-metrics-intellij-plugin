package com.disney.idea.components;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.disney.idea.actions.RefreshAction;
import com.disney.idea.utils.Trace;
import com.disney.idea.utils.TraceLoader;
import com.disney.idea.utils.Utils;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;

import static com.disney.idea.utils.Utils.openWebpage;

/**
 * Per-project UI component containing and presenting New Relic hit count metrics
 * related to this project, deriving the list of named metrics from the New Relic
 * trace annotations in the project source code.
 *
 * Referenced by fully qualified classname in plugin.xml which defines the plugin classes.
 */
public class TraceDataTable {
    private JTable curTable;

    public TraceDataTable(Project project){
        curTable = createTable(project);
    }

    public static TraceDataTable getInstance(Project project){
        return project.getComponent(TraceDataTable.class);
    }

    public JTable getTable(){
        return curTable;
    }

    private JTable createTable(Project project) {
        curTable = new JTable();
        curTable.setFillsViewportHeight(true);

        // Set Selection Mode
        curTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create the pop up menu
        JPopupMenu menu = createMenu(curTable);

        curTable.setComponentPopupMenu(menu);
        handleTableEvents(curTable, project);

        // call new relic and create the curTable
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Trace Metrics") {
            public void run(ProgressIndicator indicator) {
                indicator.setText("Running New Relic query");

                Map<String, Long> traceCounts = RefreshAction.callNewRelic(project);

                indicator.setText("Analyzing code for trace annotations");
                indicator.pushState();

                // parse the trace annotations and create the curTable
                refreshTracesAsync(traceCounts, new TraceLoader(project), curTable, project);
            }
        });
        return curTable;
    }

    private JPopupMenu createMenu(JTable table){
        // Create the pop up menu
        JPopupMenu menu = new JPopupMenu();

        JMenuItem openBrowser = new JMenuItem("Open in Browser");

        // Add Action Listener to the menu Item
        openBrowser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentRow = table.getSelectedRow();
                try{
                    int modelRow = table.convertRowIndexToModel(currentRow);
                    String searchTerm = table.getModel().getValueAt(modelRow, 0).toString();
                    openWebpage(Utils.getNewRelicUrl(searchTerm));
                }catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });

        menu.add(openBrowser);

        return menu;
    }

    private void handleTableEvents(JTable table, Project project) {
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // only perform action if double-clicked
                if (e.getClickCount() == 2) {
                    TraceTableModel model = (TraceTableModel) table.getModel();
                    // open file in editor
                    int selectedRow = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(selectedRow);
                    Trace trace = model.getTrace(modelRow);
                    FileEditorManager.getInstance(project).openFile(trace.getFile().getVirtualFile(), true);

                    // put the cursor at the beginning of the line
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    CaretModel caretModel = editor.getCaretModel();
                    String lineNumberString = table.getModel().getValueAt(modelRow, 3).toString();
                    caretModel.moveToLogicalPosition(new LogicalPosition(Integer.valueOf(lineNumberString) - 1,4));

                    // scroll to the line
                    ScrollingModel scrollingModel = editor.getScrollingModel();
                    scrollingModel.scrollToCaret(ScrollType.CENTER);
                }

                // If right click
                else if(SwingUtilities.isRightMouseButton(e) || e.isControlDown()){
                    // Highlight the right-clicked row
                    int row = table.rowAtPoint(e.getPoint() );
                    table.setRowSelectionInterval(row, row);
                }
            }
        });
    }

    /**
     * @param str The formatted number of hits from New Relic
     */
    private Long stringToLong(String str){
        DecimalFormat df = new DecimalFormat("#,###");
        Long num1 = 0L;
        try {
            num1 = df.parse(str).longValue();
        } catch (ParseException e) {
            // Throw away if we cannot parse string
        }
        return num1;
    }

    /**
     * Merges the trace counts from a New Relic metrics query with the list of named
     * trace points from the project source code to produce a new UI table model for view.
     * @param traceCounts Map of metric name to metric count from a New Relic query
     * @param traceLoader Provides a list of New Relic trace annotations from the project source
     * @param table       The UI table element to be updated with a new table model
     * @param project     a handle to the current project used for UI timing orchestration
     */
    private void refreshTracesAsync(Map<String, Long> traceCounts, TraceLoader traceLoader, JTable table, Project project) {
        DumbService.getInstance(project).runWhenSmart(new Runnable() {
            public void run() {
                ArrayList<Trace> theTraces = traceLoader.load();

                // Create and set up the table
                TraceTableModel model = new TraceTableModel(theTraces);
                model.addTraceCounts(traceCounts);
                table.setModel(model);

                // Set up table Sorting
                TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
                List<RowSorter.SortKey> sortKeys = new ArrayList<>();
                sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                sorter.setComparator(1, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2){
                        if(!s1.equals("--") && !s2.equals("--")){
                            return Long.compare(stringToLong(s1), stringToLong(s2));
                        }
                        if(s1.equals("--") && !s2.equals("--")){
                            return -1;
                        }
                        if(!s1.equals("--") && s2.equals("--")){
                            return 1;
                        }
                        return 0;
                    }

                });
                sorter.setSortKeys(sortKeys);
                table.setRowSorter(sorter);

                TableColumnModel tcm = table.getColumnModel();
                tcm.removeColumn(tcm.getColumn(3));
                tcm.removeColumn(tcm.getColumn(2));
            }
        });
    }
}
