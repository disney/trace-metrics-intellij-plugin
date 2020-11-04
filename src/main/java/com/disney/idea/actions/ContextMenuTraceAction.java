package com.disney.idea.actions;

import javax.swing.*;
import java.awt.*;
import com.disney.idea.components.TraceTableModel;
import com.disney.idea.utils.Trace;
import com.disney.idea.utils.Utils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtil;

/**
 * Menu action to display the Trace Metrics table for this project.
 *
 * Referenced by fully qualified classname in plugin.xml which defines the plugin classes.
 */
public class ContextMenuTraceAction extends AnAction {

    /**
     * Plugin framework hook, determines when to add the the item to the context menu
     * @param e the IntelliJ AnActionEvent instance containing UI context
     */
    @Override
    public void update(AnActionEvent e) {
        PsiElement element = PsiUtil.getElementAtOffset(
                e.getData(CommonDataKeys.PSI_FILE),
                e.getData(CommonDataKeys.CARET).getOffset());
        boolean isTraceElement = Utils.isPartOfTraceElement(element);
        e.getPresentation().setVisible(isTraceElement);
    }

    /**
     * Handles the action event for selecting Trace Metrics from the context menu
     * on a source code annotation, displaying the tool window with the metrics table
     * for the current project with the named metric row selected if possible, or the
     * first metric in the table otherwise, scrolling the selected row into visibility.
     *
     * @param e the AnActionEvent instance containing project context
     */
    @Override
    public void actionPerformed(final AnActionEvent e) {
        PsiElement element = PsiUtil.getElementAtOffset(
                e.getData(CommonDataKeys.PSI_FILE),
                e.getData(CommonDataKeys.CARET).getOffset());

        PsiAnnotation annotation = Utils.getTraceAnnotationParent(element);
        Trace trace = Trace.fromPsiAnnotation(annotation);

        Project myProject = Utils.getProject(e.getDataContext());

        final ToolWindow toolWindow = ToolWindowManager.getInstance(myProject).getToolWindow("Trace Metrics");

        toolWindow.show(new Runnable() {
            @Override
            public void run() {
                JTable table = Utils.getTable();

                TraceTableModel model = (TraceTableModel) table.getModel();
                int nRow = model.getRowCount();
                int index = 0;
                for (int i = 0 ; i < nRow ; i++) {
                    int modelIdx = table.convertRowIndexToModel(i);
                    if(model.getValueAt(modelIdx,1).equals(trace.getMetricName())) {
                        index = i;
                    }
                }

                table.setRowSelectionInterval(index, index);

                JViewport viewport = (JViewport)table.getParent();
                Rectangle rect = table.getCellRect(index, 0, true);
                Point pt = viewport.getViewPosition();
                rect.setLocation(rect.x-pt.x, rect.y-pt.y);

                table.scrollRectToVisible(rect);
            }
        });
    }
}